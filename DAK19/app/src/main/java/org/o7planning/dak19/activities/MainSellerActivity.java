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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.adapter.AdapterCustomer;
import org.o7planning.dak19.adapter.AdapterProductSeller;
import org.o7planning.dak19.Constants;
import org.o7planning.dak19.models.ModelCustomer;
import org.o7planning.dak19.models.ModelProduct;
import org.o7planning.dak19.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,tabProductsTv,tabCustomersTv,filteredProductsTv,
            filteredCustomersTv,phoneTv;
    private EditText searchProductEt;
    private ImageButton logoutBtn,editProfileBtn,filterProductBtn,filterCustomerBtn,addCustomerBtn,settingsBtn, doanhThuBtn;
    private ImageView profileIv;

    private RelativeLayout productsRl,customersRl;
    private RecyclerView productsRv,customersRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelCustomer> customerArrayList;
    private AdapterCustomer adapterCustomer;
    private BottomNavigationView menuBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv = findViewById(R.id.nameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
     //   logoutBtn = findViewById(R.id.logoutBtn);
     //   editProfileBtn = findViewById(R.id.editProfileBtn);
        profileIv = findViewById(R.id.profileIv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabCustomersTv = findViewById(R.id.tabSellersTv);
     //   addCustomerBtn = findViewById(R.id.addCustomerBtn);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        settingsBtn=findViewById(R.id.settingsBtn);
        menuBottomBar=findViewById(R.id.menuBottomBar);
        doanhThuBtn=findViewById(R.id.doanhThuBtn);

        filteredProductsTv = findViewById(R.id.filteredProductsTv);

        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabCustomersTv = findViewById(R.id.tabSellersTv);

        productsRl = findViewById(R.id.productsRl);
        customersRl = findViewById(R.id.customersRl);

        productsRv = findViewById(R.id.productsRv);
        customersRv = findViewById(R.id.customersRv);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();
        loadAllProducts();
        loadAllCustomers();
        showProductsUI();

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
//                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
//            }
//        });
//
//        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainSellerActivity.this, AddCustomerActivity.class));
//            }
//        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsUI();
            }
        });

        tabCustomersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomersUI();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
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
                    adapterProductSeller.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this,SettingsActivity.class));
            }
        });
        menuBottomBar.setOnNavigationItemSelectedListener(navListener);

        doanhThuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this,ChartSellerMonthActivity.class));
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.action_edit:
                            startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
                            break;
                        case R.id.action_addCustomer:
                            startActivity(new Intent(MainSellerActivity.this, AddCustomerActivity.class));
                            break;
                        case R.id.action_logout:
                            makeMeOffline();
                            break;
                    }
                    return true;
                }
            };

    private void loadAllCustomers() {
        //init array list
        customerArrayList =new ArrayList<>();

        //load orders
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Customers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear data
                        customerArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelCustomer modelCustomer=ds.getValue(ModelCustomer.class);
                            //add data to list
                            customerArrayList.add(modelCustomer);
                        }
                        //setup adapter
                        adapterCustomer=new AdapterCustomer(MainSellerActivity.this,customerArrayList);
                        //set adapter
                        customersRv.setAdapter(adapterCustomer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI(){
        productsRl.setVisibility(View.VISIBLE);
        customersRl.setVisibility(View.GONE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_tab_left);
        tabCustomersTv.setBackgroundResource(R.drawable.shape_rect_tab_right_un);
    }

    private void showCustomersUI(){
        productsRl.setVisibility(View.GONE);
        customersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_left_un);
        tabCustomersTv.setBackgroundResource(R.drawable.shape_rect_tab_right);
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
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeMeOffline() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this,R.style.AlertDialogCustom);
        builder.setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete
                        //after logging in,make user online
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
                                        Toast.makeText(MainSellerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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


    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();

                            nameTv.setText(name);
                            phoneTv.setText(phone);
                            emailTv.setText(email);
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_pass_gray).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}