package com.eatitappclient.tws.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eatitappclient.tws.Callback.IRecyclerClickListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.Database.CartItem;
import com.eatitappclient.tws.Model.Order;
import com.eatitappclient.tws.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

   private Context context;
   private List<Order> orderList;
   private Calendar calendar;
   private SimpleDateFormat simpleDateFormat;
    List<Integer> numbers,numbers2;

    public MyOrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public Order getItemAtPosition(int pos)
    {
        return orderList.get(pos);
    }
    public void setItemAtPosition(int pos,Order item)
    {
        orderList.set(pos,item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(orderList.get(position).getCartItemList().get(0).getFoodImage())
                .into(holder.img_order);
        calendar.setTimeInMillis(orderList.get(position).getCreateDate());
        if (position==0){
            holder.img_swipe.setVisibility(View.VISIBLE);
        }
        Date  date=new Date(orderList.get(position).getCreateDate());
        holder.txt_order_date.setText(new StringBuilder(context.getResources().getString(R.string.orderdate)).append(" ")
        .append(simpleDateFormat.format(date)));
        holder.txt_order_number.setText(new StringBuilder(context.getResources().getString(R.string.OrderNo)).append(" ").append(orderList.get(position).getOrderNumber()));
        if(orderList.get(position).getComment().equals("0")||orderList.get(position).getComment().equals("") ||orderList.get(position).getComment().equals(null))
        {
            holder.txt_order_comment.setVisibility(View.GONE);
        }
        else
        {
            holder.txt_order_comment.setText(new StringBuilder(context.getResources().getString(R.string.Commentt)).append(" ").append(orderList.get(position).getComment()));
        }

        holder.txt_order_status.setText(new StringBuilder(context.getResources().getString(R.string.Status)).append(" ").append(Common.convertStatusToText(orderList.get(position).getOrderStatus(),context)));

        extractNumbers2(orderList.get(position).getBranch());
        holder.txt_order_total.setText(new StringBuilder(context.getResources().getString(R.string.Pricee)).append(" ")
                .append(orderList.get(position).getTotalPayment()).append(" + ").append(numbers2.get(0)).append(" ").append(context.getResources().getString(R.string.DeliveryPrice)));

        holder.setRecyclerClickListener((view, pos) -> showDialog(orderList.get(pos).getCartItemList()));
    }

    public List<Integer> extractNumbers2(String s){
        numbers2 = new ArrayList<Integer>();

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);

        while(m.find()){
            numbers2.add(Integer.parseInt(m.group()));
        }
        Log.d("num: ",numbers2.get(0).toString());
        return numbers2;

    }


    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog=LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        builder.setView(layout_dialog);


        Button btn_ok=(Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail =(RecyclerView)layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(context,layoutManager.getOrientation()));

        MyOrderDetailAdapter myOrderDetailAdapter=new MyOrderDetailAdapter(context,cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);

        //Show Dialog
        AlertDialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        btn_ok.setOnClickListener(view -> dialog.dismiss());
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;
        @BindView(R.id.txt_order_comment)
        TextView txt_order_comment;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.img_order)
        ImageView img_order;
        @BindView(R.id.img_swipe)
        ImageView img_swipe;

        @BindView(R.id.txt_order_total)
        TextView txt_order_total;


        Unbinder unbinder;

        IRecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            recyclerClickListener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
