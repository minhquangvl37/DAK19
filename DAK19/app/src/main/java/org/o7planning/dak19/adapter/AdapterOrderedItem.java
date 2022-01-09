package org.o7planning.dak19.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.dak19.R;
import org.o7planning.dak19.models.ModelCartItem;
import org.o7planning.dak19.models.ModelOrderItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem> {

    private Context context;
    private ArrayList<ModelOrderItem> orderItemList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_order_item,parent,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {
        //lay du lieu
        ModelOrderItem modelOrderItem=orderItemList.get(position);
        String getpId=modelOrderItem.getpId();
        String cost=modelOrderItem.getCost();
        String price=modelOrderItem.getPrice();
        String name=modelOrderItem.getName();
        String quantity=modelOrderItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText(price+" VND");
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceTv.setText(cost+" VND");
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    //view holder
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //anh xa
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
