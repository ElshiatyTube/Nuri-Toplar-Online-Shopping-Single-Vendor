package com.eatitappclient.tws.ui.comments;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Model.CommentModel;
import com.eatitappclient.tws.Model.FoodModel;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataFoodList;

    public CommentViewModel() {
            mutableLiveDataFoodList=new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataFoodList() {
        return mutableLiveDataFoodList;
    }
    public void setCommentList(List<CommentModel> commentList)
    {
        mutableLiveDataFoodList.setValue(commentList);
    }

}
