package com.eatitappclient.tws.EventBus;

import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.Model.FoodModel;

public class BestDealsItemClick {
    private boolean success;
    private BestDealsModel foodModel;

    public BestDealsItemClick(boolean success, BestDealsModel foodModel) {
        this.success = success;
        this.foodModel = foodModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public BestDealsModel getFoodModel() {
        return foodModel;
    }

    public void setFoodModel(BestDealsModel foodModel) {
        this.foodModel = foodModel;
    }
}
