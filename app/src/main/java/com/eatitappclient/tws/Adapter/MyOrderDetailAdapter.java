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
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Model.AddonModel;
import com.eatitappclient.tws.Model.SizeModel;
import com.eatitappclient.tws.R;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHolder> {
    Context context;
    List<CartItem> cartItemList;
    Gson gson;


    public MyOrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_order_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            Glide.with(context).load(cartItemList.get(position).getFoodImage())
                    .into(holder.img_food_image);

            holder.txt_food_quantity.setText(new StringBuilder(context.getResources().getString(R.string.Quantity)).append(" ").append(cartItemList.get(position).getFoodQuantity()));
            holder.txt_food_name.setText(new StringBuilder("").append(cartItemList.get(position).getFoodName()));
            holder.txt_food_price.setText(new StringBuilder(context.getResources().getString(R.string.pricesinglepro)).append(cartItemList.get(position).getFoodPrice()));



          /*  try {
                SizeModel sizeModel = gson.fromJson(cartItemList.get(position).getFoodSize(), new TypeToken<SizeModel>() {
                }.getType());
                if (sizeModel != null)
                    holder.txt_size.setText(new StringBuilder(context.getResources().getString(R.string.Size)).append(" ").append(sizeModel.getName()));
            }catch (Exception e){}



            try {
                if (!cartItemList.get(position).getFoodAddon().equals("Default")) {
                    List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getFoodAddon(), new TypeToken<List<AddonModel>>() {
                    }.getType());
                    StringBuilder addonString = new StringBuilder();
                    if (addonModels != null) {
                        for (AddonModel addonModel : addonModels)
                            addonString.append(addonModel.getName()).append(",");
                        addonString.delete(addonString.length() - 1, addonString.length());
                        holder.txt_food_add_on.setText(new StringBuilder(context.getResources().getString(R.string.Addon)).append(" ").append(addonString));
                    }

                } else
                    holder.txt_food_add_on.setVisibility(View.GONE);
                // holder.txt_food_add_on.setText(new StringBuilder("Addon: Default"));
            }catch (Exception e){} */



    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_add_on)
        TextView txt_food_add_on;
        @BindView(R.id.txt_size)
        TextView txt_size;
        @BindView(R.id.txt_food_quantity)
        TextView txt_food_quantity;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;




        private Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
