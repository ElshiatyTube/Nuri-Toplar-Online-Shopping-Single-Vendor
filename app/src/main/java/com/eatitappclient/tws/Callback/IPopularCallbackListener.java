package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.PopularCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}
