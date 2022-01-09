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

import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.CustomerActivity;
import org.o7planning.dak19.activities.SellerDetailsActivity;
import org.o7planning.dak19.models.ModelCustomer;
import org.o7planning.dak19.models.ModelSeller;

import java.util.ArrayList;

public class AdapterSeller extends RecyclerView.Adapter<AdapterSeller.HolderAdapterSeller> {

    private Context context;
    public ArrayList<ModelSeller> sellerList;

    public AdapterSeller(Context context, ArrayList<ModelSeller> sellerList) {
        this.context = context;
        this.sellerList = sellerList;
    }

    @NonNull
    @Override
    public HolderAdapterSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_seller_admin,parent,false);
       return new HolderAdapterSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdapterSeller holder, int position) {
            //get data
        ModelSeller modelSeller=sellerList.get(position);
        String profileImage=modelSeller.getProfileImage();
        String uid=modelSeller.getUid();
        String name=modelSeller.getName();
        String phone=modelSeller.getPhone();
        String country=modelSeller.getCountry();
        String city=modelSeller.getCity();
        String address=modelSeller.getAddress();
        String latitude=modelSeller.getLatitude();
        String longtitude=modelSeller.getLongtitude();
        String time=modelSeller.getTimestamp();

        //set data
        holder.sellerNameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_cart).into(holder.sellerIv);
        }catch (Exception e){
            holder.sellerIv.setImageResource(R.drawable.ic_cart);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SellerDetailsActivity.class);
                intent.putExtra("sellerId",uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellerList.size();
    }

    class HolderAdapterSeller extends RecyclerView.ViewHolder{
        private ImageView sellerIv;
        private TextView sellerNameTv,phoneTv,addressTv;
        public HolderAdapterSeller(@NonNull View itemView) {
            super(itemView);

            sellerIv=itemView.findViewById(R.id.sellerIv);
            sellerNameTv=itemView.findViewById(R.id.sellerNameTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
            addressTv=itemView.findViewById(R.id.addressTv);
        }
    }
}
