package com.eatitappclient.tws.ui.cart;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Database.CartDataSource;
import com.eatitappclient.tws.Database.CartDatabase;
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Database.LocalCartDataSource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;

    public CartViewModel() {
        compositeDisposable=new CompositeDisposable();
    }

    public void initCartDataSource(Context context)
    {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }
    public void onStop(){
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItems() {
        if (mutableLiveDataCartItems == null)
            mutableLiveDataCartItems = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            mutableLiveDataCartItems.setValue(cartItems);
        }, throwable -> {
            mutableLiveDataCartItems.setValue(null);
        }));

    }
}