package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.o7planning.dak19.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPromoCodesActivity extends AppCompatActivity {

    private ImageButton backBtn ;
    private EditText promoCodeEt,promoCodeDescriptionEt ,promoPriceEt,minumumPriceEt;
    private TextView expireDateTv,titleTv;
    private Button addBtn  ;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String promoId;

    private Boolean isUpdating=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo_codes);

        backBtn=findViewById(R.id.backBtn);
        promoCodeEt=findViewById(R.id.promoCodeEt);
        promoCodeDescriptionEt=findViewById(R.id.promoCodeDescriptionEt);
        promoPriceEt=findViewById(R.id.promoPriceEt);
        expireDateTv=findViewById(R.id.expireDateTv);
        minumumPriceEt=findViewById(R.id.minumumPriceEt);
        addBtn=findViewById(R.id.addBtn);
        titleTv=findViewById(R.id.titleTv);

        //anh xa firebase
        firebaseAuth=FirebaseAuth.getInstance();

        Intent intent=getIntent();
        if(intent.getStringExtra("promoId")!=null){
            promoId=intent.getStringExtra("promoId");

            titleTv.setText("SỬA MÃ GIẢM GIÁ");
            addBtn.setText("CẬP NHẬT");

            isUpdating=true;
            loadPromoInfo();
        }
        else {
            titleTv.setText("THÊM CODE GIẢM GIÁ");
            addBtn.setText("THÊM");

            isUpdating=false;
        }

        progressDialog=new ProgressDialog(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        expireDateTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar calendar=Calendar.getInstance();
//                int year=calendar.get(Calendar.YEAR);
//                int month=calendar.get(Calendar.MONTH);
//                int mday=calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog=new DatePickerDialog(
//                        AddPromoCodesActivity.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        mDateSetListener,
//                        year,month,day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//            }
//        });
//
//        mDateSetListener=new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                month=month+1;
//                String date=day+ "/" +month+ "/" +year;
//                expireDateTv.setText(date);
//            }
//        };

        expireDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

    }

    private void loadPromoInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(promoId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id=""+snapshot.child("id").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String promoCode=""+snapshot.child("promoCode").getValue();
                        String promoDescription=""+snapshot.child("promoDescription").getValue();
                        String promoPrice=""+snapshot.child("promoPrice").getValue();
                        String minimumOrderPrice=""+snapshot.child("minimumOrderPrice").getValue();
                        String expireDate=""+snapshot.child("expireDate").getValue();

                        //set data
                        promoCodeEt.setText(promoCode);
                        promoCodeDescriptionEt.setText(promoDescription);
                        promoPriceEt.setText(promoPrice);
                        minumumPriceEt.setText(minimumOrderPrice);
                        expireDateTv.setText(expireDate);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void datePickDialog(){
        Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String pDate=day+"/"+month+"/"+year;
                expireDateTv.setText(pDate);
            }
        },mYear,mMonth,mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
    }

    private String code,description,promoPrice,minimumPriceOrder,dateExpire;
    private void inputData(){
        code=promoCodeEt.getText().toString().trim();
        description=promoCodeDescriptionEt.getText().toString().trim();
        promoPrice=promoPriceEt.getText().toString().trim();
        minimumPriceOrder=minumumPriceEt.getText().toString().trim();
        dateExpire=expireDateTv.getText().toString().trim();

        if(TextUtils.isEmpty(code)){
            Toast.makeText(this,"Vui lòng nhập mã giảm giá...",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Vui lòng nhập mô tả...",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this,"Vui lòng nhập giá giảm...",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(minimumPriceOrder)){
            Toast.makeText(this,"Vui lòng nhập giá đơn tối thiểu...",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(dateExpire)){
            Toast.makeText(this,"Vui lòng chọn ngày hết hạn...",Toast.LENGTH_LONG).show();
            return;
        }

        if(isUpdating){
            //update
            updateDataToFirebase();
        }
        else {
            //add
            addToFirebase();
        }

    }

    private void updateDataToFirebase() {
        progressDialog.setMessage("Đang cập nhật mã giảm giá...");
        progressDialog.show();

        HashMap<String, Object> hashMap =new HashMap<>();
        hashMap.put("promoCode",""+code);
        hashMap.put("promoDescription",""+description);
        hashMap.put("promoPrice",""+promoPrice);
        hashMap.put("minimumOrderPrice",""+minimumPriceOrder);
        hashMap.put("expireDate",""+dateExpire);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(promoId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoCodesActivity.this,"Đang cập nhật...",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoCodesActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addToFirebase(){
        progressDialog.setMessage("Đang thêm mã giảm giá...");
        progressDialog.show();

        String timestamp=""+System.currentTimeMillis();

        HashMap<String, Object> hashMap =new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("promoCode",""+code);
        hashMap.put("promoDescription",""+description);
        hashMap.put("promoPrice",""+promoPrice);
        hashMap.put("minimumOrderPrice",""+minimumPriceOrder);
        hashMap.put("expireDate",""+dateExpire);

        //
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoCodesActivity.this,"Đã thêm mã giảm giá!",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoCodesActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}