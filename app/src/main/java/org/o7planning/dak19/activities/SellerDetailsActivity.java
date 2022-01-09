package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.R;
import org.o7planning.dak19.adapter.AdapterOrderAdmin;
import org.o7planning.dak19.models.ModelOrderAdmin;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;

public class SellerDetailsActivity extends AppCompatActivity {
    //lay ma nhan vien tu adapter
    private String sellerId;

    //info Seller
    private String sellerName,sellerPhone,sellerAddress,sellerCity,sellerCountry,sellerLatitude,
                    sellerLongtitude;

    private TextView masellerTv,nameTv,phoneTv,addressTv,filteredOrdersTv;
    private ImageButton backBtn,callBtn,mapBtn,deleteBtn,filterOrderBtn,chartBtn;
    private ImageView profileIv;
    private EditText searchOrderEt;
    private RecyclerView ordersRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderAdmin> orderAdminArrayList;
    private AdapterOrderAdmin adapterOrderAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_details);

        //init from ui
        masellerTv=findViewById(R.id.masellerTv);
        backBtn=findViewById(R.id.backBtn);
        nameTv=findViewById(R.id.nameTv);
        phoneTv=findViewById(R.id.phoneTv);
        addressTv=findViewById(R.id.addressTv);
        profileIv=findViewById(R.id.profileIv);
        callBtn=findViewById(R.id.callBtn);
        mapBtn=findViewById(R.id.mapBtn);
        deleteBtn=findViewById(R.id.deleteBtn);
        searchOrderEt=findViewById(R.id.searchOrderEt);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);
        filteredOrdersTv=findViewById(R.id.filteredOrdersTv);
        ordersRv=findViewById(R.id.ordersRv);
        chartBtn=findViewById(R.id.chartBtn);

        sellerId=getIntent().getStringExtra("sellerId");

        firebaseAuth=FirebaseAuth.getInstance();

        loadInfoSeller();
        loadOrdersOfSeller();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(SellerDetailsActivity.this,R.style.AlertDialogCustom);
                builder.setTitle("Xóa người dùng")
                        .setMessage("Bạn có muốn xóa tài khoản này?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(sellerId);
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

        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SellerDetailsActivity.this, ChartSellerActivity.class);
                intent.putExtra("sellerId",sellerId);
                startActivity(intent);
            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options={"Tất cả đơn hàng","Đang chờ","Đã xác nhận","Đã hủy"};
                AlertDialog.Builder builder=new AlertDialog.Builder(SellerDetailsActivity.this);
                builder.setTitle("Lọc đơn hàng")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    //all clicked
                                    filteredOrdersTv.setText("Tất cả đơn hàng");
                                    adapterOrderAdmin.getFilter().filter("");
                                }
                                else {
                                    String optionClicked=options[i];
                                    filteredOrdersTv.setText(optionClicked+"");
                                    searchOrderEt.setText(optionClicked+"");
                                    adapterOrderAdmin.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });
    }

    private void loadOrdersOfSeller(){
        //init order list
        orderAdminArrayList=new ArrayList<>();

        //get orders
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderAdminArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String uid=""+ds.getRef().getKey();

                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(uid).child("Orders").orderByChild("orderBy").equalTo(sellerId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot ds:snapshot.getChildren()){
                                            ModelOrderAdmin modelOrderAdmin=ds.getValue(ModelOrderAdmin.class);

                                            //add to list
                                            orderAdminArrayList.add(modelOrderAdmin);
                                        }
                                        //setup adapter
                                        adapterOrderAdmin=new AdapterOrderAdmin(SellerDetailsActivity.this,orderAdminArrayList);
                                        //set to recyclerview
                                        ordersRv.setAdapter(adapterOrderAdmin);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadInfoSeller(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerAddress=""+snapshot.child("address").getValue();
                sellerCity=""+snapshot.child("city").getValue();
                sellerCountry=""+snapshot.child("country").getValue();
                sellerLatitude=""+snapshot.child("latitude").getValue();
                sellerLongtitude=""+snapshot.child("longtitude").getValue();
                sellerName=""+snapshot.child("name").getValue();
                sellerPhone=""+snapshot.child("phone").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();

                String addressBit="";
                if(sellerAddress.length()>30)
                {
                     addressBit=sellerAddress.substring(0,30)+"...";
                }
                else {
                     addressBit=sellerAddress;
                }
                //set data
                nameTv.setText(sellerName);
                phoneTv.setText(sellerPhone+" ");
                addressTv.setText(addressBit+" ");

                try{
                    Picasso.get().load(profileImage).into(profileIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteProduct(String sellerId){
        //delete product by id
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(sellerId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SellerDetailsActivity.this,"Đang xóa sản phẩm...",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SellerDetailsActivity.this, MainAdminActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellerDetailsActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void dialPhone() {
//        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("Gọi đến số:"+Uri.encode(shopPhone))));
//        Toast.makeText(this,""+shopName,Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+Uri.encode(sellerPhone)));
/*
Intent i = new Intent(Intent.ACTION_DIAL);
i.setData(Uri.parse("tel:0612312312"));
if (i.resolveActivity(getPackageManager()) != null) {
      startActivity(i);
}*/
        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(i);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }
}