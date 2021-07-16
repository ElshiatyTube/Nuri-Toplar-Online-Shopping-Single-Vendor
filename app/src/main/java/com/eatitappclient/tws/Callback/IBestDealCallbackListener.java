package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.BestDealsModel;

import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealsModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}
