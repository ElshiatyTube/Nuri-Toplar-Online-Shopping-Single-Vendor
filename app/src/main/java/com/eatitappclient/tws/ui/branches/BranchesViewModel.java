package com.eatitappclient.tws.ui.branches;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eatitappclient.tws.Callback.IBranchesCallbackListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Model.BranchesModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BranchesViewModel extends ViewModel implements IBranchesCallbackListener {
    private MutableLiveData<List<BranchesModel>> branchesListMutable;
    private MutableLiveData<String> messageError=new MutableLiveData<>();
    private IBranchesCallbackListener branchesCallbackListener;

    public BranchesViewModel() {
        branchesCallbackListener=this;

    }

    public MutableLiveData<List<BranchesModel>> getBranchesListMutable() {
        if(branchesListMutable == null)
        {
            branchesListMutable=new MutableLiveData<>();
            messageError=new MutableLiveData<>();
            loadBranches();
        }
        return branchesListMutable;
    }

    public void loadBranches() {
        List<BranchesModel> tempList=new ArrayList<>();
        DatabaseReference branchesRef= FirebaseDatabase.getInstance().getReference(Common.BRANCHES_REF);
        branchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    BranchesModel branchesModel=itemSnapShot.getValue(BranchesModel.class);
                    branchesModel.setBranchId(itemSnapShot.getKey());
                    tempList.add(branchesModel);
                }
                branchesCallbackListener.onBranchesLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                branchesCallbackListener.onBranchesLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }


    @Override
    public void onBranchesLoadSuccess(List<BranchesModel> branchesModelList) {
        branchesListMutable.setValue(branchesModelList);

    }

    @Override
    public void onBranchesLoadFailed(String message) {
        messageError.setValue(message);

    }
}
