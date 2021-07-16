package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.BranchesModel;
import com.eatitappclient.tws.Model.CategoryModel;

import java.util.List;

public interface IBranchesCallbackListener {
    void onBranchesLoadSuccess(List<BranchesModel> branchesModelList);
    void onBranchesLoadFailed(String message);
}
