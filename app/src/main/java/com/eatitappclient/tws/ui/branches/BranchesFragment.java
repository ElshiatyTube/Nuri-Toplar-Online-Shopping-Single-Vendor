package com.eatitappclient.tws.ui.branches;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatitappclient.tws.Adapter.MyBranchesAdapter;
import com.eatitappclient.tws.Adapter.MyCategoriesAdapter;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.SpacesItemDecoration;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BranchesFragment extends Fragment {


    private BranchesViewModel mViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyBranchesAdapter adapter;

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemback());
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(BranchesViewModel.class);
        View root = inflater.inflate(R.layout.branches_fragment, container, false);

        unbinder= ButterKnife.bind(this,root);
        initView();

        mViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        mViewModel.getBranchesListMutable().observe(getViewLifecycleOwner() ,branchesModelList -> {
            dialog.dismiss();
            adapter=new MyBranchesAdapter(getContext(),branchesModelList);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initView() {
        setHasOptionsMenu(true);
        dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(true).setMessage(getResources().getString(R.string.loading)).build();
        dialog.show();


        recycler_menu.setLayoutManager(new LinearLayoutManager(getContext()));
        //recycler_menu.addItemDecoration(new SpacesItemDecoration(8));
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_form_left);

    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BranchesViewModel.class);
        // TODO: Use the ViewModel
    } */

}
