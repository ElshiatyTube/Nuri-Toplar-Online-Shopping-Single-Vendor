package com.eatitappclient.tws.EventBus;

import com.eatitappclient.tws.Model.CategoryModel;

public class CaregoryClick {
    private boolean success;
    private CategoryModel categoryModel;

    public CaregoryClick(boolean success, CategoryModel categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}
