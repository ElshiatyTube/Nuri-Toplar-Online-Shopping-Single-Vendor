package com.eatitappclient.tws.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatitappclient.tws.Adapter.MyCategoriesAdapter;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Common.SpacesItemDecoration;
import com.eatitappclient.tws.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Unbinder unbinder;


/*    @BindView(R.id.recycler_popular)
    RecyclerView recycler_popular;
    @BindView(R.id.recycler_categories)
    RecyclerView recycler_categories;
    @BindView(R.id.viewpager)
    LoopingViewPager viewpager;

    @BindView(R.id.category_text)
    TextView category_text;
    @BindView(R.id.bestdeals_text)
    TextView bestdeals_text;
    @BindView(R.id.mostpopular_text)
    TextView mostpopular_text; */

    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;

    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        unbinder= ButterKnife.bind(this,root);
        initView();
        homeViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        homeViewModel.getCategoryListMutable().observe(getViewLifecycleOwner() ,categoryModelList -> {
            dialog.dismiss();
            adapter=new MyCategoriesAdapter(getContext(),categoryModelList);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
        });
        return root;
       /* if (Locale.getDefault().getLanguage().equals("ar")) {



            Typeface face = ResourcesCompat.getFont(getContext(), R.font.arabicfont);
            category_text.setTypeface(face);
            bestdeals_text.setTypeface(face);
            mostpopular_text.setTypeface(face);
        } */

    }





    private void initView() {
        try {
            ((AppCompatActivity)getActivity())
                    .getSupportActionBar()
                    .setTitle(getResources().getString(R.string.app_name));

        }catch (Exception e){}

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
    public void onResume() {
        super.onResume();
      //  viewpager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
       // viewpager.pauseAutoScroll();
        super.onPause();
    }


}