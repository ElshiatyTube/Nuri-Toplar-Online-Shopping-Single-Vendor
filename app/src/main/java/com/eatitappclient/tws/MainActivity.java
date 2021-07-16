package com.eatitappclient.tws;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.accounts.Account;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Helper.LocalHelper;
import com.eatitappclient.tws.Model.UserModel;
import com.eatitappclient.tws.Retrofit.ICloudFunctions;
import com.eatitappclient.tws.Retrofit.RetrofitClient;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {


    private static final int MY_REQUEST_CODE = 7177;
    private static int APP_REQUEST_CODE=7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private ICloudFunctions cloudFunctions;

    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;

 /*   private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG); */

    Configuration configuration;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.onAttach(newBase,"en"));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener!=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.e("newToken", newToken);
                        MainActivity.this.getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
                    }
                });

        Log.d("newToken", MainActivity.this.getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));

        init();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        } */

      //  showSignInOption();

        configuration = getResources().getConfiguration();

        if (Locale.getDefault().getLanguage().equals("ar")) {

            configuration.setLayoutDirection(new Locale("ar"));
            configuration.locale = new Locale("ar");
            onConfigurationChanged(configuration);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }
        else if (Locale.getDefault().getLanguage().equals("en"))
        {

            configuration.setLayoutDirection(new Locale("en"));
            configuration.locale = Locale.ENGLISH;
            onConfigurationChanged(configuration);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }
        else
        {
            configuration.setLayoutDirection(new Locale("en"));
            configuration.locale = Locale.ENGLISH;
            onConfigurationChanged(configuration);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }

        //Initioalize Paper first
        Paper.init(this);
    }
    private void showSignInOption(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.splshlogoo)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers).build(),APP_REQUEST_CODE);

    }

    private void init() {
       // Places.initialize(this,getString(R.string.google_maps_key));
       // placesClient = Places.createClient(this);


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();

        providers= Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso(countryCode).build(),
                new AuthUI.IdpConfig.EmailBuilder().build());


        userRef= FirebaseDatabase.getInstance().getReference(Common.USER_REFFERENCES);
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new SpotsDialog.Builder().setCancelable(false).setMessage(getResources().getString(R.string.loading)).setContext(this).build();
//        cloudFunctions= RetrofitClient.getInstance().create(ICloudFunctions.class);
        listener= firebaseAuth -> {

            FirebaseUser user=firebaseAuth.getCurrentUser();
            if(user!=null)
            {
                checkUserFromFirebase(user);
            }
            else {

                showSignInOption();
            }
        };
    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.ic_account_circle_black_24dp)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers).build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==APP_REQUEST_CODE)
        {
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user=firebaseAuth.getCurrentUser();
               // Toast.makeText(this, ""+user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
              //  goToHomeActivity();
                if(user!=null)
                {
                    checkUserFromFirebase(user);
                }

            }
            else
            {
               // Toast.makeText(this, "Failed to sign in!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUserFromFirebase(final FirebaseUser user)
    {
        dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                           // Toast.makeText(MainActivity.this, "Ypu Alraedy Register", Toast.LENGTH_SHORT).show();
                            UserModel userModel=dataSnapshot.getValue(UserModel.class);
                            goToHomeActivity(userModel);
                        }
                        else {
                            showRegisterDialog(user);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void showRegisterDialog(final FirebaseUser user){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.Register));
        builder.setCancelable(false);

        builder.setMessage(getResources().getString(R.string.Pleasefillinformation));

        builder.setIcon(getResources().getDrawable(R.drawable.ic_account_box_orange_24dp));

        View itemView= LayoutInflater.from(this).inflate(R.layout.layout_register,null);


        final EditText edt_name=(EditText)itemView.findViewById(R.id.edt_name);
        final EditText edt_address=(EditText) itemView.findViewById(R.id.txt_address_detail);
        final EditText edt_phone=(EditText)itemView.findViewById(R.id.edt_phonee);



        edt_phone.setText(user.getPhoneNumber());

        builder.setView(itemView);
        builder.setNegativeButton(getResources().getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.Register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if(TextUtils.isEmpty(edt_name.getText().toString()))
                {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edt_address.getText().toString()))
                {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edt_phone.getText().toString()))
                {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                    return;
                }

                    final UserModel userModel = new UserModel();
                    userModel.setUid(user.getUid());
                    userModel.setName(edt_name.getText().toString());
                    userModel.setAddress(edt_address.getText().toString());
                    userModel.setPhone(edt_phone.getText().toString());


                    userRef.child(user.getUid()).setValue(userModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.RegisterSuccessfully), Toast.LENGTH_SHORT).show();
                                        goToHomeActivity(userModel);

                                    }
                                }
                            });
                }


        });

        builder.setView(itemView);

        AlertDialog dialog=builder.create();
        /*   dialog.setOnDismissListener(dialogInterface -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
          //  fragmentTransaction.remove(places_fragment);
            fragmentTransaction.commit();
        });  */
        dialog.show();
    }

    private void goToHomeActivity(UserModel userModel) {

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    Common.currentUser=userModel;
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();

                }).addOnCompleteListener(task -> {
                    Common.currentUser=userModel;
                   //Common.updateToken(this,task.g);


            //Mashkok
            Intent intent=new Intent(this,HomeActivity.class);
            intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER,getIntent().getBooleanExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER,false));
            intent.putExtra(Common.IS_OPEN_ACTIVITY_NORMAL_NOTIFY,getIntent().getBooleanExtra(Common.IS_OPEN_ACTIVITY_NORMAL_NOTIFY,false));
            startActivity(intent);
            //startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();


                }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (Common.currentUser.getUid() != null)
                {
                    Common.updateToken(MainActivity.this,instanceIdResult.getToken());
                }
            }
        });


    }

}
