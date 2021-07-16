package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.FoodModel;

public interface ISearchFoodCallbackListener {
    void onSearchFoodFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchFoodNotFound(String message);
}
