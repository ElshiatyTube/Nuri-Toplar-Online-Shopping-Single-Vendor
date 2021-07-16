package com.eatitappclient.tws.ui.bestDealsDetails;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.Model.CommentModel;
import com.eatitappclient.tws.Model.FoodModel;

public class BestDealsDetailsViewModel extends ViewModel {
    private MutableLiveData<BestDealsModel> mutableLiveDataFood;


    public MutableLiveData<BestDealsModel> getMutableLiveDataFood() {
        if (mutableLiveDataFood == null)
            mutableLiveDataFood=new MutableLiveData<>();
        mutableLiveDataFood.setValue(Common.selectedBestDeals);
        return mutableLiveDataFood;
    }

    public void setFoodModel(BestDealsModel foodModel) {
        if (mutableLiveDataFood != null)
            mutableLiveDataFood.setValue(foodModel);
    }
}
