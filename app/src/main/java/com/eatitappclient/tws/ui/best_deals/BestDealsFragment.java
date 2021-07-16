package com.eatitappclient.tws.ui.best_deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eatitappclient.tws.Adapter.MyBestDealsAdapter;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.MySwipHelper;
import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.R;


import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;


public class BestDealsFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1234;

    private BestDealsViewModel mViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_best_deals)
    RecyclerView recycler_best_deals;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyBestDealsAdapter adapter;

    List<BestDealsModel> bestDealsModels;





    public static BestDealsFragment newInstance() {
        return new BestDealsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        mViewModel =
                new ViewModelProvider(this).get(BestDealsViewModel.class);
        View root = inflater.inflate(R.layout.best_deals_fragment, container, false);

        unbinder= ButterKnife.bind(this,root);
        initView();

        mViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        mViewModel.getBestDealsListMutable().observe(getViewLifecycleOwner() ,list -> {
            dialog.dismiss();
            bestDealsModels = list;
            adapter=new MyBestDealsAdapter(getContext(),bestDealsModels);
            recycler_best_deals.setAdapter(adapter);
            recycler_best_deals.setLayoutAnimation(layoutAnimationController);
        });

        return root;

    }

    private void initView() {



        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_form_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_best_deals.setLayoutManager(layoutManager);
        recycler_best_deals.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));



    }





}
