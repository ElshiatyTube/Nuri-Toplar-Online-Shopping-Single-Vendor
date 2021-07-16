package com.eatitappclient.tws;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.animation.ValueAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.MyCustomMarkerAdapter;
import com.eatitappclient.tws.Helper.LocalHelper;
import com.eatitappclient.tws.Model.ShippingOrderModel;
import com.eatitappclient.tws.Remote.IGoogleAPI;
import com.eatitappclient.tws.Remote.RetrofitGoogleAPIClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TrakingOrderActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;

    private Marker shipperMarker;

    private PolylineOptions polylineOptions,blackPolylineOptions;
    private List<LatLng> polylineList;
    private Polyline yellowPolyline,grayPolyline,blackPolyline;

    private IGoogleAPI iGoogleAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DatabaseReference shipperRef;

    //Move Marker
    private Handler handler;
    private int index,next;
    private LatLng start,end;
    private float v;
    private double lat,lng;

    private boolean isInit= false;


    @OnClick(R.id.btn_call)
    void onCallClick(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(new StringBuilder("tel:").append(Common.currentShippingOrder.getShipperPhone()).toString()));
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traking_order);

        ButterKnife.bind(this);

        iGoogleAPI = RetrofitGoogleAPIClient.getInstance().create(IGoogleAPI.class);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        subscribShipperMove();


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

    private void subscribShipperMove() {
        shipperRef = FirebaseDatabase.getInstance()
                .getReference(Common.SHIPPING_ORDER_REF)
                .child(Common.currentShippingOrder.getKey());

        shipperRef.addValueEventListener(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new MyCustomMarkerAdapter(getLayoutInflater()));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.uber_light_with_label));
            if (!success)
                Log.d("YOUSSEFDEV","Style parsing failed");
        }catch (Resources.NotFoundException ex){
            Log.d("YOUSSEFDEV","Resources not found");

        }


        drawRoutes();
    }

    private void drawRoutes() {
        LatLng locationOrder = new LatLng(Common.currentShippingOrder.getOrderModel().getLat(),
                Common.currentShippingOrder.getOrderModel().getLat());
        LatLng locationShipper = new LatLng(Common.currentShippingOrder.getCurrentLat(),Common.currentShippingOrder.getCurrentLng());

        //Add Box
      /*  mMap.addMarker(new MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
        .title(Common.currentShippingOrder.getOrderModel().getUserName())
        .snippet(Common.currentShippingOrder.getOrderModel().getShippingAddress())
        .position(locationOrder)); */


        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
                .title(Common.currentShippingOrder.getOrderModel().getUserName())
                .snippet(Common.currentShippingOrder.getOrderModel().getShippingAddress())
                .position(new LatLng(Common.currentShippingOrder.getOrderModel().getLat(),
                        Common.currentShippingOrder.getOrderModel().getLng())));


        //Add Shipper
        if (shipperMarker == null)
        {
            int height,width;
            height =width=80;
            BitmapDrawable bitmapDrawable =(BitmapDrawable) ContextCompat
                    .getDrawable(TrakingOrderActivity.this,R.drawable.shippernew);
            Bitmap resized = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(),width,height,false);

            shipperMarker =  mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(resized))
                    .title(new StringBuilder(getResources().getString(R.string.Shipper)).append(" ").append(Common.currentShippingOrder.getShippingName()).toString())
                    .snippet(new StringBuilder(getResources().getString(R.string.Phone)).append(" ").append(Common.currentShippingOrder.getShipperPhone())
                    .append("\n")
                    .append(getResources().getString(R.string.EstimateTime)).append(" ")
                    .append(Common.currentShippingOrder.getEstimateTime()).toString())
                    .position(locationShipper));

            //Allow Show Info
            shipperMarker.showInfoWindow();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,18));
        }
        else
        {
            shipperMarker.setPosition(locationShipper);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,18));
        }

        //Draw Route
        String to = new StringBuilder()
                .append(Common.currentShippingOrder.getOrderModel().getLat())
                .append(",")
                .append(Common.currentShippingOrder.getOrderModel().getLng())
                .toString();
        String from = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();



        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                from,to,
                getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = Common.decodePoly(polyline);
                        }

                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);
                        yellowPolyline = mMap.addPolyline(polylineOptions);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(this, ""+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Exceptionnn: ",ex.getMessage());
                    }

                }, throwable ->
                        //   Toast.makeText(ShippingActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show() ));
                        Log.d("Exception2: ",throwable.getMessage())));

    }

    @Override
    protected void onDestroy() {
        shipperRef.removeEventListener(this);
        isInit = false;
        super.onDestroy();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //Save old pos
        String from = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();
        //Update pos
        Common.currentShippingOrder = dataSnapshot.getValue(ShippingOrderModel.class);
        Common.currentShippingOrder.setKey(dataSnapshot.getKey());
        //Save new pos
        String to = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();
        if (dataSnapshot.exists())
        {
            if (isInit)
                moveMarkerAnimation(shipperMarker,from,to);
            else
                isInit=true;
        }
    }

    private void moveMarkerAnimation(Marker shipperMarker, String from, String to) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                from,to,
                getString(R.string.google_maps_key))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(returnResult ->{

            try {
                JSONObject jsonObject = new JSONObject(returnResult);
                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject route = jsonArray.getJSONObject(i);
                    JSONObject poly = route.getJSONObject("overview_polyline");
                    String polyline = poly.getString("points");
                    polylineList = Common.decodePoly(polyline);
                }

                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.GRAY);
                polylineOptions.width(12);
                polylineOptions.startCap(new SquareCap());
                polylineOptions.jointType(JointType.ROUND);
                polylineOptions.addAll(polylineList);
                grayPolyline = mMap.addPolyline(polylineOptions);


                blackPolylineOptions = new PolylineOptions();
                blackPolylineOptions.color(Color.BLACK);
                blackPolylineOptions.width(5);
                blackPolylineOptions.startCap(new SquareCap());
                blackPolylineOptions.jointType(JointType.ROUND);
                blackPolylineOptions.addAll(polylineList);
                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                //Animator
                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                polylineAnimator.setDuration(2000);
                polylineAnimator.setInterpolator(new LinearInterpolator());
                polylineAnimator.addUpdateListener(valueAnimator ->{
                    List<LatLng> points = grayPolyline.getPoints();
                    int percentValue =(int)valueAnimator.getAnimatedValue();
                    int size =points.size();
                    int newPoints =(int)(size*(percentValue/100.0f));
                    List<LatLng> p = points.subList(0,newPoints);
                    blackPolyline.setPoints(p);
                });
                polylineAnimator.start();

                //Bike moving
                handler = new Handler();
                index = -1;
                next = 1;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (index < polylineList.size()-1)
                        {
                            index++;
                            next = index+1;
                            start =polylineList.get(index);
                            end = polylineList.get(next);
                        }
                        //Animator
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                        valueAnimator.setDuration(1500);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.addUpdateListener(valueAnimator1 ->{
                            v = valueAnimator1.getAnimatedFraction();
                            lng = v*end.longitude+(1-v)
                                    *start.longitude;
                            lat = v*end.latitude+(1-v)
                                    *start.latitude;
                            LatLng newPos = new LatLng(lat,lng);
                            shipperMarker.setPosition(newPos);
                            shipperMarker.setAnchor(0.5f,0.5f);
                            shipperMarker.setRotation(Common.getBearing(start,newPos));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                        });
                        valueAnimator.start();
                        if (index < polylineList.size() -2) //Rech destionation
                            handler.postDelayed(this,1500);


                    }
                },1500);

            }
            catch (Exception ex)
            {
                Toast.makeText(this, ""+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Exceptionnn: ",ex.getMessage());
            }


        },throwable -> {
            Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }));

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
