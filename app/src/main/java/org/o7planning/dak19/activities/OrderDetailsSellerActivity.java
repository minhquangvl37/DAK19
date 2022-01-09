package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
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
import org.o7planning.dak19.adapter.AdapterOrderedItem;
import org.o7planning.dak19.models.ModelOrderItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    private String orderTo,orderBy,orderId;

    private ImageButton backBtn,editStatusBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,sellerNameTv,
            totalItemsTv,amountTv,addressTv;
    private RecyclerView itemsRv;
    private ImageView customerProfileIv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        //anhxa
        backBtn=findViewById(R.id.backBtn);
        orderIdTv=findViewById(R.id.orderIdTv);
        dateTv=findViewById(R.id.dateTv);
        orderStatusTv=findViewById(R.id.orderStatusTv);
        sellerNameTv=findViewById(R.id.sellerNameTv);
        totalItemsTv=findViewById(R.id.totalItemsTv);
        amountTv=findViewById(R.id.amountTv);
        addressTv=findViewById(R.id.addressTv);
        itemsRv=findViewById(R.id.itemsRv);
        editStatusBtn=findViewById(R.id.editStatusBtn);
        customerProfileIv=findViewById(R.id.customerProfileIv);

        Intent intent=getIntent();
        orderTo=intent.getStringExtra("orderTo");
        orderBy=intent.getStringExtra("orderBy");
        orderId=intent.getStringExtra("orderId");

        firebaseAuth=FirebaseAuth.getInstance();
        loadSellerInfo();
        loadOrderedItems();
        loadOrderDetails();
        loadCustomerInfo();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderDetailsSellerActivity.this,MainSellerActivity.class));
                finish();
            }
        });

        editStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrderStatusDialog();
            }
        });
    }

    private void loadCustomerInfo(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Customers").child(orderTo)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImage=""+snapshot.child("profileImage").getValue();

                String addressBit="";

                //set data
                try{
                    Picasso.get().load(profileImage).into(customerProfileIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editOrderStatusDialog() {
        final String[] options={"Đã hủy"};
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi tình trạng đơn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption=options[which];
                        editOrderStatus(selectedOption);
                    }
                })
                .show();
    }

    private void editOrderStatus(String selectedOption) {
        //setup data
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("orderStatus",""+selectedOption);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderDetailsSellerActivity.this,"Đang cập nhật trạng thái "+selectedOption,Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadOrderDetails(){
        //load order detail
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String orderBy=""+snapshot.child("orderBy").getValue();
                        String orderCost=""+snapshot.child("orderCost").getValue();
                        String orderId=""+snapshot.child("orderId").getValue();
                        String orderStatus=""+snapshot.child("orderStatus").getValue();
                        String orderTime=""+snapshot.child("orderTime").getValue();
                        String orderTo=""+snapshot.child("orderTo").getValue();
                        String latitude=""+snapshot.child("latitude").getValue();
                        String longtitude=""+snapshot.child("longtitude").getValue();
                        String discount=""+snapshot.child("discount").getValue();

                        if(discount.equals("null") || discount.equals("0")){
                            discount="0";
                        }
                        else {
                            discount=""+discount;
                        }

                        //conver time
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate= DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

                        if(orderStatus.equals("Đang chờ")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.yellow));
                        }
                        else if(orderStatus.equals("Đã xác nhận")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                        }
                        else if(orderStatus.equals("Đã hủy")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                        }


                        //setdata
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText(""+orderCost+" "+"[Discount: "+discount+"]");
                        dateTv.setText(formatedDate);

                        findAddress(latitude,longtitude);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderedItems(){
        orderedItemArrayList =new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelOrderItem modelOrderedItem=ds.getValue(ModelOrderItem.class);
                            //add to list
                            orderedItemArrayList.add(modelOrderedItem);
                        }

                        //setup adapter
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSellerActivity.this,orderedItemArrayList);
                        //set adapter
                        itemsRv.setAdapter(adapterOrderedItem);

                        //set item
                        totalItemsTv.setText(""+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSellerInfo(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String sellerName=""+snapshot.child("name").getValue();
                        sellerNameTv.setText(sellerName+"");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void findAddress(String latitude, String longtitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longtitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0);
            addressTv.setText(address);
        }
        catch (Exception e) {

        }
    }
}