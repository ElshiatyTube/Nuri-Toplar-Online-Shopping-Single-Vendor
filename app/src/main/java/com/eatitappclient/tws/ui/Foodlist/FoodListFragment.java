package com.eatitappclient.tws.ui.Foodlist;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatitappclient.tws.Adapter.MyFoodListAdapter;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.EventBus.ChangeMenuClick;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListFragment extends Fragment {

    private FoodListViewModel foodListViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView recycler_food_list;

    LayoutAnimationController layoutAnimationController;
    MyFoodListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodListViewModel =
                ViewModelProviders.of(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
        unbinder= ButterKnife.bind(this,root);
        initView();
        foodListViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), foodModels -> {
            if (foodModels != null)
            {
                adapter=new MyFoodListAdapter(getContext(),foodModels);
                recycler_food_list.setAdapter(adapter);
                recycler_food_list.setLayoutAnimation(layoutAnimationController);

            }

        });
        return root;
    }

    private void initView() {

        try {
            ((AppCompatActivity)getActivity())
                    .getSupportActionBar()
                    .setTitle(Common.categroySelected.getName());

        }catch (Exception e){}

        setHasOptionsMenu(true);
        recycler_food_list.setHasFixedSize(true);
        recycler_food_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_form_left);
    }
    public void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.action_search);



        SearchManager searchManager =(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        ImageView searchButton = (ImageView) searchView.findViewById(R.id.search_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.wight)));
        }
        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.wight));
        searchEditText.setHintTextColor(getResources().getColor(R.color.wight));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Cler text
        ImageView closeButton =(ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(v -> {
            EditText ed=(EditText)searchView.findViewById(R.id.search_src_text);
            ed.setText("");
            searchView.setQuery("",false);
            searchView.onActionViewCollapsed();
            menuItem.collapseActionView();
            foodListViewModel.getMutableLiveDataFoodList();

        });
    }

    private void startSearch(String query) {
        List<FoodModel> resultList = new ArrayList<>();
        for (int i=0; i<Common.categroySelected.getFoods().size();i++)
        {
            FoodModel categoryModel=Common.categroySelected.getFoods().get(i);
            if (categoryModel.getName().toLowerCase().contains(query))
                resultList.add(categoryModel);

        }
        foodListViewModel.getMutableLiveDataFoodList().setValue(resultList);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemback());
       // EventBus.getDefault().postSticky(new ChangeMenuClick(true));

        super.onDestroy();
    }


}