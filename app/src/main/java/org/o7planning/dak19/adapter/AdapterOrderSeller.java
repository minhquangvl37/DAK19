package org.o7planning.dak19.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.FilterOrderAdmin;
import org.o7planning.dak19.FilterOrderSeller;
import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.OrderDetailsSellerActivity;
import org.o7planning.dak19.models.ModelOrderSeller;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderSeller> orderSellerList,filterList;
    private FilterOrderSeller filter;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderSellerList) {
        this.context = context;
        this.orderSellerList = orderSellerList;
        this.filterList=orderSellerList;
    }

    @NonNull
    @Override
    public HolderOrderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_order_seller,parent,false);
        return new HolderOrderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderSeller holder, int position) {
        //get dât
        ModelOrderSeller modelOrderSeller=orderSellerList.get(position);
        String orderId=modelOrderSeller.getOrderId();
        String orderBy=modelOrderSeller.getOrderBy();
        String orderCost=modelOrderSeller.getOrderCost();
        String orderStatus=modelOrderSeller.getOrderStatus();
        String orderTime=modelOrderSeller.getOrderTime();
        String orderTo=modelOrderSeller.getOrderTo();

        //load seller info
        //loadCustomerInfo(modelOrderSeller,holder);

        //set data
        holder.amountTv.setText("Thành tiền  "+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Mã đơn hàng "+orderId);
        loadCustomerInfo(modelOrderSeller,holder);

        if(orderStatus.equals("Đang chờ")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        else if(orderStatus.equals("Đã xác nhận")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if(orderStatus.equals("Đã hủy")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.red));
        }
        //convert time
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate= DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, OrderDetailsSellerActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderBy",orderBy);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadCustomerInfo(ModelOrderSeller modelOrderSeller, HolderOrderSeller holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderSeller.getOrderBy()).child("Customers").child(modelOrderSeller.getOrderTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String customerName=""+snapshot.child("name").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();

                //set value
                holder.customerNamtTv.setText("Tên khách hàng "+customerName);
                try{
                    Picasso.get().load(profileImage).into(holder.profileIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return orderSellerList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterOrderSeller(this,filterList);
        }
        return filter;
    }


    class HolderOrderSeller extends RecyclerView.ViewHolder{

        private TextView orderIdTv,dateTv,customerNamtTv,amountTv,statusTv;
        private ImageView profileIv;

        public HolderOrderSeller(@NonNull View itemView) {
            super(itemView);
            orderIdTv=itemView.findViewById(R.id.orderIdTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            customerNamtTv=itemView.findViewById(R.id.CTNamtTv);
            amountTv=itemView.findViewById(R.id.amountTv);
            statusTv=itemView.findViewById(R.id.statusTv);
            profileIv=itemView.findViewById(R.id.profileIv);
        }
    }
}
