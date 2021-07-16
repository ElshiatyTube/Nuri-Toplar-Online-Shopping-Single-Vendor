package com.eatitappclient.tws;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.eatitappclient.tws.Adapter.ChoiseLanguageAdapter;
import com.eatitappclient.tws.Helper.LocalHelper;

import java.util.List;

import io.paperdb.Paper;

public class Fraglang extends DialogFragment {
    public static String va;

    List<String> list;
    int[] imageId = {
            R.drawable.arabiclangunited, //9
            R.drawable.englishlang, //9



    };
    int[] web = {
            R.string.arabic, //0
            R.string.english, //1





    } ;

    ListView mLocationList;
    //  private InterstitialAd interstitialAd;


    ImageView iv_icon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // this.getDialog().setTitle(R.string.ChooseLanguage);

        View v = inflater.inflate(R.layout.fragcontact, container,
                true);

        //     AdView adView = new AdView(this.getActivity());
        //  adView.setAdSize(AdSize.BANNER);
        //   adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        //   mAdView = v.findViewById(R.id.adView);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }
        mLocationList = (ListView)v.findViewById(R.id.listView1);
        iv_icon=(ImageView)v.findViewById(R.id.imageView1);
        //  iv_icon.setImageResource(R.drawable.glutenhead);
        iv_icon.setVisibility(View.GONE);
        //    admobbanner();


        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        //  btn.setOnClickListener(this);
        ChoiseLanguageAdapter adapter = new ChoiseLanguageAdapter(this.getActivity(), web,
                imageId);

        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(this.getActivity(), R.anim.frombutton);

        mLocationList.setLayoutAnimation(controller);

        mLocationList.deferNotifyDataSetChanged();


        mLocationList.setAdapter(adapter);
        mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,  int position, long id) {

                if (position==0){
                    Paper.book().write("language","ar");
                    updateView((String) Paper.book().read("language"));
                    restartApp();
                    dismiss();
                    //MainActivity.numm=1;
                }
                if (position==1){
                    Paper.book().write("language","en");
                    updateView((String) Paper.book().read("language"));

                    restartApp();
                    dismiss();
                    // MainActivity.numm=2;
                }



            }
        });




        return v;
    }
    private void restartFirstActivity()
    {
        Intent i = getActivity().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }
    public void reload() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }
    private void restartApp() {

        va="1";

        Intent intent = new Intent(getActivity(), MainActivity.class);
        //intent.putExtra("lang",lang);
        this.startActivity(intent);
        getActivity().finishAffinity();
        //startActivity(ii);



       // Intent intent =  new Intent(getActivity(), MainActivity.class);
     //  getActivity().finish();
       // startActivity(intent);



    }

    private void updateView(String lang) {
        Context context= LocalHelper.setLocale(getActivity(),lang);
        Resources resources=context.getResources();


    }








}

