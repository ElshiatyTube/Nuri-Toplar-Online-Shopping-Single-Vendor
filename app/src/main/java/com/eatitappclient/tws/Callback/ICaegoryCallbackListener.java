package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.CategoryModel;

import java.util.List;

public interface ICaegoryCallbackListener {
    void onCaegoryLoadSuccess(List<CategoryModel> categoryModelList);
    void onCaegoryLoadFailed(String message);
}
