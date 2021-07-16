package com.eatitappclient.tws.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eatitappclient.tws.Callback.IRecyclerClickListener;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.EventBus.CaregoryClick;
import com.eatitappclient.tws.Model.BranchesModel;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyBranchesAdapter extends RecyclerView.Adapter<MyBranchesAdapter.MyViewHolder> {
    Context context;
    List<BranchesModel> branchesModelList;

    public MyBranchesAdapter(Context context, List<BranchesModel> branchesModelList) {
        this.context = context;
        this.branchesModelList = branchesModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyBranchesAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_braches_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(branchesModelList.get(position).getBranchImage())
                .into(holder.thumbnail);
        holder.txt_branch_name.setText(new StringBuilder(branchesModelList.get(position).getBranchName()));
        holder.txt_branch_address.setText(new StringBuilder(context.getResources().getString(R.string.BranchAddress)).append(" ")
                .append(branchesModelList.get(position).getBranchAddress()));
        holder.txt_branch_phone.setText(new StringBuilder(context.getResources().getString(R.string.BranchPhone)).append(" ")
                .append(branchesModelList.get(position).getBranchPhone()));
        holder.txt_branch_location.setText(new StringBuilder(branchesModelList.get(position).getMapLinkLocation()));

        //EVENT
        holder.setListener((view, pos) -> {

            branchInfoDialog(pos);

           /* if (branchesModelList.get(position).getMapLinkLocation()!=null ||
                    !branchesModelList.get(position).getMapLinkLocation().equals(""))
            {
                try {
                    Uri uri = Uri.parse(branchesModelList.get(position).getMapLinkLocation());
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context, context.getResources().getString(R.string.locationnull), Toast.LENGTH_SHORT).show();

                }

            }
            else {
                Toast.makeText(context, context.getResources().getString(R.string.locationnull), Toast.LENGTH_SHORT).show();
            } */

        });

    }

    private void branchInfoDialog(int pos) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // add a list
        String[] animals = {context.getResources().getString(R.string.PhoneCall),context.getResources().getString(R.string.OpenLocation)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Phone Call
                        dialContactPhone(branchesModelList.get(pos).getBranchPhone());
                        break;
                    case 1: // Open Location on Map
                        try {
                            Uri uri = Uri.parse(branchesModelList.get(pos).getMapLinkLocation());
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                            context.startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(context, context.getResources().getString(R.string.locationnull), Toast.LENGTH_SHORT).show();

                        }
                        break;

                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dialContactPhone(final String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    public List<BranchesModel> getBranchesModelList() {
        return branchesModelList;
    }

    @Override
    public int getItemViewType(int position) {
        if (branchesModelList.size()==1)
            return Common.DEFAULT_CLUMN_COUNT;
        else
        {
            if(branchesModelList.size() % 2 == 0)
                return Common.DEFAULT_CLUMN_COUNT;
            else
                return (position > 1 && position == branchesModelList.size()-1 ? Common.FULL_WIDTH_CLOUMN:Common.DEFAULT_CLUMN_COUNT);
        }
    }

    @Override
    public int getItemCount() {
        return branchesModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        @BindView(R.id.txt_branch_name)
        TextView txt_branch_name;
        @BindView(R.id.txt_branch_address)
        TextView txt_branch_address;
        @BindView(R.id.txt_branch_phone)
        TextView txt_branch_phone;
        @BindView(R.id.txt_branch_location)
        TextView txt_branch_location;

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
