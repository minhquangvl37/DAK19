package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.o7planning.dak19.CloudMess.APIService;
import org.o7planning.dak19.CloudMess.Client;
import org.o7planning.dak19.CloudMess.Data;
import org.o7planning.dak19.CloudMess.MyResponse;
import org.o7planning.dak19.CloudMess.Sender;
import org.o7planning.dak19.CloudMess.Token;
import org.o7planning.dak19.Constants;
import org.o7planning.dak19.R;
import org.o7planning.dak19.adapter.AdapterOrderAdmin;
import org.o7planning.dak19.adapter.AdapterOrderedItem;
import org.o7planning.dak19.models.ModelOrderAdmin;
import org.o7planning.dak19.models.ModelOrderItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CALL_PHONE;

public class OrderDetailsAdminActivity extends AppCompatActivity {
    //lay ma tu adapter AdapterOrderAdmin
    private String orderId,orderBy,orderTo;

    //ui view
    private ImageButton backBtn,editBtn,mapBtn;
    private TextView orderIdTv,sellerNameTv,dateTv,orderStatusTv,CTNameTv,phoneTv,
            totalItemsTv,amountTv,addressTv;
    private ImageView customerProfileIv;
    private RecyclerView itemsRv;

    //thong tin admin va khach hang, seller
    private String cusLongtitude,cusLatitude,adLongtitude,adLatitude, sellerName,customerName,customerPhone;
    
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_admin);

        backBtn=findViewById(R.id.backBtn);
        editBtn=findViewById(R.id.editBtn);
        mapBtn=findViewById(R.id.mapBtn);
        orderIdTv=findViewById(R.id.orderIdTv);
        sellerNameTv=findViewById(R.id.sellerNameTv);
        dateTv=findViewById(R.id.dateTv);
        orderStatusTv=findViewById(R.id.orderStatusTv);
        CTNameTv=findViewById(R.id.CTNameTv);
        phoneTv=findViewById(R.id.phoneTv);
        totalItemsTv=findViewById(R.id.totalItemsTv);
        amountTv=findViewById(R.id.amountTv);
        addressTv=findViewById(R.id.addressTv);
        customerProfileIv=findViewById(R.id.customerProfileIv);
        itemsRv=findViewById(R.id.itemsRv);

        orderId=getIntent().getStringExtra("orderId");
        orderBy=getIntent().getStringExtra("orderBy");
        orderTo=getIntent().getStringExtra("orderTo");

        firebaseAuth=FirebaseAuth.getInstance();

        loadAdminInfo();
        loadSellerInfo();
        loadCustomerInfo();
        loadOrderDetails();
        loadOrderedItems();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callToCustomer();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatusDialog();
            }
        });
    }

    private void editOrderStatusDialog() {
        final String[] options={"Đang chờ", "Đã xác nhận","Đã hủy"};
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi tình trạng đơn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption=options[which];
                        notify = true;
                        editOrderStatus(selectedOption);
                        //chạy thử xác nhận đi
                        //no de chu that bai

                    }
                })
                .show();
    }

    private void editOrderStatus(String selectedOption) {
        //setup data
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("orderStatus",""+selectedOption);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message="Đã cập nhật trạng thái "+selectedOption;
                        Toast.makeText(OrderDetailsAdminActivity.this,message,Toast.LENGTH_LONG).show();

                    //   prepareNotificationMessage(orderId,message);
                    //gui thong bao den seller sau khi da xac nhan don hang cho seller
                        if(notify){
                            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users");
                            refUser.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = snapshot.child("name").getValue().toString();
                                    sendNotification(orderBy, name, "Đã xác nhận");
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                        notify = false;
                        //save to Chart Firebase
                        if(selectedOption.equals("Đã xác nhận")){
                            setupChartFirebase(orderId);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsAdminActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseAuth.getCurrentUser().getUid()
                            , R.drawable.manager
                            , username + ": " + msg
                            , "Trạng thái đơn hàng"
                            , receiver
                            , "admin");
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
                                    Toast.makeText(OrderDetailsAdminActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupChartFirebase(String orderId) {
        //get value
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get order info
                        String orderBy=""+snapshot.child("orderBy").getValue();
                        String orderCost=""+snapshot.child("orderCost").getValue();
                        String orderId=""+snapshot.child("orderId").getValue();
                        String orderStatus=""+snapshot.child("orderStatus").getValue();
                        String orderTime=""+snapshot.child("orderTime").getValue();
                        String orderTo=""+snapshot.child("orderTo").getValue();
                        String latitude=""+snapshot.child("latitude").getValue();
                        String longtitude=""+snapshot.child("longtitude").getValue();

                        //convert time
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String dateFormated= DateFormat.format("dd/MM/yyyy",calendar).toString();
                       String date= dateFormated.replace("/","");

                        saveToChartFirebase(orderBy,dateFormated,orderCost,orderTime,date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //set value, then save values to firebase

    }

    private void saveToChartFirebase(String orderBy, String dateFormated, String orderCost,String orderTime,String date) {
        //setup order data
                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("orderTime",""+dateFormated);
                        hashMap.put("orderCost",""+orderCost);

        //save to firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy).child("ChartInfo").child(date).child(orderTime).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //db update
                        Toast.makeText(OrderDetailsAdminActivity.this,"...",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(OrderDetailsAdminActivity.this,"???",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadAdminInfo(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adLatitude=""+snapshot.child("latitude").getValue();
                        adLongtitude=""+snapshot.child("longtitude").getValue();
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

                        //set value
                        sellerNameTv.setText(sellerName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCustomerInfo(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy).child("Customers").child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String customerName=""+snapshot.child("name").getValue();
                        customerPhone=""+snapshot.child("phone").getValue();
                        String customerAddress=""+snapshot.child("address").getValue();
                        cusLatitude=""+snapshot.child("latitude").getValue();
                        cusLongtitude=""+snapshot.child("longtitude").getValue();
                        String customerProfile=""+snapshot.child("profileImage").getValue();

                        //set value
                        CTNameTv.setText(customerName);
                        phoneTv.setText(customerPhone);
                        addressTv.setText(customerAddress);
                        try {
                            Picasso.get().load(customerProfile).placeholder(R.drawable.ic_person_gray).into(customerProfileIv);
                        }
                        catch (Exception e){
                            customerProfileIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetails(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get order info
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

                        //convert time
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String dateFormated= DateFormat.format("dd/MM/yyyy",calendar).toString();

                        //orderStatus
                        if(orderStatus.equals("Đang chờ")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.yellow));
                        }
                        else if(orderStatus.equals("Đã xác nhận")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                        }
                        else if(orderStatus.equals("Đã hủy")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                        }

                        //set data
                        orderIdTv.setText(""+orderId);
                        orderStatusTv.setText(""+orderStatus);
                        dateTv.setText(dateFormated);
                        amountTv.setText(orderCost+" VND");

                        findAddress(latitude,longtitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderedItems(){
        //load the products/items of order
        //init list
        orderedItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear(); //before adding data, clear list
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelOrderItem modelOrderedItem=ds.getValue(ModelOrderItem.class);
                            //add to list
                            orderedItemArrayList.add(modelOrderedItem);
                        }

                        //setup data
                        adapterOrderedItem=new AdapterOrderedItem(OrderDetailsAdminActivity.this,orderedItemArrayList);
                        //set adapter
                        itemsRv.setAdapter(adapterOrderedItem);

                        //set total items
                        totalItemsTv.setText(""+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void openMap() {
        String address="https://maps.google.com/maps?saddr" + adLatitude + "," + adLongtitude + "&daddr=" + cusLatitude + "," + cusLongtitude;
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void findAddress(String latitude, String longtitude) {
        double lat=Double.parseDouble(latitude);
        double lon=Double.parseDouble(longtitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try{
            addresses=geocoder.getFromLocation(lat, lon, 1);

            //complete address
            String address=addresses.get(0).getAddressLine(0);
            addressTv.setText(address);
        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void callToCustomer() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + Uri.encode(customerPhone)));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(i);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }

    private void prepareNotificationMessage(String orderId,String message){
        //khi ad change order status thì thôgn bóa cho seller

        String NOTIFICATION_TOPIC="/topics/"+Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE="Đơn hàng của bạn "+orderId;
        String NOTIFICATION_MESSAGE=""+message;
        String NOTIFICATION_TYPE="OrderStatusChanged";

        //prepare json
        JSONObject notificationJo=new JSONObject();
        JSONObject notificationBodyJo=new JSONObject();
        try{
            //what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("adminUid",firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid",orderBy);
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC); //to all who subcribed to this topic
            notificationJo.put("data",notificationBodyJo);
        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //notification send
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //put required headers
                Map<String,String> headers=new HashMap<>();
                headers.put("Content_Type","application/json");
                headers.put("Authorization","key="+ Constants.FCM_KEY);
                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}