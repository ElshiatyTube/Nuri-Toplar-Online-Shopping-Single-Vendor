package com.eatitappclient.tws.Adapter;

import android.content.Context;
import android.media.MediaCodec;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eatitappclient.tws.Callback.IRecyclerClickListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.EventBus.CaregoryClick;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCategoriesAdapter extends RecyclerView.Adapter<MyCategoriesAdapter.MyViewHolder> {
    Context context;
    List<CategoryModel> categoryModelList;

    public MyCategoriesAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_category_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(categoryModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(categoryModelList.get(position).getName()));

        //EVENT
        holder.setListener((view, pos) -> {
            Common.categroySelected=categoryModelList.get(pos);
            EventBus.getDefault().postSticky(new CaregoryClick(true,categoryModelList.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public List<CategoryModel> getListCategory() {
        return categoryModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.category_image)
        ImageView category_image;
        @BindView(R.id.txt_category)
        TextView category_name;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryModelList.size()==1)
            return Common.DEFAULT_CLUMN_COUNT;
        else
        {
            if(categoryModelList.size() % 2 == 0)
                return Common.DEFAULT_CLUMN_COUNT;
            else
                return (position > 1 && position == categoryModelList.size()-1 ? Common.FULL_WIDTH_CLOUMN:Common.DEFAULT_CLUMN_COUNT);
        }
    }
}
