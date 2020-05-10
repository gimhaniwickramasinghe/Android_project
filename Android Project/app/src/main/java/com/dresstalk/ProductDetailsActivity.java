package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dresstalk.ModelClasses.Products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton add_product_cart;
    private ImageView productImg;
    private ElegantNumberButton numberButton;
    private TextView dprice, ddescription, dProductname;
    private String productID ="";
    private ImageView product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        add_product_cart = findViewById(R.id.add_product_cart);
        productImg = findViewById(R.id.product_image_details);
        numberButton = findViewById(R.id.product_count);
        dprice = findViewById(R.id.product_detail_price);
        ddescription = findViewById(R.id.product_detail_description);
        dProductname = findViewById(R.id.product_detail_name);
        product = findViewById(R.id.product_image_details);

        getProductDetails(productID);

        add_product_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
                Toast.makeText(ProductDetailsActivity.this, "Added to cart", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProductDetailsActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addingToCartList() {
        /*Cant getInstance() method*/
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productSRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productSRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    dProductname.setText(products.getPname());
                    dprice.setText(products.getPrice());
                    ddescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
