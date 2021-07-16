package com.eatitappclient.tws;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.andremion.counterfab.CounterFab;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Database.CartDataSource;
import com.eatitappclient.tws.Database.CartDatabase;
import com.eatitappclient.tws.Database.LocalCartDataSource;
import com.eatitappclient.tws.EventBus.BestDealItemClick;
import com.eatitappclient.tws.EventBus.BestDealsItemClick;
import com.eatitappclient.tws.EventBus.CaregoryClick;
import com.eatitappclient.tws.EventBus.ChangeMenuClick;
import com.eatitappclient.tws.EventBus.CounterCartEvent;
import com.eatitappclient.tws.EventBus.FoodItemClick;
import com.eatitappclient.tws.EventBus.GoToOrdersEvent;
import com.eatitappclient.tws.EventBus.HideFABCart;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.EventBus.PopularCatergoryClick;
import com.eatitappclient.tws.Helper.LocalHelper;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.Model.UserModel;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;

    private CartDataSource cartDataSource;

    android.app.AlertDialog dialog;

    int menuClickId = -1;


    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    private TextView text;

    private ArrayAdapter<String> adapter;
    private List<String> liste;

    @BindView(R.id.fab)
    CounterFab fab;

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    Configuration configuration;
    FragmentManager manager = getFragmentManager();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.onAttach(newBase,"en"));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
    }
    public  static PackageManager packageManager;
   public static NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initPlaceClient();

        dialog=new SpotsDialog.Builder().setContext(this).setMessage(getResources().getString(R.string.loading)).setCancelable(true).build();
        packageManager = this.getPackageManager();

        ButterKnife.bind(this);

        cartDataSource=new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

      //  countCartItem();
        langchosperf();
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_cart);

           //     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
             //           .setAction("Action", null).show();
            }
        });
         drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_view_orders, R.id.nav_cart, R.id.nav_food_list, R.id.nav_branches, R.id.nav_offers,R.id.nav_bestDeals_detail)
                .setDrawerLayout(drawer)
                .build();
         navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        View headerView =navigationView.getHeaderView(0);
        TextView txt_user = (TextView)headerView.findViewById(R.id.txt_user);
        Common.setSpanString(getResources().getString(R.string.Hey)+" ",Common.currentUser.getName(),txt_user);
        CheckPermission();

        countCartItem();
        subscribeNewsFirstTime();
        checkIsOpenFromActivity();
        checkIsOpenNormalNotify();
        Common.getOpenStatue(this);
        menuClickId  =R.id.nav_home;


        Common.getOffersStatue();
    }
    public static void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_offers).setVisible(false);
    }

    public  void langchosperf(){
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);//this==context
        if(!prefs.contains("FirstTime22")) {
            //Other dialog code

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("FirstTime22",true);
            editor.commit();


            Fraglang dialoggg = new Fraglang();

            dialoggg.show(manager, "dialog");

        }


    }

    private void initPlaceClient() {
        Places.initialize(this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
    }

    private void checkIsOpenFromActivity() {
        boolean isOpenFromNewOrder = getIntent().getBooleanExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER,false);
        if (isOpenFromNewOrder)
        {
            navController.popBackStack();
            navController.navigate(R.id.nav_view_orders);
            menuClickId = R.id.nav_view_orders;
        }
    }
    private void checkIsOpenNormalNotify() {
        boolean isOpenFromNewOrder = getIntent().getBooleanExtra(Common.IS_OPEN_ACTIVITY_NORMAL_NOTIFY,false);
        if (isOpenFromNewOrder)
        {
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
            menuClickId = R.id.nav_view_orders;
        }
    }




    private void CheckPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    } */

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setCheckable(true);
        drawer.closeDrawers();
        switch (item.getItemId())
        {
            case R.id.nav_home:
                if (item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_home);
                break;

            case R.id.nav_branches:
                if (item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_branches);
                break;

            case R.id.nav_menu:
                if (item.getItemId() != menuClickId)
                   navController.navigate(R.id.nav_menu);
                break;
            case R.id.nav_cart:
                if (item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_cart);
                break;
            case R.id.nav_view_orders:
                if (item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_view_orders);
                break;

            case R.id.nav_offers:
                if (item.getItemId() != menuClickId)
                    navController.navigate(R.id.nav_offers);
                break;

            case R.id.nav_sign_out:
                signOut();
                break;
            case R.id.nav_update_info:
                showUpdateInfoDialog();
                break;

            case R.id.nav_language:{
                Fraglang dialoggg = new Fraglang();
                dialoggg.show(manager, "dialog");
            }
            break;

            case R.id.nav_call:
                fragPhonEmail dialoggg = new fragPhonEmail();
                dialoggg.show(manager, "dialog");
                break;
            case R.id.nav_aboutUs:{
                editAboutUs();
            }
            break;



            case R.id.nav_news:
                showSubscribeNews();
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.ShareInvite)+"  https://play.google.com/store/apps/details?id=com.NuriToplar.client");
                startActivity(Intent.createChooser(shareIntent, "Share App using"));
                break;

            case R.id.nav_rate:
                Uri urii = Uri.parse("https://play.google.com/store/apps/details?id=com.NuriToplar.client");
                Intent intenttt = new Intent(Intent.ACTION_VIEW, urii);
                startActivity(intenttt);
                break;

        }
        menuClickId=item.getItemId();
        return true;
    }




    private void editAboutUs() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.aboutText));
        alertDialog.setCancelable(true);
        alertDialog.setIcon(getResources().getDrawable(R.drawable.ic_format_quote_black_24dp));

        getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);

        text = new TextView(this);
        text.setTextSize(20f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        text.setLayoutParams(lp);
        alertDialog.setView(text); // uncomment this line

        getAboutUsData();

        alertDialog.show();
    }

    private void getAboutUsData(){
        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("about")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            text.setText(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void showSubscribeNews() {
       /* Paper.init(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp));

        builder.setTitle(getResources().getString(R.string.NewsSystem));
        builder.setMessage(getResources().getString(R.string.SubscribeQuest));

        View itemView= LayoutInflater.from(this).inflate(R.layout.layout_subscribe_news,null);
        CheckBox ckb_news=(CheckBox)itemView.findViewById(R.id.ckb_subscribe_news);
        boolean isSubscribeNews = Paper.book().read(Common.IS_SUBSCRIBE_NEWS,false);
        if (isSubscribeNews)
            ckb_news.setChecked(true);

        builder.setPositiveButton(getResources().getString(R.string.SEND), (dialog, which) -> {
            if (ckb_news.isChecked())
            {
                Paper.book().write(Common.IS_SUBSCRIBE_NEWS,true);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(Common.NEWS_TOPIC)
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getResources().getString(R.string.SubscribeToast), Toast.LENGTH_SHORT).show();
                });
            }
            else
            {
                Paper.book().delete(Common.IS_SUBSCRIBE_NEWS);

                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(Common.NEWS_TOPIC)
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getResources().getString(R.string.UnSubscribeToast), Toast.LENGTH_SHORT).show();
                });
            }
        });
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show(); */



        final Intent notificationSettingsIntent = new Intent();
        notificationSettingsIntent
                .setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        notificationSettingsIntent
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationSettingsIntent.putExtra(
                    "android.provider.extra.APP_PACKAGE",
                    this.getPackageName());
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationSettingsIntent.putExtra(
                    "app_package",
                    this.getPackageName());
            notificationSettingsIntent.putExtra(
                    "app_uid",
                    this.getApplicationInfo().uid);
        }
        try {
            this.startActivityForResult(
                    notificationSettingsIntent,
                    1);
        }catch (Exception E){
            Toast.makeText(this, "No need", Toast.LENGTH_SHORT).show();
        }


    }
    private void subscribeNewsFirstTime(){
        Paper.init(this);

        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);//this==context
        if(!prefs.contains("FirstTime222")){
            //Other dialog code

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("FirstTime222",true);
            editor.commit();

            Paper.book().write(Common.IS_SUBSCRIBE_NEWS,true);
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(Common.NEWS_TOPIC)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(aVoid -> {
                // Toast.makeText(this, getResources().getString(R.string.SubscribeToast), Toast.LENGTH_SHORT).show();
            });


        }
    }


    private void showUpdateInfoDialog() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.updateinfo));
        builder.setCancelable(false);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_update_orange_24dp));


        View itemView= LayoutInflater.from(this).inflate(R.layout.layout_register,null);


        final EditText edt_name=(EditText)itemView.findViewById(R.id.edt_name);
        final EditText edt_address=(EditText) itemView.findViewById(R.id.txt_address_detail);
        final EditText edt_phone=(EditText)itemView.findViewById(R.id.edt_phonee);

       /* places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragmentt);
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                edt_address.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(HomeActivity.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        }); */


        //Set DAta
        edt_name.setText(Common.currentUser.getName());
        edt_address.setText(Common.currentUser.getAddress());
        edt_phone.setText(Common.currentUser.getPhone());

        builder.setView(itemView);
        builder.setNegativeButton(getResources().getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
            //   if (placeSelected != null)
            //   {
                   if(TextUtils.isEmpty(edt_name.getText().toString()))
                   {
                       Toast.makeText(HomeActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                       return;
                   }
                    if(TextUtils.isEmpty(edt_address.getText().toString()))
                    {
                        Toast.makeText(HomeActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(edt_phone.getText().toString()))
                    {
                        Toast.makeText(HomeActivity.this,getResources().getString(R.string.CantEmpty),Toast.LENGTH_SHORT).show();
                        return;
                    }
                   Map<String,Object> update_data = new HashMap<>();
                   update_data.put("name",edt_name.getText().toString());
                   update_data.put("address",edt_address.getText().toString());
                   update_data.put("phone",edt_phone.getText().toString());

              /*     if (placeSelected != null) {
                       update_data.put("lat", placeSelected.getLatLng().latitude);
                       update_data.put("lng", placeSelected.getLatLng().longitude);
                   } */

                   FirebaseDatabase.getInstance()
                           .getReference(Common.USER_REFFERENCES)
                           .child(Common.currentUser.getUid())
                           .updateChildren(update_data)
                           .addOnFailureListener(e -> {
                               dialog.dismiss();
                               Toast.makeText(HomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                           })
                           .addOnSuccessListener(aVoid -> {
                               dialog.dismiss();
                               Toast.makeText(HomeActivity.this, getResources().getString(R.string.updateinfoSuccess), Toast.LENGTH_SHORT).show();
                               Common.currentUser.setName(update_data.get("name").toString());
                               Common.currentUser.setAddress(update_data.get("address").toString());
                               Common.currentUser.setPhone(update_data.get("phone").toString());

                              /* if (placeSelected != null) {
                                   Common.currentUser.setLat(Double.parseDouble(update_data.get("lat").toString()));
                                   Common.currentUser.setLng(Double.parseDouble(update_data.get("lng").toString()));
                               } */

                           });


            //   }
             //  else {
             //      Toast.makeText(HomeActivity.this, getResources().getString(R.string.Pleaseaddress), Toast.LENGTH_SHORT).show();
            //   }

            }
        });

        builder.setView(itemView);

        android.app.AlertDialog dialog=builder.create();
       /* dialog.setOnDismissListener(dialogInterface -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
         //   fragmentTransaction.remove(places_fragment);
            fragmentTransaction.commit();
        });  */
        dialog.show();
    }

    private void signOut() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp));

        builder.setTitle(getResources().getString(R.string.SignOut))
                .setMessage(getResources().getString(R.string.SignOutMSG))
                .setNegativeButton(getResources().getString(R.string.CANCELLL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getResources().getString(R.string.YESS), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.selectedFood= null;
                Common.categroySelected = null;
                Common.currentUser = null;
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //Event Bus

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents();

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CaregoryClick event)
    {
        if (event.isSuccess())
        {
            navController.navigate(R.id.nav_food_list);
            //Toast.makeText(this, "Click to "+event.getCategoryModel().getMenu_id(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onGoToOrder(GoToOrdersEvent event)
    {
        if (event.isSuccess())
        {
            navController.navigate(R.id.nav_view_orders);
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onFoodItemClick(FoodItemClick event)
    {
        if (event.isSuccess())
        {
            navController.navigate(R.id.nav_food_detail);
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onFoodItemClick(BestDealsItemClick event)
    {
        if (event.isSuccess())
        {
            navController.navigate(R.id.nav_bestDeals_detail);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABCart event)
    {
        if (event.isHidden())
        {
            fab.hide();
        }
        else
            fab.show();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event)
    {
        if (event.isSuccess())
        {
            countCartItem();
        }
    }

  /*  @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick(BestDealItemClick event)
    {
        if (event.getBestDealModel() != null)
        {
            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getBestDealModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                Common.categroySelected =dataSnapshot.getValue(CategoryModel.class);
                                Common.categroySelected.setMenu_id(dataSnapshot.getKey());

                                //Load foof
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getBestDealModel().getMenu_id())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getBestDealModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                {
                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                                                    {
                                                        Common.selectedFood =itemSnapShot.getValue(FoodModel.class);
                                                    Common.selectedFood.setKey(itemSnapShot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_food_detail);
                                                }
                                                else
                                                {

                                                    Toast.makeText(HomeActivity.this, "Item doesn't exists!", Toast.LENGTH_SHORT).show();

                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Item doesn't exists!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    } */


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPopularItemClick(PopularCatergoryClick event)
    {
        if (event.getPopularCategoryModel() != null)
        {
            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getPopularCategoryModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                Common.categroySelected =dataSnapshot.getValue(CategoryModel.class);
                                Common.categroySelected.setMenu_id(dataSnapshot.getKey());

                                //Load foof
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getPopularCategoryModel().getMenu_id())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getPopularCategoryModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                {
                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                                                    {
                                                        Common.selectedFood =itemSnapShot.getValue(FoodModel.class);
                                                        Common.selectedFood.setKey(itemSnapShot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_food_detail);
                                                }
                                                else
                                                {

                                                    Toast.makeText(HomeActivity.this, "Item doesn't exists!", Toast.LENGTH_SHORT).show();

                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Item doesn't exists!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


    private void countCartItem() {
        cartDataSource.countItemInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        fab.setCount(integer);

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                        {
                            Toast.makeText(HomeActivity.this, "{COUNT CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                            fab.setCount(0);

                    }
                });

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void countCartAgain(CounterCartEvent event)
    {
        if (event.isSuccess())
            countCartItem();
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMenuItemBack(MenuItemback event)
    {
      menuClickId= -1;
     // navController.popBackStack(R.id.nav_home,true);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategoryMenuClick(ChangeMenuClick event)
    {
        if (event.isFromFoodList())
        {
            navController.popBackStack(R.id.nav_menu,true);
            navController.navigate(R.id.nav_menu);
        }
        menuClickId = -1;
    }

}
