package org.o7planning.dak19.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.o7planning.dak19.FilterProduct;
import org.o7planning.dak19.FilterProductCustomer;
import org.o7planning.dak19.models.ModelProduct;
import org.o7planning.dak19.R;
import org.o7planning.dak19.activities.CustomerActivity;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductCustomer extends RecyclerView.Adapter<AdapterProductCustomer.HolderProductCustomer> implements Filterable {
    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProductCustomer filter;


    public AdapterProductCustomer(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList=productList;
    }

    @NonNull
    @Override
    public HolderProductCustomer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.row_product_customer,parent,false);
        return new HolderProductCustomer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductCustomer holder, int position) {
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

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add product to cart
                showQuantityDialog(modelProduct);
            }
        });
    }

    private double cost=0;
    private double finalCost=0;
    private int quantity=0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        //init layout
        ImageView productIv=view.findViewById(R.id.productIv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView pQuantityTv=view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv=view.findViewById(R.id.discountedNoteTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv=view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv=view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn=view.findViewById(R.id.decrementBtn);
        final TextView quantityTv=view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn=view.findViewById(R.id.incrementBtn);
        Button continueBtn=view.findViewById(R.id.continueBtn);

        //get data from model
        final String productId=modelProduct.getProductId();
        String title=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String description=modelProduct.getProductDescription();
        String discountNote=modelProduct.getDiscountNote();
        String image=modelProduct.getProductIcon();

        final String price;
        if(modelProduct.getDiscountAvaliable().equals("true")){
            //product have discount
            price=modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //add strile throungh on orinal price
        }
        else {
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price=modelProduct.getOriginalPrice();
        }

        cost=Double.parseDouble(price.replaceAll(" VND",""));
        finalCost=Double.parseDouble(price.replaceAll(" VND",""));
        quantity=1;

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        //setdata
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart);
        }

        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText(modelProduct.getOriginalPrice()+" VND");
        priceDiscountedTv.setText(modelProduct.getDiscountPrice()+" VND");

        //xoa chu so sau phan thap phan
        String finalCostString= String.format("%.0f",finalCost);
        finalPriceTv.setText(finalCostString+" VND");

        AlertDialog dialog=builder.create();
        dialog.show();

        //increment quantity of product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cost;
                quantity++;

                //xoa chu so sau phan thap phan
                String finalCostString= String.format("%.0f",finalCost);
                finalPriceTv.setText(finalCostString+" VND");

               // finalPriceTv.setText(finalCostString+" VND");
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalCost=finalCost-cost;
                    quantity--;

                    //xoa chu so sau phan thap phan
                    String finalCostString= String.format("%.0f",finalCost);
                    finalPriceTv.setText(finalCostString+" VND");

                   // finalPriceTv.setText(finalCostString+" VND");
                    quantityTv.setText(""+quantity);
                }

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleTv.getText().toString().trim();
                String priceEach=price;
                String totalPrice=finalPriceTv.getText().toString().trim().replace(" VND","");
                String quantity=quantityTv.getText().toString().trim();

                //add to db
                addToCart(productId,title,priceEach,totalPrice,quantity);
                dialog.dismiss();
            }
        });

    }

    //use SQLite
    private int itemId=1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;
        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();
        Boolean b =easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();
        Toast.makeText(context,"Đang thêm vào giỏ hàng...",Toast.LENGTH_LONG).show();

        //update cartCount
        ((CustomerActivity)context).cartCount();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProductCustomer(this,filterList);
        }
        return filter;
    }

    class HolderProductCustomer extends RecyclerView.ViewHolder {
        private ImageView productIconIv;
        private TextView discountedNoteTv, titleTv, quantityTv, discountedPriceTv, originalPriceTv,productDes;
        private ImageButton addToCartBtn;
        public HolderProductCustomer(@NonNull View itemView) {
            super(itemView);
            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            addToCartBtn=itemView.findViewById(R.id.addToCartBtn);
            productDes=itemView.findViewById(R.id.productDes);
        }
    }
}
