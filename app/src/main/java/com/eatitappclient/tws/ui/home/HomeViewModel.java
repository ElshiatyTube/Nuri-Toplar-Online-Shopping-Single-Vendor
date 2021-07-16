package com.eatitappclient.tws.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Callback.ICaegoryCallbackListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements ICaegoryCallbackListener {

    private MutableLiveData<List<CategoryModel>> categoryListMutable;
    private MutableLiveData<String> messageError=new MutableLiveData<>();
    private ICaegoryCallbackListener categoryCallbackListener;

    public HomeViewModel() {
        categoryCallbackListener=this;

    }

    public MutableLiveData<List<CategoryModel>> getCategoryListMutable() {
        if(categoryListMutable == null)
        {
            categoryListMutable=new MutableLiveData<>();
            messageError=new MutableLiveData<>();
            loadCategories();
        }
        return categoryListMutable;
    }

    public void loadCategories() {
        List<CategoryModel> tempList=new ArrayList<>();
        DatabaseReference categoryRef= FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    CategoryModel categoryModel=itemSnapShot.getValue(CategoryModel.class);
                    categoryModel.setMenu_id(itemSnapShot.getKey());
                    tempList.add(categoryModel);
                }
                categoryCallbackListener.onCaegoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                categoryCallbackListener.onCaegoryLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCaegoryLoadSuccess(List<CategoryModel> categoryModelList)
    {
        categoryListMutable.setValue(categoryModelList);
    }


    @Override
    public void onCaegoryLoadFailed(String message) {
        messageError.setValue(message);

    }
}