package com.eatitappclient.tws.ui.Foodlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Model.FoodModel;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList;

    public FoodListViewModel() {

    }

    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList() {
        if (mutableLiveDataFoodList == null)
            mutableLiveDataFoodList=new MutableLiveData<>();
        mutableLiveDataFoodList.setValue(Common.categroySelected.getFoods());
        return mutableLiveDataFoodList;
    }
}