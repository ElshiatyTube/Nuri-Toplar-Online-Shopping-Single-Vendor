package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.BestDealsModel;

import java.util.List;

public interface IBestDealsCallbackListener {
    void onListBestDealsLoadSuccess(List<BestDealsModel> bestDealsModels);
    void onListBestDealsLoadFailed(String message);
}
