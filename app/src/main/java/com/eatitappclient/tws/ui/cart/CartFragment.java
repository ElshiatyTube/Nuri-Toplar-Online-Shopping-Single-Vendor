package com.eatitappclient.tws.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.eatitappclient.tws.Adapter.MyCartAdapter;
import com.eatitappclient.tws.Callback.ILoadTimeFromFirebaseListener;
import com.eatitappclient.tws.Callback.ISearchFoodCallbackListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.MySwipHelper;
import com.eatitappclient.tws.Database.CartDataSource;
import com.eatitappclient.tws.Database.CartDatabase;
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Database.LocalCartDataSource;
import com.eatitappclient.tws.EventBus.CounterCartEvent;
import com.eatitappclient.tws.EventBus.GoToOrdersEvent;
import com.eatitappclient.tws.EventBus.HideFABCart;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.EventBus.UpdateItemInCart;
import com.eatitappclient.tws.HomeActivity;
import com.eatitappclient.tws.MainActivity;
import com.eatitappclient.tws.Model.AddonModel;
import com.eatitappclient.tws.Model.BranchesModel;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.CommentModel;
import com.eatitappclient.tws.Model.FCMResponse;
import com.eatitappclient.tws.Model.FCMSendData;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.Model.Order;
import com.eatitappclient.tws.Model.SizeModel;
import com.eatitappclient.tws.R;
import com.eatitappclient.tws.Remote.IFCMService;
import com.eatitappclient.tws.Remote.RetrofitFCMClient;
import com.eatitappclient.tws.Retrofit.ICloudFunctions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener, ISearchFoodCallbackListener, MaterialSpinner.OnItemSelectedListener {


    private BottomSheetDialog addonBottomSheetDialog;
    private ChipGroup chip_group_addon,chip_group_user_selected_addon;
   // private EditText edt_search;


    private ISearchFoodCallbackListener searchFoodCallbackListener;
    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private LinearLayout google_place_frag;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private Parcelable recyclerViewState;
    private CartDataSource cartDataSource;

    AlertDialog spotsDialog;


    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    ICloudFunctions cloudFunctions;
    IFCMService ifcmService;
    ILoadTimeFromFirebaseListener listener;

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;

    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;

    ArrayAdapter<String > adapterSpinner;
    List<String> spinnerDataList;
    ValueEventListener listenerSpinner;

    MaterialSpinner mySpinner;

    TextView txt_total_price_delevriy;
    List<Integer> numbers,numbers2;
    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick(){
        if(!Common.RESTURANT_STATU.equals("close")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.stepmore));
            builder.setCancelable(false);
            builder.setIcon(getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));

            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);




             txt_total_price_delevriy = (TextView) view.findViewById(R.id.txt_total_price_delevriy);

            //EditText edt_address=(EditText)view.findViewById(R.id.edt_address);
            EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
            EditText txt_address = (EditText) view.findViewById(R.id.txt_address_detail);

            RadioButton rdi_home = (RadioButton) view.findViewById(R.id.rdi_home_address);
            RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
            RadioButton rdi_ship_to_this = (RadioButton) view.findViewById(R.id.rdi_ship_this_address);
            RadioButton rdi_cod = (RadioButton) view.findViewById(R.id.rdi_cod);
            RadioButton rdi_braintree = (RadioButton) view.findViewById(R.id.rdi_braintree);

            google_place_frag = (LinearLayout) view.findViewById(R.id.google_place_frag);
            places_fragment = (AutocompleteSupportFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.places_autocomplete_fragment);
            places_fragment.setPlaceFields(placeFields);
            places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    placeSelected = place;
                    // txt_address.setText(place.getAddress());
                    //place.getLatLng();

                    //  currentLocation=null;
                    Common.PLACE_LAT = place.getLatLng().latitude;
                    Common.PLACE_LNG = place.getLatLng().longitude;
                    Log.d("latitudePlace", String.valueOf(Common.PLACE_LAT));
                    Log.d("longitudePlace", String.valueOf(Common.PLACE_LNG));
                    Log.d("LatLngPlace", String.valueOf(place.getLatLng()));

                    txt_address.setHint(getResources().getString(R.string.Enteraddress2));


                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(getContext(), getResources().getString(R.string.ToaseCantEmptyPlaceAddress), Toast.LENGTH_SHORT).show();

                }
            });


             mySpinner = (MaterialSpinner) view.findViewById(R.id.mySpinner);
            spinnerDataList=new ArrayList<>();
            spinnerDataList.add(getResources().getString(R.string.pleaseselectbranch));
            adapterSpinner=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,spinnerDataList);
            getBranchesName();




          //  mySpinner.setAdapter(adapterSpinner);
            mySpinner.setOnItemSelectedListener(this);


//            String text = mySpinner.getSelectedItem().toString();



            //Data
            txt_address.setText(Common.currentUser.getAddress());


            //Event
            rdi_home.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    google_place_frag.setVisibility(View.GONE);
                    txt_address.setText(Common.currentUser.getAddress());
                    txt_address.setVisibility(View.VISIBLE);
                }
            });
            rdi_other_address.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    //  txt_address.setText("");
                    //txt_address.setHint("Enter your address");
                    google_place_frag.setVisibility(View.VISIBLE);
                    places_fragment.setHint(getResources().getString(R.string.NearestLocation));
                    txt_address.setText("");
                  //  txt_address.setHint(getResources().getString(R.string.Enteraddress2));
                    txt_address.setVisibility(View.VISIBLE);
                }
            });
            rdi_ship_to_this.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    google_place_frag.setVisibility(View.VISIBLE);
                    fusedLocationProviderClient.getLastLocation()
                            .addOnFailureListener(e -> {
                                //  Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("ERRR!", e.getMessage());
                                txt_address.setVisibility(View.GONE);
                            })
                            .addOnCompleteListener(task -> {
                                String coordinates = new StringBuilder()
                                        .append(task.getResult().getLatitude())
                                        .append("/")
                                        .append(task.getResult().getLongitude()).toString();

                                Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(),
                                        task.getResult().getLongitude()));

                                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        //  edt_address.setText(coordinates);
                                        txt_address.setText(s);
                                        txt_address.setVisibility(View.VISIBLE);
                                        places_fragment.setHint(s);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        //  edt_address.setText(coordinates);
                                        txt_address.setText("Error to get Location: " + e.getMessage());
                                        txt_address.setVisibility(View.VISIBLE);
                                    }
                                });


                            });
                }
            });

            builder.setView(view);
            builder.setNegativeButton(getResources().getString(R.string.CANCEL), (dialog, which) -> {
                dialog.dismiss();

            });
            builder.setPositiveButton(getResources().getString(R.string.Confirmm), (dialog, which) -> {

                if(TextUtils.isEmpty(txt_address.getText().toString()))
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.ToaseCantEmptyAddress),Toast.LENGTH_LONG).show();
                    return;
                }
                if(rdi_cod.isChecked() && rdi_other_address.isChecked() && Common.PLACE_LAT==0 && Common.PLACE_LNG==0)
                {
                    Toast.makeText(getContext(),getResources().getString(R.string.ToaseCantEmptyPlaceAddress),Toast.LENGTH_LONG).show();
                    return;
                }
                    if(txt_total_price_delevriy.getVisibility()==View.GONE)
                    {
                        Toast.makeText(getContext(),getResources().getString(R.string.Youbranch),Toast.LENGTH_LONG).show();
                        return;
                    }

              //  if(!TextUtils.isEmpty(txt_address.getText().toString()) && !rdi_cod.isChecked() && !rdi_other_address.isChecked() && Common.PLACE_LAT!=0 && Common.PLACE_LNG!=0
               // && txt_total_price_delevriy.getVisibility()!=View.GONE)
               // {
                    if (rdi_cod.isChecked() && rdi_home.isChecked())
                        paymentCOD(txt_address.getText().toString(), edt_comment.getText().toString());
                    else if (rdi_cod.isChecked() && rdi_other_address.isChecked())
                        paymentCODPlaceLoc(txt_address.getText().toString(), edt_comment.getText().toString());

                //}
                //else {

               // }






            });

            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(dialogInterface -> {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(places_fragment);
                fragmentTransaction.commit();
            });
            dialog.show();
        }
        else {
            Common.CloseDialog(getContext());
        }
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        if(position > 0)
        {
            //spinnerDataList.remove(getResources().getString(R.string.pleaseselectbranch));

            txt_total_price_delevriy.setVisibility(View.VISIBLE);
           // Toast.makeText(getContext(), "pos: "+item, Toast.LENGTH_SHORT).show();
            Common.SELECTED_BRANCH=item.toString();
            extractNumbers(item.toString());
            extractNumbers2(txt_total_price.getText().toString());
            txt_total_price_delevriy.setText(new StringBuilder(getResources().getString(R.string.Total)).append(" ")
                    .append(String.valueOf(numbers2.get(0)+numbers.get(0))).append(getContext().getResources().getString(R.string.currancy)));

        }
        else {
            txt_total_price_delevriy.setVisibility(View.GONE);
        }

    }

    public List<Integer> extractNumbers2(String s){
        numbers = new ArrayList<Integer>();

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);

        while(m.find()){
            numbers.add(Integer.parseInt(m.group()));
        }
        Log.d("num: ",numbers.get(0).toString());
        return numbers;

    }

    public List<Integer> extractNumbers(String s){
        numbers2 = new ArrayList<Integer>();

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);

        while(m.find()){
            numbers2.add(Integer.parseInt(m.group()));
        }
        Log.d("num: ",numbers2.get(0).toString());
        return numbers2;

    }




    private void getBranchesName(){
        List<BranchesModel> resultList = new ArrayList<>();

        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference(Common.BRANCHES_REF);
        zonesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnap:dataSnapshot.getChildren())
                {
                    BranchesModel branchesModel=itemSnap.getValue(BranchesModel.class);
                    resultList.add(branchesModel);
                    spinnerDataList.add(branchesModel.getBranchName()+" + "+branchesModel.getBranchDeliveryPrice()+getResources().getString(
                            R.string.currancy
                    ));
                    Log.d("newwwd",itemSnap.child("branchName").getValue(String.class));
                    Log.d("branchDeliveryPrice ",branchesModel.getBranchDeliveryPrice().toString());
                    Common.DELVPRICE=branchesModel.getBranchDeliveryPrice().toString();

                    mySpinner.setItems(spinnerDataList);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }





    private void paymentCODPlaceLoc(String address, String comment) {

        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    //When We have all Cart Item <we get total price
                    cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Double>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Double totalPrice) {
                                    double finalPrice=totalPrice; //for descount lat imp
                                    Order order=new Order();
                                    order.setUserId(Common.currentUser.getUid());
                                    order.setUserName(Common.currentUser.getName());
                                    order.setUserPhone(Common.currentUser.getPhone());
                                    order.setShippingAddress(address);
                                    order.setComment(comment);

                                    if (Common.PLACE_LAT != 0 && Common.PLACE_LNG != 0)
                                    {
                                        order.setLat(Common.PLACE_LAT);
                                        order.setLng(Common.PLACE_LNG);
                                        Log.d("LocPlace",String.valueOf(Common.PLACE_LAT));
                                    }
                                    else
                                    {
                                        order.setLat(-0.1f);
                                        order.setLng(-0.1f);
                                    }
                           /* else if (Common.PLACE_LAT != 0 && Common.PLACE_LNG != 0 && currentLocation == null)
                            {
                                order.setLat(Common.PLACE_LAT);
                                order.setLng(Common.PLACE_LNG);
                                Log.d("LocPlace",String.valueOf(Common.PLACE_LAT));
                            } */

                                    order.setCartItemList(cartItems);
                                    order.setTotalPayment(totalPrice);
                                    order.setDiscount(0); //Modify dicount late
                                    order.setFinalPayment(finalPrice);
                                    order.setCod(true);
                                    order.setTransactionId("Cash On Delivery");
                                    order.setBranch(Common.SELECTED_BRANCH);
                                    //Submit
                                    //writeOrderToFirebase(order);
                                    syncLocalTimeWithGlobalTime(order);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (!e.getMessage().contains("Query returned empty result set"))
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("ERROR_CART_ORDER : ",e.getMessage());

                                }
                            });
                }, throwable -> {
                    // Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERRR!",throwable.getMessage());
                }));

    }

    private void paymentCOD(String address, String comment) {

        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            //When We have all Cart Item <we get total price
            cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Double>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Double totalPrice) {
                            double finalPrice=totalPrice; //for descount lat imp
                            Order order=new Order();
                            order.setUserId(Common.currentUser.getUid());
                            order.setUserName(Common.currentUser.getName());
                            order.setUserPhone(Common.currentUser.getPhone());
                            order.setShippingAddress(address);
                            order.setComment(comment);

                            if (currentLocation != null)
                            {
                                order.setLat(currentLocation.getLatitude());
                                order.setLng(currentLocation.getLongitude());
                            }
                            else
                            {
                                order.setLat(-0.1f);
                                 order.setLng(-0.1f);

                            }
                            order.setCartItemList(cartItems);
                            order.setTotalPayment(totalPrice);
                            order.setDiscount(0); //Modify dicount late
                            order.setFinalPayment(finalPrice);
                            order.setCod(true);
                            order.setTransactionId("Cash On Delivery");
                            order.setBranch(Common.SELECTED_BRANCH);

                            //Submit
                            //writeOrderToFirebase(order);
                            syncLocalTimeWithGlobalTime(order);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!e.getMessage().contains("Query returned empty result set"))
                                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_CART_ORDER : ",e.getMessage());

                        }
                    });
        }, throwable -> {
           // Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("ERRR!",throwable.getMessage());
        }));

    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        final DatabaseReference offerRef =FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long offset=dataSnapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis()+offset;
                SimpleDateFormat sdf=new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultDate=new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE",""+sdf.format(resultDate));

                listener.onLoadTimeSuccess(order,estimatedServerTimeMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                listener.onLoadTimeFailed(databaseError.getMessage());
            }
        });
    }

    private void writeOrderToFirebase(Order order) {
        spotsDialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener(e -> {
                    //Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_ORDE: ",e.getMessage());
                }).addOnCompleteListener(task -> {
                    cartDataSource.cleanCart(Common.currentUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Map<String,String> notiData= new HashMap<>();
                                    notiData.put(Common.NOTI_TITLE,"طلب جديد");
                                    notiData.put(Common.NOTI_CONTENT," لديك طلب جديد من المستخدم: "+Common.currentUser.getPhone());

                                    FCMSendData sendData=new FCMSendData(Common.createTopicOrder(),notiData);

                                    compositeDisposable.add(ifcmService.sendNotification(sendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        spotsDialog.dismiss();
                                             Snackbar.make(getView(), getResources().getString(R.string.order_palced_toast), Snackbar.LENGTH_LONG)
                                                   .setAction("Action",null).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        EventBus.getDefault().postSticky(new GoToOrdersEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), getResources().getString(R.string.Failredtosendnotify), Toast.LENGTH_LONG).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }));


                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));





                                }

                                @Override
                                public void onError(Throwable e) {
                                   // Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("ERROR_ORDE: ",e.getMessage());
                                }
                            });
                });
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Locale lHebrew = new Locale("ar");
        Geocoder geocoder=new Geocoder(getContext(), lHebrew);
        String result="";

        try {
            List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
            if (addressList != null && addressList.size() > 0)
            {
                Address address=addressList.get(0);
                StringBuilder sb=new StringBuilder(address.getAddressLine(0));
                result =sb.toString();
            }
            else
                result ="Address not found";
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    private MyCartAdapter adapter;

    private Unbinder unbinder;

    private CartViewModel cartViewModel;






    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        listener =this;

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty())
                {
                    recycler_cart.setVisibility(View.GONE);
                    group_place_holder.setVisibility(View.GONE);
                    txt_empty_cart.setVisibility(View.VISIBLE);

                }
                else
                {
                    recycler_cart.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_cart.setVisibility(View.GONE);

                    adapter=new MyCartAdapter(getContext(),cartItems);
                    recycler_cart.setAdapter(adapter);
                }

            }
        });

        unbinder= ButterKnife.bind(this,root);
        initView();
        initLocation();
        return root;
    }

    private void initLocation() {
        buildLocationRequest();
        buildLocationCallBack();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }

    private void buildLocationCallBack() {
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation=locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void initView() {

        searchFoodCallbackListener = this;

        spotsDialog=new SpotsDialog.Builder().setCancelable(false).setMessage(getResources().getString(R.string.loading)).setContext(getContext()).build();


        initPlaceClient();

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipHelper mySwipHelper=new MySwipHelper(getContext(),recycler_cart,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),getResources().getString(R.string.Delete),30,0, Color.parseColor("#FF3C30"),
                        pos -> {
                           // Toast.makeText(getContext(), "Delete Item Click!", Toast.LENGTH_SHORT).show();
                            CartItem cartItem=adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            sumAllItemInCart();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            Toast.makeText(getContext(), getResources().getString(R.string.DeleteCartitem), Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                           // Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("ERRR!",e.getMessage());
                                        }
                                    });
                        }));

                /*buf.add(new MyButton(getContext(),getResources().getString(R.string.UpdateOrder),30,0, Color.parseColor("#5D4037"),
                        pos -> {
                          spotsDialog.show();
                          CartItem cartItem = adapter.getItemAtPosition(pos);
                          FirebaseDatabase.getInstance()
                                  .getReference(Common.CATEGORY_REF)
                                  .child(cartItem.getCategoryId())
                                  .addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if (dataSnapshot.exists())
                                          {
                                              CategoryModel categoryModel =dataSnapshot.getValue(CategoryModel.class);
                                              searchFoodCallbackListener.onSearchFoodFound(categoryModel,cartItem);

                                          }
                                          else
                                          {
                                              searchFoodCallbackListener.onSearchFoodNotFound("Food not found");
                                          }
                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {
                                          searchFoodCallbackListener.onSearchFoodNotFound(databaseError.getMessage());

                                      }
                                  });
                        })); */
            }
        };

        sumAllItemInCart();

        //Addon
        addonBottomSheetDialog = new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_addon =(ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        //edt_search=(EditText) layout_addon_display.findViewById(R.id.edt_search);
        addonBottomSheetDialog.setContentView(layout_addon_display);

        addonBottomSheetDialog.setOnDismissListener(dialog -> {
            diplayUserSelectedAddon(chip_group_user_selected_addon);
            calculateTotalPrice();
        });
    }

    private void diplayUserSelectedAddon(ChipGroup chip_group_user_selected_addon) {
        if (Common.selectedFood.getUserSelectedAddon() != null && Common.selectedFood.getUserSelectedAddon().size() > 0)
        {
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel:Common.selectedFood.getUserSelectedAddon())
            {
                Chip chip =(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(getContext().getResources().getString(R.string.currancy))
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_user_selected_addon.addView(chip);

            }
        }
        else
            chip_group_user_selected_addon.removeAllViews();
    }

    private void initPlaceClient() {
        Places.initialize(getContext(),getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());
    }

    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        txt_total_price.setText(new StringBuilder(getResources().getString(R.string.Total)).append(" ").append(aDouble).append(getContext().getResources().getString(R.string.currancy)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                            Toast.makeText(getContext(), "{UPDATE CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cart)
        {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Clear Cart Success", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                           // Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("ERRR!",e.getMessage());
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient !=null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper());
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event)
    {
        if (event.getCartItem() != null)
        {
            //Save state of recycle virew
            recyclerViewState=recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(getContext(), "{UPDATE CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("ERRR!",e.getMessage());
                        }
                    });

        }
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        txt_total_price.setText(new StringBuilder(getResources().getString(R.string.Total)).append(" ").append(Common.formatPrice(price))
                        .append(getContext().getResources().getString(R.string.currancy)));
                        recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty result set"))
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERRR!",e.getMessage());
                    }
                });

    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeInMs) {

        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemback());
        super.onDestroy();
    }




    private void showUpdateDialog(CartItem cartItem, FoodModel foodModel) {
        Common.selectedFood = foodModel;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_update_cart,null);
        builder.setView(itemView);

        //View
        Button btn_ok = (Button)itemView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button)itemView.findViewById(R.id.btn_cancel);
        CardView card_addon = (CardView)itemView.findViewById(R.id.card_addon);


      /*  if ( foodModel.getAddon()==null){
            card_addon.setVisibility(View.GONE);
        } */

        RadioGroup rdi_group_size =(RadioGroup)itemView.findViewById(R.id.rdi_group_size);
        chip_group_user_selected_addon = (ChipGroup) itemView.findViewById(R.id.chip_group_user_selected_addon);
        ImageView img_add_addon = (ImageView) itemView.findViewById(R.id.img_add_addon);
        img_add_addon.setOnClickListener(v -> {
            if (foodModel.getAddon() != null)
            {
                displayAddonList();
                addonBottomSheetDialog.show();
            }



        });

        //Size
        if (foodModel.getSize() != null) {
            for (SizeModel sizeModel : foodModel.getSize()) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                     if (isChecked)
                         Common.selectedFood.setUserSelectedSize(sizeModel);
                     calculateTotalPrice();
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
                radioButton.setLayoutParams(params);

                if(!sizeModel.getName().equals("Default"))
                {
                    radioButton.setText(sizeModel.getName());
                    radioButton.setTag(sizeModel.getPrice());

                }
                else {
                    radioButton.setVisibility(View.GONE);
                }

                rdi_group_size.addView(radioButton);





            }

            if (rdi_group_size.getChildCount() > 0)
            {
                RadioButton radioButton =(RadioButton)rdi_group_size.getChildAt(0); //Get First
                radioButton.setChecked(true); //Set Deafult
            }
        }

        //Addon
        displayAleardySelectedAddon(chip_group_user_selected_addon,cartItem);

        //Show Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        //Custom Dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        //Event
        btn_ok.setOnClickListener(v -> {
            //First delete item in cart
            cartDataSource.deleteCartItem(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            if (Common.selectedFood.getUserSelectedAddon() != null)
                                cartItem.setFoodAddon(new Gson().toJson(Common.selectedFood.getUserSelectedAddon()));
                            else
                                cartItem.setFoodAddon("Default");
                            if (Common.selectedFood.getUserSelectedSize() != null)
                                cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
                            else
                                cartItem.setFoodSize("Default");

                            cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize(),
                                    Common.selectedFood.getUserSelectedAddon()));

                            //Insset new
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                calculateTotalPrice();
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Update Cart Success", Toast.LENGTH_SHORT).show();
                            },throwable -> {
                                Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        });
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }

    private void displayAleardySelectedAddon(ChipGroup chip_group_user_selected_addon, CartItem cartItem) {
        if (cartItem.getFoodAddon() != null && !cartItem.getFoodAddon().equals("Default"))
        {
            List<AddonModel> addonModels = new Gson().fromJson(
                    cartItem.getFoodAddon(),new TypeToken<List<AddonModel>>(){}.getType());
            Common.selectedFood.setUserSelectedAddon(addonModels);
            chip_group_user_selected_addon.removeAllViews();
            //Add all View
            for (AddonModel addonModel : addonModels)
            {
                Chip chip =(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(getContext().getResources().getString(R.string.currancy))
                        .append(addonModel.getPrice()).append(")"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(v -> {
                    //Remove when user sel deltete
                    chip_group_user_selected_addon.removeView(v);
                    Common.selectedFood.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();
                });
                chip_group_user_selected_addon.addView(chip);
            }

        }
    }

    private void displayAddonList() {
        if (Common.selectedFood.getAddon() != null && Common.selectedFood.getAddon().size() > 0) {

            chip_group_addon.clearCheck();
            chip_group_addon.removeAllViews();

          //  edt_search.addTextChangedListener(this);

            //Add all View
            for (AddonModel addonModel : Common.selectedFood.getAddon())
            {
                Chip chip =(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(getContext().getResources().getString(R.string.currancy))
                .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked)
                    {
                        try {
                            if (Common.selectedFood.getUserSelectedAddon() == null)
                                Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                            Common.selectedFood.getUserSelectedAddon().add(addonModel);
                        }catch (Exception e){}

                    }
                });
                chip_group_addon.addView(chip);
            }
        }

    }

    @Override
    public void onSearchFoodFound(CategoryModel categoryModel,CartItem cartItem) {
        FoodModel foodModel =Common.findFoodInListById(categoryModel,cartItem.getFoodId());
        if (foodModel != null)
        {
            showUpdateDialog(cartItem,foodModel);
            spotsDialog.dismiss();

        }
        else
            Toast.makeText(getContext(), "Food Id not Found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchFoodNotFound(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();

    }


  /*  @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();
        for (AddonModel addonModel : Common.selectedFood.getAddon())
        {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
            {
                Chip chip =(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(getContext().getResources().getString(R.string.currancy))
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);

            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    } */
}