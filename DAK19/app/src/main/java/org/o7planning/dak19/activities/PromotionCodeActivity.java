package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.dak19.R;
import org.o7planning.dak19.adapter.AdapterPromotionCodeAdmin;
import org.o7planning.dak19.models.ModelPromotionCode;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PromotionCodeActivity extends AppCompatActivity {
    private ImageButton backBtn,addPromotionBtn;
    private TextView filteredTv,maad;
    private ImageButton filterBtn;
    private RecyclerView promoRv;

    private ArrayList<ModelPromotionCode> promoList;
    private AdapterPromotionCodeAdmin adapterPromotionCodeAdmin;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_code);

        backBtn=findViewById(R.id.backBtn);
        addPromotionBtn=findViewById(R.id.addPromotionBtn);
        filteredTv=findViewById(R.id.filteredTv);
        filterBtn=findViewById(R.id.filterBtn);
        promoRv=findViewById(R.id.promoRv);
        maad=findViewById(R.id.maad);

        firebaseAuth=FirebaseAuth.getInstance();
        //Toast.makeText(this,""+firebaseAuth.getUid(),Toast.LENGTH_LONG).show();

        showAllPromotionCode();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addPromotionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PromotionCodeActivity.this,AddPromoCodesActivity.class));
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });
    }

    private void filterDialog(){
        String[] options ={"Tất cả","Đã hết hạn","Chưa hết hạn"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Lọc mã giảm giá")
                .setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    filteredTv.setText("Tất cả mã giảm giá");
                    showAllPromotionCode();
                }
                else if(i==1){
                    filteredTv.setText("Mã giảm giá đã hết hạn");
                    loadExpiredPromoCode();
                }
                else if(i==2){
                    filteredTv.setText("Mã giảm giá còn hiệu lực");
                    loadNotExpiredPromocode();
                }
            }
        }).show();
    }

    private void showAllPromotionCode(){
        promoList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promoList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelPromotionCode modelPromotionCode=ds.getValue(ModelPromotionCode.class);
                            promoList.add(modelPromotionCode);
                        }
                        adapterPromotionCodeAdmin=new AdapterPromotionCodeAdmin(PromotionCodeActivity.this,promoList);
                        promoRv.setAdapter(adapterPromotionCodeAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadExpiredPromoCode(){
        Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH)+1;
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        String today=mDay+"/"+mMonth+"/"+mYear;

        promoList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promoList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelPromotionCode modelPromotionCode=ds.getValue(ModelPromotionCode.class);

                            String exDate=modelPromotionCode.getExpireDate();

                            try{
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate=simpleDateFormat.parse(today);
                                Date expireDate=simpleDateFormat.parse(exDate);

                                if(expireDate.compareTo(currentDate)>0){

                                }
                                else if(expireDate.compareTo(currentDate)<0){
                                    promoList.add(modelPromotionCode);
                                }
                                else if(expireDate.compareTo(currentDate)==0){

                                }
                            }
                            catch (Exception e){

                            }
                        }
                        adapterPromotionCodeAdmin=new AdapterPromotionCodeAdmin(PromotionCodeActivity.this,promoList);
                        promoRv.setAdapter(adapterPromotionCodeAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadNotExpiredPromocode(){
        Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH)+1;
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        String today=mDay+"/"+mMonth+"/"+mYear;

        promoList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promoList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelPromotionCode modelPromotionCode=ds.getValue(ModelPromotionCode.class);

                            String exDate=modelPromotionCode.getExpireDate();

                            try{
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate=simpleDateFormat.parse(today);
                                Date expireDate=simpleDateFormat.parse(exDate);

                                if(expireDate.compareTo(currentDate)>0){
                                    promoList.add(modelPromotionCode);
                                }
                                else if(expireDate.compareTo(currentDate)<0){

                                }
                                else if(expireDate.compareTo(currentDate)==0){
                                    promoList.add(modelPromotionCode);
                                }
                            }
                            catch (Exception e){

                            }
                        }
                        adapterPromotionCodeAdmin=new AdapterPromotionCodeAdmin(PromotionCodeActivity.this,promoList);
                        promoRv.setAdapter(adapterPromotionCodeAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}