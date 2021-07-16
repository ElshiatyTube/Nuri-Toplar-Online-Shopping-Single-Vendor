package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.PopularCategoryModel;

import java.util.List;

public interface ICategoryHomeCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModels);
    void onCategoryLoadFailed(String message);
}
