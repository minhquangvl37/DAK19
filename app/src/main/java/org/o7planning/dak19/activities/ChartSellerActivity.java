package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.ChartInfo;
import org.o7planning.dak19.R;
import org.o7planning.dak19.adapter.AdapterOrderedItem;
import org.o7planning.dak19.models.ModelOrderItem;
import org.o7planning.dak19.models.ModelProduct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CompletionStage;

public class ChartSellerActivity extends AppCompatActivity {
    private TextView masellerTv,doanhSoTv,dateTv;
    private String sellerId;
    private ImageView profileIv;
    private FirebaseAuth firebaseAuth;
    private ImageButton backBtn;
    private Button btnChartDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

  private   BarChart barChart,barChartMonth;
  private   ArrayList<BarEntry> barEntryArrayList;
  private   ArrayList<String> labelName;
  private   ArrayList<ChartInfo> dateCostInfo =new ArrayList<>();

  private int doanhSo=0;

  private int day,month,year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_seller);

        masellerTv=findViewById(R.id.masellerTv);
        barChart=findViewById(R.id.barChart);
        profileIv=findViewById(R.id.profileIv);
        doanhSoTv=findViewById(R.id.doanhSoTv);
        backBtn=findViewById(R.id.backBtn);
        btnChartDay=findViewById(R.id.btnChartDay);
        dateTv=findViewById(R.id.dateTv);

        sellerId=getIntent().getStringExtra("sellerId");

        firebaseAuth=FirebaseAuth.getInstance();

        loadInfoSeller();
        //fillDate();

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        dateTv.setText(day+"/"+month+"/"+year);
        fillOnNow(day,month,year);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnChartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(
                        ChartSellerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                    String date=day+ "/" +month+ "/" +year;
                dateTv.setText(date);
                    fillOnNow(day,month,year);
            }
        };
    }

    private void fillOnNow(int d, int m, int y) {
        doanhSo=0;
        doanhSoTv.setText("0");
        dateCostInfo=new ArrayList<>();
        String day=String.valueOf(d);

        if(day.length()==1){
            day="0"+day;
        }

        String month=String.valueOf(m);
        String year=String.valueOf(y);
        String dmy=day+month+year;
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(sellerId).child("ChartInfo").child(dmy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dateCostInfo.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ChartInfo chartInfo=new ChartInfo(""+ds.child("orderTime").getValue(),
                                    Integer.parseInt(""+ds.child("orderCost").getValue()));

                            doanhSo=(Integer.parseInt(""+ds.child("orderCost").getValue()))+doanhSo;

                            dateCostInfo.add(chartInfo);
                        }

                        String doanhSoFormatted=String.format("%,d",doanhSo);
                        doanhSoTv.setText(doanhSoFormatted+"VNĐ");

                        barEntryArrayList=new ArrayList<>();
                        labelName=new ArrayList<>();
                        for(int i=0;i<dateCostInfo.size();i++){
                            String day =dateCostInfo.get(i).getDate();
                            int cost=dateCostInfo.get(i).getCost();
                            barEntryArrayList.add(new BarEntry(i,cost));
                            labelName.add(day);
                        }

                        BarDataSet barDataSet=new BarDataSet(barEntryArrayList,"Giá trị theo từng đơn hàng");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        Description description=new Description();
                        barChart.setDescription(description);
                        BarData barData=new BarData(barDataSet);
                        barChart.setData(barData);

                        XAxis xAxis=barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));
                        xAxis.setPosition(XAxis.XAxisPosition.TOP);
                        xAxis.setDrawGridLines(false);
                        xAxis.setDrawAxisLine(false);
                        xAxis.setGranularity(1f);
                        xAxis.setLabelCount(labelName.size());
                        xAxis.setLabelRotationAngle(270);
                        barChart.animateY(2000);
                        barChart.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadInfoSeller() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sellerAddress=""+snapshot.child("address").getValue();
             String   sellerCity=""+snapshot.child("city").getValue();
                String  sellerCountry=""+snapshot.child("country").getValue();
                String  sellerLatitude=""+snapshot.child("latitude").getValue();
                String sellerLongtitude=""+snapshot.child("longtitude").getValue();
                String sellerName=""+snapshot.child("name").getValue();
                String  sellerPhone=""+snapshot.child("phone").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();

                //set data
                masellerTv.setText("Nhân viên: "+sellerName);
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

    public void fillDate(){
//        dateCostInfo.clear();
//        dateCostInfo.add(new ChartInfo("Ngày 22/11",43));
//        dateCostInfo.add(new ChartInfo("Ngày 22/11",22));
//        dateCostInfo.add(new ChartInfo("Ngày 22/11",63));
//        dateCostInfo.add(new ChartInfo("Ngày 22/11",53));
//        dateCostInfo.add(new ChartInfo("Ngày 22/11",33));

        dateCostInfo=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(sellerId).child("ChartInfo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dateCostInfo.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ChartInfo chartInfo=new ChartInfo(""+ds.child("orderTime").getValue(),
                                                             Integer.parseInt(""+ds.child("orderCost").getValue()));

                            doanhSo=(Integer.parseInt(""+ds.child("orderCost").getValue()))+doanhSo;

                            dateCostInfo.add(chartInfo);
                        }

                        String doanhSoFormatted=String.format("%,d",doanhSo);
                        doanhSoTv.setText(doanhSoFormatted+"VND");

                        barEntryArrayList=new ArrayList<>();
                        labelName=new ArrayList<>();
                        for(int i=0;i<dateCostInfo.size();i++){
                            String day =dateCostInfo.get(i).getDate();
                            int cost=dateCostInfo.get(i).getCost();
                            barEntryArrayList.add(new BarEntry(i,cost));
                            labelName.add(day);
                        }

                        BarDataSet barDataSet=new BarDataSet(barEntryArrayList,"Seller Cost");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        Description description=new Description();
                        barChart.setDescription(description);
                        BarData barData=new BarData(barDataSet);
                        barChart.setData(barData);

                        XAxis xAxis=barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));
                        xAxis.setPosition(XAxis.XAxisPosition.TOP);
                        xAxis.setDrawGridLines(false);
                        xAxis.setDrawAxisLine(false);
                        xAxis.setGranularity(1f);
                        xAxis.setLabelCount(labelName.size());
                        xAxis.setLabelRotationAngle(270);
                        barChart.animateY(2000);
                        barChart.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
   }
}
