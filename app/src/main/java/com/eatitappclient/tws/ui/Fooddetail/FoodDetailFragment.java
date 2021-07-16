package com.eatitappclient.tws.ui.Fooddetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.andremion.counterfab.CounterFab;
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
import com.eatitappclient.tws.Model.CommentModel;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.Model.SizeModel;
import com.eatitappclient.tws.R;
import com.eatitappclient.tws.ui.comments.CommentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import org.w3c.dom.Comment;

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

public class FoodDetailFragment extends Fragment  {

    private FoodDetailViewModel foodDetailViewModel;

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


    @BindView(R.id.card_addon)
    CardView card_addon;  //addon

    @BindView(R.id.layout_size)
    LinearLayout layout_size;

    @BindView(R.id.layout_addon)
    LinearLayout layout_addon;

    @BindView(R.id.txt_size_one)
    TextView txt_size_one;

    @BindView(R.id.food_availability)
    TextView food_availability;



    @OnClick(R.id.img_add_addon)
    void onAddonClick()
    {
        if (Common.selectedFood.getAddon() != null)
        {
            displayAddonList();
            addonButtomSheetDialog.show();
        }
    }



    @OnClick(R.id.btnCart)
    void onCartItemAdd()
    {
        if(food_availability.getVisibility()==View.GONE)
        {
            CartItem cartItem=new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setCategoryId(Common.categroySelected.getMenu_id());

            cartItem.setFoodId(Common.selectedFood.getId());
            cartItem.setFoodName(Common.selectedFood.getName());
            cartItem.setFoodImage(Common.selectedFood.getImage());
            cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedFood.getPrice())));
            cartItem.setFoodQuantity(Integer.valueOf(number_button.getNumber()));
            cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize(),Common.selectedFood.getUserSelectedAddon())); //As Default
            if (Common.selectedFood.getUserSelectedAddon() !=null)
                cartItem.setFoodAddon(new Gson().toJson(Common.selectedFood.getUserSelectedAddon()));
            else
                cartItem.setFoodAddon("Default");

            if (Common.selectedFood.getUserSelectedSize() !=null)
                cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
            else
                cartItem.setFoodSize("Default");

            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                    Common.categroySelected.getMenu_id(),
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

    private void displayAddonList() {
        if (Common.selectedFood.getAddon().size() > 0)
        {
            chip_group_addon.clearCheck();
            chip_group_addon.removeAllViews();

           // edt_search.addTextChangedListener(this);

            //Add all view
            for (AddonModel addonModel:Common.selectedFood.getAddon())
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

    @OnClick(R.id.btn_rating)
    void onRatingButtonClick()
    {
        showDialogRating();
    }
    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick()
    {
        CommentFragment commentFragment=CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(),"CommentFragment");
    }

    double totalPrice,displayPrice=0.0;;

    private void showDialogRating() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Rating Food");
        builder.setMessage("Please fill information");

        View itemView= LayoutInflater.from(getContext()).inflate(R.layout.layout_rating,null);

        RatingBar ratingBar=(RatingBar)itemView.findViewById(R.id.rating_bar);
        EditText edt_comment=(EditText)itemView.findViewById(R.id.edit_comment);

        builder.setView(itemView);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommentModel commentModel=new CommentModel();
                commentModel.setName(Common.currentUser.getName());
                commentModel.setUid(Common.currentUser.getUid());
                commentModel.setComment(edt_comment.getText().toString());
                commentModel.setRatingValue(ratingBar.getRating());
                Map<String,Object> serverTimeStamp= new HashMap<>();
                serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                commentModel.setCommentTimeStamp(serverTimeStamp);

                foodDetailViewModel.setCommentModel(commentModel);


            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodDetailViewModel =
                ViewModelProviders.of(this).get(FoodDetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder= ButterKnife.bind(this,root);
        initView();
        foodDetailViewModel.getMutableLiveDataFood().observe(getViewLifecycleOwner(),foodModel -> {
            displayInfo(foodModel);
        });
        foodDetailViewModel.getMutableLiveDataComment().observe(getViewLifecycleOwner(),commentModel -> {
            submitRatingToFirebase(commentModel);
        });

      /*  number_button.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                double totalPrice=Double.parseDouble(String.valueOf(Common.selectedFood.getPrice())),displayPrice=0.0;


                displayPrice = totalPrice * (Integer.parseInt(number_button.getNumber()));
                displayPrice = Math.round(displayPrice*100.0/100.0);

                food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
            }
        }); */

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
            diplayUserSelectedAddon();
            calculateTotalprice();
        });

/*        if (Common.selectedFood.getSize()==null)
            layout_size.setVisibility(View.GONE);
        if (Common.selectedFood.getSize()==null)
            layout_addon.setVisibility(View.GONE); */

    }

    private void diplayUserSelectedAddon() {
        if (Common.selectedFood.getUserSelectedAddon() != null &&
        Common.selectedFood.getUserSelectedAddon().size() > 0)
        {
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel: Common.selectedFood.getUserSelectedAddon())
            {
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+")
                .append(addonModel.getPrice()).append(")").append(getContext().getResources().getString(R.string.currancy)));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(v -> {
                    //Remove when user selcet delete
                    chip_group_user_selected_addon.removeView(v);
                    Common.selectedFood.getUserSelectedAddon().remove(addonModel);
                    calculateTotalprice();
                });
                chip_group_user_selected_addon.addView(chip);
            }
        }else  {
            chip_group_user_selected_addon.removeAllViews();

        }
    }

    private void submitRatingToFirebase(CommentModel commentModel) {
        waitingDialog.show();
        //Sumit to comment ref
        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectedFood.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        addRatingToFood(commentModel.getRatingValue());

                    }
                    waitingDialog.dismiss();
                });
    }

    private void addRatingToFood(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categroySelected.getMenu_id()) //Selcet Category
             .child("foods")//select array list 'food' for this category
             .child(Common.selectedFood.getKey())
             .addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists())
                     {
                         FoodModel foodModel=dataSnapshot.getValue(FoodModel.class);
                         foodModel.setKey(Common.selectedFood.getKey());

                         //Applay rating
                         if (foodModel.getRatingValue() == null)
                             foodModel.setRatingValue(0d);
                         if (foodModel.getRatingCount() == null)
                             foodModel.setRatingCount(0l);

                         double sumRating=foodModel.getRatingValue()+ratingValue;
                         long ratingCount=foodModel.getRatingCount()+1;

                         Map<String,Object> updateData=new HashMap<>();
                         updateData.put("ratingValue",sumRating);
                         updateData.put("ratingCount",ratingCount);

                         foodModel.setRatingValue(sumRating);
                         foodModel.setRatingCount(ratingCount);


                         dataSnapshot.getRef()
                                 .updateChildren(updateData)
                                 .addOnCompleteListener(task -> {
                                     waitingDialog.dismiss();
                                     if (task.isSuccessful())
                                     {
                                         Toast.makeText(getContext(), "Thank you!", Toast.LENGTH_SHORT).show();
                                         Common.selectedFood=foodModel;
                                         foodDetailViewModel.setFoodModel(foodModel); //Call refresh
                                     }

                                 });
                     }
                     else
                         waitingDialog.dismiss();

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {
                     waitingDialog.dismiss();
                     Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                 }
             });
    }

    private void displayInfo(FoodModel foodModel) {
        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuilder(foodModel.getName()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));
        food_price.setText(new StringBuilder(String.valueOf(foodModel.getPrice())));
        if (!foodModel.isAvailability())
            food_availability.setVisibility(View.VISIBLE);




        if(foodModel.getRatingValue() != null)
            ratingBar.setRating(foodModel.getRatingValue().floatValue() / foodModel.getRatingCount());



        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedFood.getName());

     /*   if (Common.selectedFood.getAddon()==null || Common.selectedFood.getAddon().size()==0){
            card_addon.setVisibility(View.GONE);
        } */

        if (Common.selectedFood.getSize()!=null)
        {

            for (SizeModel sizeModel: Common.selectedFood.getSize())
            {
                RadioButton radioButton=new RadioButton(getContext());

                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked)
                        Common.selectedFood.setUserSelectedSize(sizeModel);
                    calculateTotalprice();

                });
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f);
                radioButton.setLayoutParams(params);
                if(!sizeModel.getName().equals("Default"))
                {
                    radioButton.setText(new StringBuilder(sizeModel.getName()).append(" +").append(sizeModel.getPrice()));
                    radioButton.setTag(sizeModel.getPrice());

                }
                else {
                    radioButton.setVisibility(View.GONE);
                }

                if(radioButton.getVisibility()==View.GONE)
                {
                    txt_size_one.setText(getResources().getString(R.string.OneSize));
                }
                else {
                    txt_size_one.setText(getResources().getString(R.string.Size));
                }


                rdi_group_size.addView(radioButton);
            }

            if (rdi_group_size.getChildCount() > 1 )
            {
                RadioButton radioButton=(RadioButton)rdi_group_size.getChildAt(1);
                radioButton.setChecked(true);
                Log.d("Check","Sdsd");
            }

        }




        calculateTotalprice();

    }

    private void calculateTotalprice() {
        totalPrice=Double.parseDouble(String.valueOf(Common.selectedFood.getPrice()));
      //  food_price.setText(new StringBuilder("").append(Common.formatPrice(totalPrice)).toString());

        Log.d("DesPrice1: ", String.valueOf(totalPrice));
        //Addon
        if (Common.selectedFood.getUserSelectedAddon() !=null && Common.selectedFood.getUserSelectedAddon().size()>0)
            for (AddonModel addonModel:Common.selectedFood.getUserSelectedAddon())
                totalPrice+=Double.parseDouble(addonModel.getPrice().toString());

        //Size
        if (Common.selectedFood.getUserSelectedSize() !=null)
            totalPrice += Double.parseDouble(Common.selectedFood.getUserSelectedSize().getPrice().toString());


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