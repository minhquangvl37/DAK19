package org.o7planning.dak19.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.dak19.activities.CustomerActivity;
import org.o7planning.dak19.models.ModelCartItem;
import org.o7planning.dak19.R;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_cart_item,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        //get data
        ModelCartItem modelCartItem= cartItems.get(position);
        String id=modelCartItem.getId();
        String getpId=modelCartItem.getpId();
        String title=modelCartItem.getName();
        String cost=modelCartItem.getCost();
        String price=modelCartItem.getPrice();
        String quantity=modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceEachTv.setText(""+price);

        //remove texview
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will create table if not exists,but in that  case will must exists
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context,"Đang xóa sản phẩm khỏi giỏ hàng...",Toast.LENGTH_LONG).show();

                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                //adjust the subtotal after products remove
                double subTotalWithoutDiscount=((CustomerActivity)context).allTotalPrice;
                double subTotalAfterProductRemove=subTotalWithoutDiscount-Double.parseDouble(cost.replace(" VND",""));
                ((CustomerActivity)context).allTotalPrice=subTotalAfterProductRemove;
                ((CustomerActivity)context).sTotalTv.setText(String.format("%.0f",((CustomerActivity)context).allTotalPrice));

                //one subtotal is updated, update minumum price of promo code
                double promoPrice=Double.parseDouble(((CustomerActivity)context).promoPrice);

                //check if promodeCode is applied
                if(((CustomerActivity)context).isPromoCodeApplied){
                    //applied
                    if(subTotalAfterProductRemove<Double.parseDouble(((CustomerActivity) context).promoMinimumPrice)){
                        Toast.makeText(context,"Giá trị đơn hàng tối thiểu phải đạt "+(((CustomerActivity) context).promoMinimumPrice),Toast.LENGTH_LONG).show();
                        ((CustomerActivity)context).applyBtn.setVisibility(View.GONE);
                        ((CustomerActivity)context).promoDescriptionTv.setVisibility(View.GONE);
                        ((CustomerActivity)context).promoDescriptionTv.setText("");
                        ((CustomerActivity)context).discountTv.setText("0");
                        ((CustomerActivity)context).isPromoCodeApplied=false;

                        //
                        ((CustomerActivity)context).allTotalPriceTv.setText(String.format("%.0f",Double.parseDouble(String.format("%.0f",subTotalAfterProductRemove))));
                    }
                    else {
                        ((CustomerActivity)context).applyBtn.setVisibility(View.VISIBLE);
                        ((CustomerActivity)context).promoDescriptionTv.setVisibility(View.VISIBLE);
                        ((CustomerActivity)context).promoDescriptionTv.setText(((CustomerActivity)context).promoDescrtiption+"");

                        //
                        ((CustomerActivity)context).isPromoCodeApplied=true;
                        ((CustomerActivity)context).allTotalPriceTv.setText(String.format("%.0f",Double.parseDouble(String.format("%.0f",subTotalAfterProductRemove))-promoPrice));
                    }
                }
                else {
                    ((CustomerActivity)context).allTotalPriceTv.setText(String.format("%.0f",Double.parseDouble(String.format("%.0f",subTotalAfterProductRemove))));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        //ui views of row_cartitem.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv,itemRemoveTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);
            //init
            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);
        }
    }
}
