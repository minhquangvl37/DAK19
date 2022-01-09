package org.o7planning.dak19.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.AddPromoCodesActivity;
import org.o7planning.dak19.models.ModelPromotionCode;

import java.util.ArrayList;

public class AdapterPromotionCodeAdmin extends RecyclerView.Adapter<AdapterPromotionCodeAdmin.HolderPromoCode> {

    private Context context;
    public ArrayList<ModelPromotionCode> promoList;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public AdapterPromotionCodeAdmin(Context context, ArrayList<ModelPromotionCode> promoList) {
        this.context = context;
        this.promoList = promoList;
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng đợi...");

        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderPromoCode onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_promotioncode_admin,parent,false);
        return new HolderPromoCode(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPromoCode holder, int position) {
        //get data
        ModelPromotionCode modelPromotionCode=promoList.get(position);
        String id=modelPromotionCode.getId();
        String timestamp=modelPromotionCode.getTimestamp();
        String promoDescription=modelPromotionCode.getPromoDescription();
        String code=modelPromotionCode.getPromoCode();
        String promoPrice=modelPromotionCode.getPromoPrice();
        String minimumOrderPrice=modelPromotionCode.getMinimumOrderPrice();
        String expireDate=modelPromotionCode.getExpireDate();


        //set data
        holder.promoCodeTv.setText(code+"");
        holder.descriptionTv.setText(""+promoDescription);
        holder.promoPriceTv.setText(promoPrice +" VND");
        holder.minumumPriceTv.setText(minimumOrderPrice+" VND");
        holder.expireDateTv.setText(expireDate+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDeleteCode(modelPromotionCode,holder);
            }
        });

    }

    private void editDeleteCode(ModelPromotionCode modelPromotionCode, HolderPromoCode holder) {
        String  [] options={"Chỉnh sửa","Xóa"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Tùy chọn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            editPromoCode(modelPromotionCode);
                        }
                        else if(i==1){
                            deletePromocode(modelPromotionCode);
                        }
                    }
                })
                .show();
    }

    private void deletePromocode(ModelPromotionCode modelPromotionCode) {
        progressDialog.setMessage("Đang xóa...");
        progressDialog.show();



        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(modelPromotionCode.getId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context,"Đang xóa mã giảm giá..", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void editPromoCode(ModelPromotionCode modelPromotionCode) {
        Intent intent=new Intent(context, AddPromoCodesActivity.class);
        intent.putExtra("promoId",modelPromotionCode.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    class HolderPromoCode extends RecyclerView.ViewHolder{
        private TextView promoCodeTv,descriptionTv,expireDateTv,promoPriceTv,minumumPriceTv;
        public HolderPromoCode(@NonNull View itemView) {
            super(itemView);
            promoCodeTv=itemView.findViewById(R.id.promoCodeTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            promoPriceTv=itemView.findViewById(R.id.promoPriceTv);
            minumumPriceTv=itemView.findViewById(R.id.minumumPriceTv);
            expireDateTv=itemView.findViewById(R.id.expireDateTv);
        }
    }
}
