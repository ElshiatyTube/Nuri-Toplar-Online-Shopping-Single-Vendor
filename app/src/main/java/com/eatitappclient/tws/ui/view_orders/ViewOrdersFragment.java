package com.eatitappclient.tws.ui.view_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatitappclient.tws.Adapter.MyOrdersAdapter;
import com.eatitappclient.tws.Callback.ILoadOrderCallBackListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.MySwipHelper;
import com.eatitappclient.tws.Database.CartDataSource;
import com.eatitappclient.tws.Database.CartDatabase;
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Database.LocalCartDataSource;
import com.eatitappclient.tws.EventBus.CounterCartEvent;
import com.eatitappclient.tws.Model.Order;
import com.eatitappclient.tws.Model.ShippingOrderModel;
import com.eatitappclient.tws.R;
import com.eatitappclient.tws.TrakingOrderActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallBackListener {

    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    AlertDialog dialog;

    private Unbinder unbinder;

    private ViewOrdersViewModel viewOrdersViewModel;

    private ILoadOrderCallBackListener listener;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                ViewModelProviders.of(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder= ButterKnife.bind(this,root);

        initView(root);
        loadOrderFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {
            MyOrdersAdapter adapter=new MyOrdersAdapter(getContext(),orderList);
            recycler_orders.setAdapter(adapter);
        });

        return root;
    }

    private void loadOrderFromFirebase() {
        List<Order> orderList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapShot:dataSnapshot.getChildren())
                        {
                            Order order=orderSnapShot.getValue(Order.class);
                            order.setOrderNumber(orderSnapShot.getKey());
                            orderList.add(order);
                        }
                        listener.onLoadOrderSuccess(orderList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderFailed(databaseError.getMessage());

                    }
                });
    }

    private void initView(View root) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        listener=this;

         dialog=new SpotsDialog.Builder().setCancelable(false).setMessage(getResources().getString(R.string.loading)).setContext(getContext()).build();

         recycler_orders.setHasFixedSize(true);
         LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
         recycler_orders.setLayoutManager(layoutManager);
         recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;


        MySwipHelper mySwipHelper=new MySwipHelper(getContext(),recycler_orders,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
             /*   buf.add(new MyButton(getContext(),"Cancel Order",30,0, Color.parseColor("#FF3C30"),
                        pos -> {
                          Order orderModel =((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);
                          if (orderModel.getOrderStatus() == 0)
                          {
                              androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
                              builder.setTitle("Cancel Order")
                                      .setMessage("Do you really want to cancel this order?")
                                      .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialogInterface, int which) {
                                              dialogInterface.dismiss();
                                          }
                                      })
                                      .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              Map<String,Object> update_data=new HashMap<>();
                                              update_data.put("orderStatus",-1); //Cancel Order Status num
                                              FirebaseDatabase.getInstance()
                                                      .getReference(Common.ORDER_REF)
                                                      .child(orderModel.getOrderNumber())
                                                      .updateChildren(update_data)
                                                      .addOnFailureListener(new OnFailureListener() {
                                                          @Override
                                                          public void onFailure(@NonNull Exception e) {
                                                              Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                          }
                                                      }).addOnSuccessListener(aVoid -> {
                                                         orderModel.setOrderStatus(-1);
                                                         ((MyOrdersAdapter)recycler_orders.getAdapter()).setItemAtPosition(pos,orderModel);
                                                         recycler_orders.getAdapter().notifyItemChanged(pos);
                                                  Toast.makeText(getContext(), "Cancel Order successfully", Toast.LENGTH_SHORT).show();
                                                      });
                                          }
                                      });
                              androidx.appcompat.app.AlertDialog dialog = builder.create();
                              dialog.show();

                          }
                          else
                          {
                              Toast.makeText(getContext(), new StringBuilder("Your Order wase Changed to ")
                                      .append(Common.convertStatusToText(orderModel.getOrderStatus(),getContext()))
                                      .append(", so you can't cancel it!"), Toast.LENGTH_LONG).show();
                          }
                        })); */


                  /*  buf.add(new MyButton(getContext(),getResources().getString(R.string.TrackingOrder),30,0,getResources().getColor(R.color.colorAccent),
                            pos -> {
                                Order orderModel =((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);

                                //Fech from firebase
                                FirebaseDatabase.getInstance()
                                        .getReference(Common.SHIPPING_ORDER_REF)
                                        .child(orderModel.getOrderNumber())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                {
                                                    Common.currentShippingOrder = dataSnapshot.getValue(ShippingOrderModel.class);
                                                    Common.currentShippingOrder.setKey(dataSnapshot.getKey());
                                                    if (Common.currentShippingOrder.getCurrentLat() != -1 &&
                                                            Common.currentShippingOrder.getCurrentLng() != -1)
                                                    {
                                                        startActivity(new Intent(getContext(), TrakingOrderActivity.class));

                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getContext(), getResources().getString(R.string.Canrightnow), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(getContext(),  getResources().getString(R.string.Canrightnow), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            })); */





                buf.add(new MyButton(getContext(), getResources().getString(R.string.Repeat),30,0,getResources().getColor(R.color.appOrange),
                        pos -> {
                            Order orderModel =((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);
                            dialog.show();

                            cartDataSource.cleanCart(Common.currentUser.getUid())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            //After clear Cart , Just add new
                                            CartItem[] cartItems = orderModel
                                                    .getCartItemList().toArray(new CartItem[orderModel.getCartItemList().size()]);
                                            //Insert New
                                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItems)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() ->{
                                                dialog.dismiss();
                                                Toast.makeText(getContext(),  getResources().getString(R.string.Addcart), Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));

                                            },throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })
                                            );


                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }));
            }
        };


    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        if (orderList.size() > 0)
        {
            Collections.sort(orderList,(orderModel, t1)->{
                if (orderModel.getCreateDate() < t1.getCreateDate())
                    return -1;
                return orderModel.getCreateDate() == t1.getCreateDate() ? 0:1;
            });
            Collections.reverse(orderList);

        }
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
    }

}