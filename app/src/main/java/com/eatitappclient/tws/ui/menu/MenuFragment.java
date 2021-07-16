package com.eatitappclient.tws.ui.menu;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatitappclient.tws.Adapter.MyCategoriesAdapter;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.SpacesItemDecoration;
import com.eatitappclient.tws.EventBus.MenuItemback;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        unbinder= ButterKnife.bind(this,root);
        initView();

        menuViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        menuViewModel.getCategoryListMutable().observe(getViewLifecycleOwner() ,categoryModelList -> {
            dialog.dismiss();
            adapter=new MyCategoriesAdapter(getContext(),categoryModelList);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initView() {
        setHasOptionsMenu(true);
        dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(true).setMessage(getResources().getString(R.string.loading)).build();
        dialog.show();
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_form_left);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null)
                {
                    switch (adapter.getItemViewType(position))
                    {
                        case Common.DEFAULT_CLUMN_COUNT:return 1;
                        case Common.FULL_WIDTH_CLOUMN:return 2;
                        default:return -1;
                    }
                }
                return -1;
            }
        });

        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new SpacesItemDecoration(8));

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
            menuViewModel.loadCategories();

        });
    }

    private void startSearch(String query) {
        List<CategoryModel> resultList = new ArrayList<>();
        for (int i=0; i<adapter.getListCategory().size();i++)
        {
            CategoryModel categoryModel=adapter.getListCategory().get(i);
            if (categoryModel.getName().toLowerCase().contains(query))
                resultList.add(categoryModel);

        }
        menuViewModel.getCategoryListMutable().setValue(resultList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemback());
        super.onDestroy();
    }

}