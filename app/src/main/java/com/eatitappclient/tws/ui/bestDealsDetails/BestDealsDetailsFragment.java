package com.eatitappclient.tws.ui.bestDealsDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Database.CartDataSource;
import com.eatitappclient.tws.Database.CartDatabase;
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Database.LocalCartDataSource;
import com.eatitappclient.tws.EventBus.CounterCartEvent;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.Model.AddonModel;
import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.Model.CommentModel;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.Model.SizeModel;
import com.eatitappclient.tws.R;
import com.eatitappclient.tws.ui.Fooddetail.FoodDetailViewModel;
import com.eatitappclient.tws.ui.best_deals.BestDealsViewModel;
import com.eatitappclient.tws.ui.comments.CommentFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BestDealsDetailsFragment extends Fragment {

    private BestDealsDetailsViewModel bestDealsDetailsViewModel;

    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private Unbinder unbinder;

    private AlertDialog waitingDialog;
    private BottomSheetDialog addonButtomSheetDialog;

    ChipGroup chip_group_addon;
    // EditText edt_search;

    @BindView(R.id.img_food)
    ImageView img_food;
    @BindView(R.id.btnCart)
    MaterialButton btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView(R.id.food_name)
    TextView food_name;
    @BindView(R.id.food_description)
    TextView food_description;
    @BindView(R.id.food_price)
    TextView food_price;
    @BindView(R.id.number_button)
    ElegantNumberButton number_button;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;

    @BindView(R.id.img_add_addon)
    ImageView img_add_on;  //addon
    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chip_group_user_selected_addon;


    @BindView(R.id.food_availability)
    TextView food_availability;




    @OnClick(R.id.btnCart)
    void onCartItemAdd()
    {
        if(food_availability.getVisibility()==View.GONE)
        {
            CartItem cartItem=new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setCategoryId(Common.selectedBestDeals.getKey());

            cartItem.setFoodId(Common.selectedBestDeals.getId());
            cartItem.setFoodName(Common.selectedBestDeals.getName());
            cartItem.setFoodImage(Common.selectedBestDeals.getImage());
            cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedBestDeals.getPrice())));
            cartItem.setFoodQuantity(Integer.valueOf(number_button.getNumber()));
            cartItem.setFoodExtraPrice(0.0); //As Default
                cartItem.setFoodAddon("Default");
            cartItem.setFoodSize("Default");


            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                    Common.selectedBestDeals.getKey(),
                    cartItem.getFoodId(),
                    cartItem.getFoodSize(),
                    cartItem.getFoodAddon())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<CartItem>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(CartItem cartItemFromDB) {
                            if (cartItemFromDB.equals(cartItem))
                            {
                                //
                                cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                                cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                                cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                                cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                                cartDataSource.updateCartItems(cartItemFromDB)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(Integer integer) {
                                                Toast.makeText(getContext(),  getResources().getString(R.string.updatecartToase), Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(getContext(), "{UPDATE CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });


                            }
                            else
                            {
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(getContext(), getResources().getString(R.string.AddCartsuccess), Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        },throwable -> {
                                            Toast.makeText(getContext(), "{CART ERROR}"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                        }));
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e.getMessage().contains("empty"))
                            {
                                //Defult if catr is emptyF
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(getContext(), getResources().getString(R.string.AddCartsuccess), Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        },throwable -> {
                                            Toast.makeText(getContext(), "{CART ERROR}"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                        }));
                            }
                            else
                                Toast.makeText(getContext(), "{GET CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    });


        }
        else {
            Toast.makeText(getContext(), getResources().getString(R.string.OutStock), Toast.LENGTH_SHORT).show();
        }

    }



    double totalPrice,displayPrice=0.0;;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bestDealsDetailsViewModel =
                ViewModelProviders.of(this).get(BestDealsDetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder= ButterKnife.bind(this,root);
        initView();
        bestDealsDetailsViewModel.getMutableLiveDataFood().observe(getViewLifecycleOwner(),foodModel -> {
            displayInfo(foodModel);
        });


        return root;
    }

    private void initView() {



        cartDataSource=new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        waitingDialog=new SpotsDialog.Builder().setCancelable(true).setMessage(getResources().getString(R.string.loading)).setContext(getContext()).build();

        addonButtomSheetDialog=new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display=getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_addon=(ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        //edt_search=(EditText)layout_addon_display.findViewById(R.id.edt_search);
        addonButtomSheetDialog.setContentView(layout_addon_display);

        addonButtomSheetDialog.setOnDismissListener(dialog -> {
            calculateTotalprice();
        });

/*        if (Common.selectedFood.getSize()==null)
            layout_size.setVisibility(View.GONE);
        if (Common.selectedFood.getSize()==null)
            layout_addon.setVisibility(View.GONE); */

    }




    private void displayInfo(BestDealsModel foodModel) {
        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuilder(foodModel.getName()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));
        food_price.setText(new StringBuilder(String.valueOf(foodModel.getPrice())));
        if (!foodModel.isAvailability())
            food_availability.setVisibility(View.VISIBLE);






        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedBestDeals.getName());





        calculateTotalprice();

    }

    private void calculateTotalprice() {
        totalPrice=Double.parseDouble(String.valueOf(Common.selectedBestDeals.getPrice()));
        //  food_price.setText(new StringBuilder("").append(Common.formatPrice(totalPrice)).toString());

        Log.d("DesPrice1: ", String.valueOf(totalPrice));


        displayPrice = totalPrice * (Integer.parseInt(number_button.getNumber()));
        //  displayPrice = Math.round(displayPrice*100.0/100.0);
        Log.d("DesPrice2: ", String.valueOf(displayPrice));

   /*     number_button.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                totalPrice += Integer.parseInt(number_button.getNumber());
                displayPrice = totalPrice * (Integer.parseInt(number_button.getNumber()));

            }
        }); */

        food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }

  /*  @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();

        for (AddonModel addonModel:Common.selectedFood.getAddon())
        {
            if (addonModel.getName().toLowerCase().contains(s.toString().toLowerCase()))
            {
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
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
        //Nothing

    } */

    @Override
    public void onStop() {
        compositeDisposable.clear();

        super.onStop();
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemback());
        super.onDestroy();
    }

}
