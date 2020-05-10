package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dresstalk.ModelClasses.Members;
import com.dresstalk.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private TextView seller, customer;
    private String parentDBName = "Members";
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.login_btn);
        inputPhone = (EditText) findViewById(R.id.phone_input);
        inputPassword = (EditText) findViewById(R.id.password_input);
        seller = (TextView) findViewById(R.id.seller_link);
        customer = (TextView) findViewById(R.id.customer_link);
        loadingBar = new ProgressDialog(this);

        rememberMe = (CheckBox) findViewById(R.id.remember_ch_box);
        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginUser();
            }
        });
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Seller Login");
                seller.setVisibility(View.INVISIBLE);
                customer.setVisibility(View.VISIBLE);
                parentDBName = "Sellers";
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login");
                seller.setVisibility(View.VISIBLE);
                customer.setVisibility(View.INVISIBLE);
                parentDBName = "Members";
            }
        });
    }

    private void loginUser() {
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter phone..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password...",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login in account");
            loadingBar.setMessage("Please wait until checking information");
            loadingBar.setCancelable(false);
            loadingBar.show();

            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(rememberMe.isChecked()){
            Paper.book().write(Prevalent.PhoneKey,phone);
            Paper.book().write(Prevalent.PasswordKey,password);
        }

        final DatabaseReference val;
        val = FirebaseDatabase.getInstance().getReference();

        val.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBName).child(phone).exists()){

                    Members membersData = dataSnapshot.child(parentDBName).child(phone).getValue(Members.class);

                    if(membersData.getPhone().equals(phone)){
                        if(membersData.getPassword().equals(password)){
                            if(parentDBName.equals("Sellers")){
                                Toast.makeText(LoginActivity.this,"Log in successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,SellerChooseItemActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDBName.equals("Members")){
                                Toast.makeText(LoginActivity.this,"Log in successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.onlineMembers = membersData;
                                startActivity(intent);
                            }
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect.Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "No account found with this " + phone + " number", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,"You must create account first",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
