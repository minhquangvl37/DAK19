package org.o7planning.dak19.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.dak19.FilterOrderAdmin;
import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.MainAdminActivity;
import org.o7planning.dak19.activities.OrderDetailsAdminActivity;
import org.o7planning.dak19.models.ModelOrderAdmin;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderAdmin extends RecyclerView.Adapter<AdapterOrderAdmin.HolderOrderAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderAdmin> orderAdminArrayList, filterList;
    private FilterOrderAdmin filter;

    public AdapterOrderAdmin(Context context, ArrayList<ModelOrderAdmin> orderAdminArrayList) {
        this.context = context;
        this.orderAdminArrayList = orderAdminArrayList;
        this.filterList=orderAdminArrayList;
    }

    @NonNull
    @Override
    public HolderOrderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_order_admin,parent,false);
       return new HolderOrderAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderAdmin holder, int position) {
        //get data
        ModelOrderAdmin modelOrderAdmin=orderAdminArrayList.get(position);
        String orderId=modelOrderAdmin.getOrderId();
        String orderBy=modelOrderAdmin.getOrderBy();
        String orderCost=modelOrderAdmin.getOrderCost();
        String orderStatus=modelOrderAdmin.getOrderStatus();
        String orderTime=modelOrderAdmin.getOrderTime();
        String orderTo=modelOrderAdmin.getOrderTo();

        holder.orderIdTv.setText("Mã HĐ: "+orderId+"");
        holder.statusTv.setText(orderStatus);
        holder.amountTv.setText(orderCost+" VND");

        //load thong tin khach hang
        loadCustomerInfo(modelOrderAdmin,holder);
        //load thong tin nhan vien
        loadSellerInfo(modelOrderAdmin,holder);

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
                Intent intent=new Intent(context, OrderDetailsAdminActivity.class);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderBy",orderBy);
                context.startActivity(intent);
                // z phải thêm dữ liệu
            }
        });
    }

    private void loadSellerInfo(ModelOrderAdmin modelOrderAdmin, HolderOrderAdmin holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderAdmin.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String sellerName=""+snapshot.child("name").getValue();
                        holder.sellerNameTv.setText(sellerName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCustomerInfo(ModelOrderAdmin modelOrderAdmin, HolderOrderAdmin holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderAdmin.getOrderBy()).child("Customers").child(modelOrderAdmin.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String customerName=""+snapshot.child("name").getValue();
                holder.CTNamtTv.setText(customerName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderAdminArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterOrderAdmin(this,filterList);
        }
        return filter;
    }


    class HolderOrderAdmin extends RecyclerView.ViewHolder{
        private TextView orderIdTv,dateTv,CTNamtTv,sellerNameTv,amountTv,statusTv;
        public HolderOrderAdmin(@NonNull View itemView) {
            super(itemView);

            orderIdTv=itemView.findViewById(R.id.orderIdTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            CTNamtTv=itemView.findViewById(R.id.CTNamtTv);
            sellerNameTv=itemView.findViewById(R.id.sellerNameTv);
            amountTv=itemView.findViewById(R.id.amountTv);
            statusTv=itemView.findViewById(R.id.statusTv);
        }
    }
}
