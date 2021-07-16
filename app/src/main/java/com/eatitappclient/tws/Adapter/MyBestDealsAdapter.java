package com.eatitappclient.tws.Adapter;

import android.content.Context;
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
import com.eatitappclient.tws.EventBus.BestDealsItemClick;
import com.eatitappclient.tws.EventBus.FoodItemClick;
import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.R;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyBestDealsAdapter extends RecyclerView.Adapter<MyBestDealsAdapter.MyViewHolder> {


    Context context;
    List<BestDealsModel> bestDealsModelList;

    public MyBestDealsAdapter(Context context, List<BestDealsModel> bestDealsModelList) {
        this.context = context;
        this.bestDealsModelList = bestDealsModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(bestDealsModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(bestDealsModelList.get(position).getName()));

        //EVENT
        holder.setListener((view, pos) -> {
            Common.selectedBestDeals = bestDealsModelList.get(pos);
            Common.selectedBestDeals.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new BestDealsItemClick(true,bestDealsModelList.get(pos)));

        });
    }

    @Override
    public int getItemCount() {
        return bestDealsModelList.size();
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
}
