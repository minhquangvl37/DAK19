package org.o7planning.dak19.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.o7planning.dak19.models.ModelCustomer;
import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.CustomerActivity;

import java.util.ArrayList;

public class AdapterCustomer extends RecyclerView.Adapter<AdapterCustomer.HolderCustomerSeller>{
    private Context context;
    public ArrayList<ModelCustomer> customerList;

    public AdapterCustomer(Context context, ArrayList<ModelCustomer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public HolderCustomerSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_customer,parent,false);
        return new HolderCustomerSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCustomerSeller holder, int position) {
        //get data
        ModelCustomer modelCustomer=customerList.get(position);
        String customerIcon=modelCustomer.getProfileImage();
        String uid=modelCustomer.getUid();
        String customerId=modelCustomer.getTimestamp();
        String name=modelCustomer.getName();
        String phone=modelCustomer.getPhone();
        String country=modelCustomer.getCountry();
        String city=modelCustomer.getCity();
        String address=modelCustomer.getAddress();
        String latitude=modelCustomer.getLatitude();
        String longtitude=modelCustomer.getLongtitude();
        String time=modelCustomer.getTimestamp();

        //set data
        holder.customerNameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
        try {
            Picasso.get().load(customerIcon).placeholder(R.drawable.ic_cart).into(holder.customerIv);
        }catch (Exception e){
            holder.customerIv.setImageResource(R.drawable.ic_cart);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CustomerActivity.class);
                intent.putExtra("customerId",customerId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class HolderCustomerSeller extends RecyclerView.ViewHolder{
        private ImageView customerIv;
        private TextView addressTv,phoneTv,customerNameTv;

        public HolderCustomerSeller(@NonNull View itemView) {
            super(itemView);
            customerNameTv=itemView.findViewById(R.id.customerNameTv);
            customerIv=itemView.findViewById(R.id.customerIv);
            addressTv=itemView.findViewById(R.id.addressTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
        }
    }
}
