package com.eatitappclient.tws.EventBus;

import com.eatitappclient.tws.Model.PopularCategoryModel;

public class PopularCatergoryClick {
    private PopularCategoryModel popularCategoryModel;

    public PopularCatergoryClick(PopularCategoryModel popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }

    public PopularCategoryModel getPopularCategoryModel() {
        return popularCategoryModel;
    }

    public void setPopularCategoryModel(PopularCategoryModel popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }
}
