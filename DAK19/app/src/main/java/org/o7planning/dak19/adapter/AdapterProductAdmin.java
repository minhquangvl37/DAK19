package org.o7planning.dak19.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.FilterProduct;
import org.o7planning.dak19.models.ModelProduct;
import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.EditProductActivity;

import java.util.ArrayList;

public class AdapterProductAdmin extends RecyclerView.Adapter<AdapterProductAdmin.HolderProductAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;

    public AdapterProductAdmin(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList=productList;
    }

    @NonNull
    @Override
    public HolderProductAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_product,parent,false);
        return new HolderProductAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductAdmin holder, int position) {
        //get data
        ModelProduct modelProduct=productList.get(position);
        String id=modelProduct.getProductId();
        String uid=modelProduct.getUid();
        String discountAvaliable=modelProduct.getDiscountAvaliable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String productDescription=modelProduct.getProductDescription();
        String productIcon=modelProduct.getProductIcon();
        String productQuantity=modelProduct.getProductQuantity();
        String productTitle=modelProduct.getProductTitle();
        String timestamp=modelProduct.getTimestamp();
        String originalPrice=modelProduct.getOriginalPrice();

        //set data
        holder.titleTv.setText(productTitle);
        holder.quantityTv.setText(productQuantity);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText(discountPrice+" VND");
        holder.originalPriceTv.setText(originalPrice+" VND");
        holder.productDes.setText(productDescription+"");
        if(discountAvaliable.equals("true")){
            //product is discount
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //add strile throungh on orinal price
        }
        else {
            //product is not on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_cart).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_cart);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle item click, show item details
                detailsBottomSheet(modelProduct);
            }
        });

    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);

        //inflate view of bottomsheet
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_details,null);

        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //init
        ImageButton backBtn=view.findViewById(R.id.backBtn);
        ImageButton deleteBtn=view.findViewById(R.id.deleteBtn);
        ImageButton editBtn=view.findViewById(R.id.editBtn);
        ImageView productIconIv=view.findViewById(R.id.productIconIv);
        TextView discountNoteTv=view.findViewById(R.id.discountNoteTv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView discountPriceTv=view.findViewById(R.id.discountPriceTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);

        //get data
        String id=modelProduct.getProductId();
        String uid=modelProduct.getUid();
        String discountAvaliable=modelProduct.getDiscountAvaliable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String productDescription=modelProduct.getProductDescription();
        String productIcon=modelProduct.getProductIcon();
        String productQuantity=modelProduct.getProductQuantity();
        String productTitle=modelProduct.getProductTitle();
        String timestamp=modelProduct.getTimestamp();
        String originalPrice=modelProduct.getOriginalPrice();

        //set data
        titleTv.setText(productTitle);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(productQuantity);
        discountNoteTv.setText(discountNote);
        discountPriceTv.setText(discountPrice+" VND");
        originalPriceTv.setText(originalPrice+ " VND");

        if(discountAvaliable.equals("true")){
            discountPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //add strile throungh on orinal price
        }
        else {
            discountPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_cart).into(productIconIv);
        }catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_cart);
        }
        //show dialog
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit product activity
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.AlertDialogCustom);
                builder.setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có muốn xóa sản phẩm "+productTitle+"?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        //back click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id) {
        //delete product by id
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Orders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count=0;
                        //chạy thử
                        for(DataSnapshot ds:snapshot.getChildren()){
                            for(DataSnapshot ds1: ds.child("Items").getChildren())
                            {
                                String maSP= ds1.child("pId").getValue().toString();
                                if(maSP.equals(id)){
                                    count++;
                                }
                            }
                        }
                        if (count==0) {
                            reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue();
                        }
                        else {
                            Toast.makeText(context,"Sản phẩm đã tồn tại trong đơn hàng - Không thể xóa!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(context,"Đang xóa sản phẩm...",Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
//
//                    }
//                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProduct(this,filterList);
        }
        return filter;
    }

    class HolderProductAdmin extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,originalPriceTv,productDes;
        public HolderProductAdmin(@NonNull View itemView) {
            super(itemView);
            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);
            productDes=itemView.findViewById(R.id.productDes);
        }
    }
}
