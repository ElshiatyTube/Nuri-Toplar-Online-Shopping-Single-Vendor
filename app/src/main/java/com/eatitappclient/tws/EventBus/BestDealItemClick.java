package com.eatitappclient.tws.EventBus;

import com.eatitappclient.tws.Model.BestDealsModel;

public class BestDealItemClick {
    private BestDealsModel bestDealModel;

    public BestDealItemClick(BestDealsModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }

    public BestDealsModel getBestDealModel() {
        return bestDealModel;
    }

    public void setBestDealModel(BestDealsModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }
}
