package com.eatitappclient.tws;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.eatitappclient.tws.Adapter.ImageAdapter2;
import com.eatitappclient.tws.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public class fragPhonEmail extends DialogFragment {

    int[] imageId = {

            R.drawable.callred, //9
           R.drawable.whatsapp, //2
            R.drawable.messengerr //2



    };
    int[] web= {
              R.string.calnow,
            R.string.wats,
            R.string.messenger,


    } ;


    ListView mLocationList;
    String phonenum = "+201067036006";
    String call = "+201067036006";


    ImageView iv_icon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragcontact, container,
                true);


        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
       mLocationList = (ListView)v.findViewById(R.id.listView1);
        iv_icon=(ImageView)v.findViewById(R.id.imageView1);
        iv_icon.setVisibility(View.GONE);

        ImageAdapter2 adapter = new ImageAdapter2(this.getActivity(), web,
                imageId);

        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(this.getActivity(), R.anim.frombutton);

        mLocationList.setLayoutAnimation(controller);

        mLocationList.deferNotifyDataSetChanged();


        mLocationList.setAdapter(adapter);
        mLocationList.setOnItemClickListener((adapterView, view, position, id) -> {

              if (position==0){
                  getCallNumberFormFirebase();
               }

            if (position==1){


                getWhtasNumberFormFirebase();


            }

            if (position==2){


                getMessengerNumberFormFirebase();


            }




        });



        return v;
    }

    private void getMessengerNumberFormFirebase() {
        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("messenger")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.getValue()!=null)
                            {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setPackage("com.facebook.orca");
                                intent.setData(Uri.parse("https://"+dataSnapshot.getValue(String.class)));
                                try
                                {
                                    startActivity(intent);
                                }catch (Exception e ){
                                    Toast.makeText(getActivity(),"Sorry you should download Facebook Messenger first :)",Toast.LENGTH_SHORT).show();}

                            }
                            else {
                                Toast.makeText(getActivity(), "No Number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




    }

    private void getCallNumberFormFirebase() {
        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("phone")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.getValue()!=null) {
                                dialContactPhone(dataSnapshot.getValue(String.class));
                            }
                            else {
                                Toast.makeText(getActivity(), "No Number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getWhtasNumberFormFirebase() {
        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("whatsapp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.getValue()!=null)
                            {
                                Intent i = new Intent(Intent.ACTION_VIEW);

                                try {
                                    String url = "https://api.whatsapp.com/send?phone="+ dataSnapshot.getValue(String.class)  +"&text=" + URLEncoder.encode(getResources().getString(R.string.FromRokn), "UTF-8");
                                    i.setPackage("com.whatsapp");
                                    i.setData(Uri.parse(url));
                                    if (i.resolveActivity(HomeActivity.packageManager) != null) {
                                        startActivity(i);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                            else {
                                Toast.makeText(getActivity(), "No Number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

}

