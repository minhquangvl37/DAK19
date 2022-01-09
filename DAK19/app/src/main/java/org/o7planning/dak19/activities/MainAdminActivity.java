package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.CloudMess.FcmNotificationsSender;
import org.o7planning.dak19.CloudMess.Token;
import org.o7planning.dak19.adapter.AdapterCustomer;
import org.o7planning.dak19.adapter.AdapterOrderAdmin;
import org.o7planning.dak19.adapter.AdapterOrderSeller;
import org.o7planning.dak19.adapter.AdapterProductAdmin;
import org.o7planning.dak19.Constants;
import org.o7planning.dak19.adapter.AdapterSeller;
import org.o7planning.dak19.models.ModelCustomer;
import org.o7planning.dak19.models.ModelOrderAdmin;
import org.o7planning.dak19.models.ModelOrderSeller;
import org.o7planning.dak19.models.ModelProduct;
import org.o7planning.dak19.R;
import org.o7planning.dak19.models.ModelSeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainAdminActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,phoneTv,tabProductsTv,tabOrdersTv,tabSellersTv,filteredProductsTv,
            filteredOrdersTv, filteredSellerstv;
    private ImageButton logoutBtn,editProfileBtn,addSeller,addProductBtn,filterProductBtn,filterOrderBtn,moreBtn;
    private ImageView profileIv;
    private EditText searchProductEt,searchOrderEt;

    RelativeLayout productsRl,ordersRl,sellersRl;
    RecyclerView productsRv,ordersRv,sellersRv;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference refToken;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductAdmin adapterProductAdmin;

    private ArrayList<ModelSeller> sellerList;
    private AdapterSeller adapterSeller;

    private ArrayList<ModelOrderAdmin> orderAdminArrayList;
    private AdapterOrderAdmin adapterOrderAdmin;

    private BottomNavigationView menuBottomBar;

    private String token = "";

//    private ArrayList<ModelOrderUser> ordersList;
//    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        nameTv=findViewById(R.id.nameTv);
        profileIv=findViewById(R.id.profileIv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
      //  addSeller=findViewById(R.id.addSeller);
       // logoutBtn=findViewById(R.id.logoutBtn);
      //  editProfileBtn=findViewById(R.id.editProfileBtn);
     //   addProductBtn=findViewById(R.id.addProductBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        searchOrderEt=findViewById(R.id.searchOrderEt);
        moreBtn=findViewById(R.id.settingsBtn);
        menuBottomBar=findViewById(R.id.menuBottomBar);


        filteredProductsTv=findViewById(R.id.filteredProductsTv);
        filteredOrdersTv=findViewById(R.id.filteredOrdersTv);

        filterProductBtn=findViewById(R.id.filterProductBtn);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);

        productsRl=findViewById(R.id.productsRl);
        productsRv=findViewById(R.id.productsRv);
        tabProductsTv=findViewById(R.id.tabProductsTv);

        ordersRl=findViewById(R.id.ordersRl);
       ordersRv=findViewById(R.id.ordersRv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);

        sellersRl=findViewById(R.id.sellersRl);
       sellersRv=findViewById(R.id.sellersRv);
        tabSellersTv=findViewById(R.id.tabSellersTv);



        firebaseAuth= FirebaseAuth.getInstance();
        refToken = FirebaseDatabase.getInstance().getReference().child("Token").child(firebaseAuth.getUid());
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();
        loadAllProducts();
        showProductsUI();
        loadAllSellers();
        loadAllOrders();

        //
        // fcm settings for perticular user

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String token = task.getResult();
                    updateToken(token);
                });
        //m đang chạy 2 máy phairk ua

//        // CloudMess
//        refToken.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String token = String.valueOf(snapshot.getChildren());
//                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
//                        token,
//                        "Test",
//                        "Thong bao khan",
//                        getApplicationContext(),
//                        MainAdminActivity.this);
//                notificationsSender.SendNotifications();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                makeMeOffline();
//            }
//        });
//
//        editProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainAdminActivity.this, ProfileEditAdminActivity.class));
//            }
//        });
//
//        addSeller.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainAdminActivity.this, RegisterSellerActivity.class));
//            }
//        });
//
//        addProductBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainAdminActivity.this,AddProductActivity.class));
//            }
//        });
//
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomersUI();
            }
        });

        tabSellersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSellersUI();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainAdminActivity.this);
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
                    adapterProductAdmin.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options={"Tất cả đơn hàng","Đang chờ","Đã xác nhận","Đã hủy"};
                AlertDialog.Builder builder=new AlertDialog.Builder(MainAdminActivity.this);
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

//        settingsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    startActivity(new Intent(MainAdminActivity.this,SettingsActivity.class));
//            }
//        });

        PopupMenu popupMenu = new PopupMenu(MainAdminActivity.this,moreBtn);
        popupMenu.getMenu().add("Cài đặt");
        popupMenu.getMenu().add("Mã giảm giá");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Cài đặt") {
                    startActivity(new Intent(MainAdminActivity.this, SettingsActivity.class));
                } else if (menuItem.getTitle() == "Mã giảm giá") {
                    startActivity(new Intent(MainAdminActivity.this, PromotionCodeActivity.class));
                }
                return true;
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        menuBottomBar.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.action_edit:
                            startActivity(new Intent(MainAdminActivity.this, ProfileEditAdminActivity.class));
                            break;
                        case R.id.action_addProduct:
                            startActivity(new Intent(MainAdminActivity.this,AddProductActivity.class));
                            break;
                        case R.id.action_addSeller:
                            startActivity(new Intent(MainAdminActivity.this, RegisterSellerActivity.class));
                            break;
                        case R.id.action_logout:
                            makeMeOffline();
                            break;
                    }
                    return true;
                }
            };

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseAuth.getUid()).setValue(token1);
    }

    private void loadAllOrders() {
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
                    ref.child(uid).child("Orders")
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
                                        adapterOrderAdmin=new AdapterOrderAdmin(MainAdminActivity.this,orderAdminArrayList);
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


    private void loadAllSellers() {
        //init array list
        sellerList =new ArrayList<>();

        //load orders
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear data
                        sellerList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelSeller modelSeller=ds.getValue(ModelSeller.class);
                            //add data to list
                            sellerList.add(modelSeller);
                        }
                        //setup adapter
                        adapterSeller=new AdapterSeller(MainAdminActivity.this,sellerList);
                        //set adapter
                        sellersRv.setAdapter(adapterSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String city=""+ds.child("city").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String accountType=""+ds.child("accountType").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            //load only shops that are in the same city
//                            loadShops(city);
//                            loadOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAllProducts() {
        //init array list
        productList=new ArrayList<>();

        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
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
                        adapterProductAdmin=new AdapterProductAdmin(MainAdminActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        //show ui shop
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);
        sellersRl.setVisibility(View.GONE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_tab_left);
        tabSellersTv.setBackgroundResource(R.drawable.shape_rect_tab_right_un);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_tab_between_un);


    }

    private void showCustomersUI() {
        //show orders ui and hide products ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);
        sellersRl.setVisibility(View.GONE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_left_un);
        tabSellersTv.setBackgroundResource(R.drawable.shape_rect_tab_right_un);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_tab_between);
    }

    private void showSellersUI() {
        //show orders ui and hide products ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.GONE);
        sellersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_left_un);
        tabSellersTv.setBackgroundResource(R.drawable.shape_rect_tab_right);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_tab_between_un);
    }

    private void makeMeOffline() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainAdminActivity.this,R.style.AlertDialogCustom);
        builder.setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete
                        progressDialog.setMessage("Đang đăng xuất...");

                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("online","false");

                        //update value to db
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //update successfully
                                        firebaseAuth.signOut();
                                        checkUser();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //up failed
                                        progressDialog.dismiss();
                                        Toast.makeText(MainAdminActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        //after logging in,make user online

    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainAdminActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadFilteredProducts(String selected) {
        productList=new ArrayList<>();

        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
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
                        adapterProductAdmin=new AdapterProductAdmin(MainAdminActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}