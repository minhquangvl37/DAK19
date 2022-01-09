package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.o7planning.dak19.CloudMess.APIService;
import org.o7planning.dak19.CloudMess.Client;
import org.o7planning.dak19.CloudMess.Data;
import org.o7planning.dak19.CloudMess.FcmNotificationsSender;
import org.o7planning.dak19.CloudMess.MyResponse;
import org.o7planning.dak19.CloudMess.Sender;
import org.o7planning.dak19.CloudMess.Token;
import org.o7planning.dak19.Constants;
import org.o7planning.dak19.adapter.AdapterCartItem;
import org.o7planning.dak19.adapter.AdapterOrderSeller;
import org.o7planning.dak19.adapter.AdapterProductCustomer;
import org.o7planning.dak19.adapter.AdapterProductSeller;
import org.o7planning.dak19.models.ModelCartItem;
import org.o7planning.dak19.models.ModelOrderSeller;
import org.o7planning.dak19.models.ModelProduct;
import org.o7planning.dak19.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CALL_PHONE;

public class CustomerActivity extends AppCompatActivity {
    //ui views
    private TextView nameTv,phoneTv,addressTv,makh,filteredOrdersTv,filteredProductsTv,
                        tabProductsTv,tabOrdersTv,cartCountTv;
    private ImageView profileIv;
    private ImageButton editProfileBtn,backBtn,callBtn,mapBtn,filterProductBtn,filterOrderBtn,cartBtn;
    private EditText searchProductEt,searchOrderEt;

    private String customerId;
    private String adminUid="ky0gFPTgkgcLVhiKBWaNmYbW5652";
    //in4 seller
    private String mLongtitude, mLatitude, mPhone, mName;
    //in4 customer
    private String customerName, customerPhone, customerAddress,customerLongtitude, customerCity, customerCountry,
                    customerLatitude;

    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private RelativeLayout productsRl,ordersRl;
    private RecyclerView productsRv,ordersRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductCustomer adapterProductCustomer;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private ArrayList<ModelOrderSeller> orderSellerList;
    private AdapterOrderSeller adapterOrderSeller;

    private EasyDB easyDB;

    APIService apiService;

    boolean notify = false;

    private DatabaseReference refToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        nameTv=findViewById(R.id.nameTv);
        phoneTv=findViewById(R.id.phoneTv);
        addressTv=findViewById(R.id.addressTv);
        profileIv=findViewById(R.id.profileIv);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        backBtn=findViewById(R.id.backBtn);
        callBtn=findViewById(R.id.callBtn);
        mapBtn=findViewById(R.id.mapBtn);
        searchOrderEt=findViewById(R.id.searchOrderEt);
        searchProductEt=findViewById(R.id.searchProductEt);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);
        filteredProductsTv=findViewById(R.id.filteredProductsTv);
        filteredOrdersTv=findViewById(R.id.filteredOrdersTv);
        tabProductsTv=findViewById(R.id.tabProductsTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        cartCountTv=findViewById(R.id.cartCount);
        cartBtn=findViewById(R.id.cartBtn);

        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);

        productsRv = findViewById(R.id.productsRv);
        ordersRv = findViewById(R.id.ordersRv);

        //init progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        customerId=getIntent().getStringExtra("customerId");

        firebaseAuth=FirebaseAuth.getInstance();
    //    refToken = FirebaseDatabase.getInstance().getReference().child("Token").child(firebaseAuth.getUid());

        loadMyInfo();
        loadCustomerDetail();
        loadAllProducts();
        showProductsUI();
        loadAllOrder();

        //Tới chỗ nhấn để gọi hàm đặt hàng đi m

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String token = task.getResult();
                    updateToken(token);
                });

        //declare it to class level and init onCreate
        easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();

        //each shop have its own procducts and orders so if user add items to cart and go back and open cart in different shop then should be different
        //so delete cart data when user open this actitivy
        deleteCartData();
        cartCount();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onBackPressed();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerActivity.this,EditProfileCustomerActivity.class);
                intent.putExtra("customerId",customerId);
                startActivity(intent);
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCartDialog();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(CustomerActivity.this);
                builder.setTitle("Chọn loại sản phẩm!")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected=Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("Tất cả sản phẩm")){
                                    //load all products
                                    loadAllProducts();
                                }
                                else {
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }
                            }
                        }).show();
            }
        });

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductCustomer.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadAllOrder() {
        //init order list
        orderSellerList=new ArrayList<>();

        //get orders
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderSellerList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String uid=""+ds.getRef().getKey();

                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderTo").equalTo(customerId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot ds:snapshot.getChildren()){
                                            ModelOrderSeller modelOrderSeller=ds.getValue(ModelOrderSeller.class);

                                            //add to list
                                            orderSellerList.add(modelOrderSeller);
                                        }
                                        //setup adapter
                                        adapterOrderSeller=new AdapterOrderSeller(CustomerActivity.this,orderSellerList);
                                        //set to recyclerview
                                        ordersRv.setAdapter(adapterOrderSeller);
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

    public double allTotalPrice=0.0;
    public TextView sTotalTv,dFeeTv,allTotalPriceTv,promoDescriptionTv,discountTv;
    public EditText promoCodeEt;
    public FloatingActionButton validateBtn;
    public Button applyBtn;

    private void showCartDialog() {
        //init list
        cartItemList=new ArrayList<>();
        //inflater cart layout
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views
        TextView nameSellerTv=view.findViewById(R.id.nameSellerTv);
        RecyclerView cardItemsRv=view.findViewById(R.id.cardItemsRv);
        sTotalTv=view.findViewById(R.id.sTotalTv);
        allTotalPriceTv=view.findViewById(R.id.totalTv);
        promoCodeEt=view.findViewById(R.id.promoCodeEt);
        validateBtn=view.findViewById(R.id.validateBtn);
        promoDescriptionTv=view.findViewById(R.id.promoDescriptionTv);
        applyBtn=view.findViewById(R.id.applyBtn);
        discountTv=view.findViewById(R.id.discountTv);
        Button checkoutBtn=view.findViewById(R.id.checkoutBtn);

        if(isPromoCodeApplied){
            promoDescriptionTv.setVisibility(View.VISIBLE);
            applyBtn.setVisibility(View.VISIBLE);
            applyBtn.setText("Đã áp dụng");
            promoCodeEt.setText(promoCode);
            promoDescriptionTv.setText(promoDescrtiption);
        }
        else {
            promoDescriptionTv.setVisibility(View.GONE);
            applyBtn.setVisibility(View.GONE);
            applyBtn.setText("Áp dụng");
        }

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);
        nameSellerTv.setText("Nhân Viên: "+mName);

        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();

        //get all records from db
        Cursor res=easyDB.getAllData();
        while (res.moveToNext()){
            String id=res.getString(1);
            String pId=res.getString(2);
            String name=res.getString(3);
            String price=res.getString(4);
            String cost=res.getString(5);
            String quantity=res.getString(6);

            allTotalPrice=allTotalPrice+Double.parseDouble(cost);

            ModelCartItem modelCartItem=new ModelCartItem(""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity);

            cartItemList.add(modelCartItem);


        }
        //setup adapter
        adapterCartItem=new AdapterCartItem(this,cartItemList);
        //set to recyclerview
        cardItemsRv.setAdapter(adapterCartItem);

//        sTotalTv.setText(allTotalPrice+" VND");
//
//        //xoa phan thap phan
//        String totalPrice=String.format("%.0f",allTotalPrice);
//        allTotalPriceTv.setText(totalPrice+" VND");

        if(isPromoCodeApplied){
            priceWithDiscount();
        }
        else {
            priceWithoutDiscount();
//            sTotalTv.setText(allTotalPrice+" VND");
//            String totalPrice=String.format("%.0f",allTotalPrice);
//            allTotalPriceTv.setText(totalPrice+" VND");
        }

        //show dialog
        AlertDialog dialog=builder.create();
        dialog.show();
        //reset total price on dialog dissmiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice=0.0;
            }
        });

        //place order
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address
                if(customerLatitude.equals("")  || customerLongtitude.equals("") ){
                    Toast.makeText(CustomerActivity.this,"Vui lòng cập nhật địa chỉ vào thông tin của bạn!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(customerPhone.equals("") || customerPhone.equals("null")){
                    Toast.makeText(CustomerActivity.this,"Vui lòng cập nhật số điện thoại vào thông tin của bạn!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(cartItemList.size()==0){
                    Toast.makeText(CustomerActivity.this,"Không có sản phẩm nào trong giỏ hàng!",Toast.LENGTH_LONG).show();
                    return;
                }
                submitOrder();
                cartCountTv.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        //start validating promocode when validate button pressed
        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String promotionCode=promoCodeEt.getText().toString().trim();
                if(TextUtils.isEmpty(promotionCode)){
                    Toast.makeText(CustomerActivity.this,"Vui lòng điền mã giảm giá!",Toast.LENGTH_LONG).show();
                }
                else {
                    checkCodeValiable(promotionCode);
                }
            }
        });


        //aps dụng mã nếu mã hợp lệ,vì nút áp dụng sẽ k hiện ra nếu mã k hợp
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPromoCodeApplied=true;
                applyBtn.setText("Đã áp dụng");

                priceWithDiscount();
            }
        });
    }

    private void priceWithDiscount(){
        discountTv.setText(""+promoPrice+" VND");
        sTotalTv.setText(String.format("%.0f",allTotalPrice));
        allTotalPriceTv.setText(String.format("%.0f",allTotalPrice-Double.parseDouble(promoPrice)) +" VND");
    }

    private void priceWithoutDiscount() {
        discountTv.setText("");
        sTotalTv.setText(String.format("%.0f",allTotalPrice));
        allTotalPriceTv.setText(String.format("%.0f",allTotalPrice)+" VND");
    }

    //anh xa tu layout dialog_cart
    public String promoId,promoTime,promoCode,promoDescrtiption,promoExpireDate,promoPrice,promoMinimumPrice;
    public boolean isPromoCodeApplied=false;

    private void checkCodeValiable(String promotionCode){
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setMessage("Đang kiểm tra...");
        progressDialog.setCanceledOnTouchOutside(false);

        isPromoCodeApplied=false;
        applyBtn.setText("Áp dụng");
        priceWithoutDiscount();

        //check in db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(adminUid).child("Promotions").orderByChild("promoCode").equalTo(promotionCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                            for(DataSnapshot ds:snapshot.getChildren()){
                                promoId=""+ds.child("id").getValue();
                                promoTime=""+ds.child("timestamp").getValue();
                                promoCode=""+ds.child("promoCode").getValue();
                                promoDescrtiption=""+ds.child("promoDescription").getValue();
                                promoPrice=""+ds.child("promoPrice").getValue();
                                promoMinimumPrice=""+ds.child("minimumOrderPrice").getValue();
                                promoExpireDate=""+ds.child("expireDate").getValue();

                                checkCodeExpire();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(CustomerActivity.this,"Mã không hợp lệ",Toast.LENGTH_LONG).show();
                            applyBtn.setVisibility(View.GONE);
                            promoDescriptionTv.setVisibility(View.GONE);
                            promoDescriptionTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkCodeExpire() {
        //lay ngay hien tai
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String toDay=day+"/"+month+"/"+year;

        try{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate=simpleDateFormat.parse(toDay);
            Date expireDate=simpleDateFormat.parse(promoExpireDate);

            if(expireDate.compareTo(currentDate)>0){ //kiem tra xem code con hsd
                //kiem tra don don co dat gia tri toi thieu de giam gia hay khong
                checkMinimumOrderPrice();
            }
            else if(expireDate.compareTo(currentDate)<0){
                Toast.makeText(this,"Mã đã hết hạn!",Toast.LENGTH_LONG).show();
                applyBtn.setVisibility(View.GONE);
                promoDescriptionTv.setVisibility(View.GONE);
                promoDescriptionTv.setText("");
            }
            else if(expireDate.compareTo(currentDate)==0){
                checkMinimumOrderPrice();
            }

        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
    }

    private void checkMinimumOrderPrice() {
        if(Double.parseDouble(String.format("%.0f",allTotalPrice))<Double.parseDouble(promoMinimumPrice)){
            Toast.makeText(this,"Đơn hàng chưa đạt giá trị tối thiểu! "+promoMinimumPrice,Toast.LENGTH_LONG).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
        else {
            applyBtn.setVisibility(View.VISIBLE);
            promoDescriptionTv.setVisibility(View.VISIBLE);
            promoDescriptionTv.setText(promoDescrtiption);
        }
    }


    private void submitOrder() {
        notify = true;
        //show progress dialog
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.show();

        String timestamp=""+System.currentTimeMillis();

        String costClear=allTotalPriceTv.getText().toString().trim().replace(" VND","");
        String costFinal=costClear.replace("%.0f","");
        String cost=allTotalPriceTv.getText().toString().trim().replace(" VND","");

        //add latitude, longtitude of user  to each order

        //setup order data
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","Đang chờ");
        hashMap.put("orderCost",""+costFinal);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+customerId);
        hashMap.put("latitude",""+customerLatitude);
        hashMap.put("longtitude",""+customerLongtitude);

        if(isPromoCodeApplied){
            hashMap.put("discount",""+promoPrice);
        }
        else {
            hashMap.put("discount","0");
        }

        //add to db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //order info added now add order items
                        for(int i=0;i<cartItemList.size();i++){
                            String pId=cartItemList.get(i).getpId();
                            String id=cartItemList.get(i).getId();
                            String cost=cartItemList.get(i).getCost();
                            String name=cartItemList.get(i).getName();
                            String price=cartItemList.get(i).getPrice();
                            String quantity=cartItemList.get(i).getQuantity();

                            HashMap<String, Object> hashMap1=new HashMap<>();
                            hashMap1.put("pId",pId);
                            hashMap1.put("id",id);
                            hashMap1.put("cost",cost);
                            hashMap1.put("name",name);
                            hashMap1.put("price",price);
                            hashMap1.put("quantity",quantity);
                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(CustomerActivity.this,"Đang tiến hành đặt hàng...",Toast.LENGTH_LONG).show();

                        //sau khi dat hang, gui thong bao don hang moi den cho admin

                        if(notify){
                            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users");
                            refUser.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = snapshot.child("name").getValue().toString();
                                    sendNotification(adminUid, name, "Đã đặt hàng");
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                        notify = false;

                      //  prepareNotificationMessage(timestamp);
                        Intent intent=new Intent(CustomerActivity.this, OrderDetailsSellerActivity.class);
                        intent.putExtra("orderTo",customerId);
                        intent.putExtra("orderBy",firebaseAuth.getUid());
                        intent.putExtra("orderId",timestamp);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CustomerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendNotification(String receiver, String username, String msg){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseAuth.getCurrentUser().getUid()
                            , R.drawable.manager
                            , username+": "+ msg
                            , "Bạn có đơn hàng mới"
                            , receiver
                            , "customer");
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            if(response.code() == 200){
                                if(response.body().success!=1){
                                    Toast.makeText(CustomerActivity.this,"Thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) { }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseAuth.getUid()).setValue(token1);
    }

    public void cartCount(){
        //keep it public so we can access  in adapter
        //get cart count
        int count=easyDB.getAllData().getCount();
        if(count<=0){
            cartCountTv.setVisibility(View.GONE);
        }
        else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);
        }
    }

    private void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    private void loadAllProducts() {
        //init array list
        productList=new ArrayList<>();

        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting, reset list
                        productList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductCustomer=new AdapterProductCustomer(CustomerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductCustomer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI(){
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_tab_left);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_tab_right_un);
    }

    private void showOrdersUI(){
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_left_un);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_tab_right);
    }

    private void openMap() {
        String address="https://maps.google.com/maps?saddr" + mLatitude + "," + mLongtitude + "&daddr=" + customerLatitude + "," + customerLongtitude;
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String email=""+ds.child("email").getValue();
                            mPhone=""+ds.child("phone").getValue();
                            String city=""+ds.child("city").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            mName=""+ds.child("name").getValue();
                            mLatitude=""+ds.child("latitude").getValue();
                            mLongtitude=""+ds.child("longtitude").getValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCustomerDetail(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Customers").child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerAddress=""+snapshot.child("address").getValue();
                customerCity=""+snapshot.child("city").getValue();
                customerCountry=""+snapshot.child("country").getValue();
                customerLatitude=""+snapshot.child("latitude").getValue();
                customerLongtitude=""+snapshot.child("longtitude").getValue();
                customerName=""+snapshot.child("name").getValue();
                customerPhone=""+snapshot.child("phone").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();

                String addressBit="";
                if(customerAddress.length()>30)
                {
                    addressBit=customerAddress.substring(0,35)+"...";
                }
                else {
                    addressBit=customerAddress;
                }
                //set data
                nameTv.setText(customerName+" ");
                phoneTv.setText(customerPhone+" ");
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

    private void dialPhone() {
//        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("Gọi đến số:"+Uri.encode(shopPhone))));
//        Toast.makeText(this,""+shopName,Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+Uri.encode(customerPhone)));
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

    private void loadFilteredProducts(String selected) {
        productList=new ArrayList<>();

        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child("ky0gFPTgkgcLVhiKBWaNmYbW5652").child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting, reset list
                        productList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){

                            String productCategory=""+ds.child("productCategory").getValue();
                            //if selected category matches productCategory
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                        }
                        //setup adapter
                        adapterProductCustomer=new AdapterProductCustomer(CustomerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductCustomer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void prepareNotificationMessage(String orderId){
        //khi seller order thì thôgn bóa cho admin

        String NOTIFICATION_TOPIC="/topics/"+Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE="Đơn hàng: "+orderId;
        String NOTIFICATION_MESSAGE="Bạn có đơn hàng mới!";
        String NOTIFICATION_TYPE="NewOrder";

        //json
        JSONObject notificationJo=new JSONObject();
        JSONObject notificationBodyJo=new JSONObject();
        try{
            //set bien, gia tri gui
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("sellerUid",firebaseAuth.getUid());
            notificationBodyJo.put("adminUid",adminUid);
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //
            notificationJo.put("to",NOTIFICATION_TOPIC); //to all who subcribed to this topic
            notificationJo.put("data",notificationBodyJo);
        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        sendFcmNotification(notificationJo,orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required headers
                Map<String,String> headers=new HashMap<>();
                headers.put("Content_Type","application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);
                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}