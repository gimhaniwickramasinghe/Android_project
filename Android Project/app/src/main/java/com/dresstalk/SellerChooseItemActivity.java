package com.dresstalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.paperdb.Paper;

public class SellerChooseItemActivity extends AppCompatActivity {

    private ImageView kids, men, women, shoes;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_choose_item);

        kids = (ImageView) findViewById(R.id.image_kids);
        men = (ImageView) findViewById(R.id.image_men);
        women = (ImageView) findViewById(R.id.image_women);
        shoes = (ImageView) findViewById(R.id.image_shoe);
        logout = (Button) findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();

                Intent intent = new Intent(SellerChooseItemActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerChooseItemActivity.this,SellerActivity.class);
                intent.putExtra("category","kids");
                startActivity(intent);
            }
        });

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerChooseItemActivity.this,SellerActivity.class);
                intent.putExtra("category","men");
                startActivity(intent);
            }
        });

        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerChooseItemActivity.this,SellerActivity.class);
                intent.putExtra("category","women");
                startActivity(intent);
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerChooseItemActivity.this,SellerActivity.class);
                intent.putExtra("category","shoes");
                startActivity(intent);
            }
        });
    }
}
