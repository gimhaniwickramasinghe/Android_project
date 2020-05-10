package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dresstalk.ModelClasses.Members;
import com.dresstalk.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button createBtn, loginBtn ;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBtn = (Button) findViewById(R.id.create_btn);
        loginBtn = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SigninActivity.class);
                startActivity(intent);
            }
        });

        String PhoneKey = Paper.book().read(Prevalent.PhoneKey);
        String PasswordKey = Paper.book().read(Prevalent.PasswordKey);

        if(PhoneKey != "" && PasswordKey != ""){
            if(!TextUtils.isEmpty(PhoneKey) && !TextUtils.isEmpty(PasswordKey)){

                AutoLogin(PhoneKey,PasswordKey);

                loadingBar.setMessage("Please wait");
                loadingBar.setCancelable(false);
                loadingBar.show();
            }
        }
    }

    private void AutoLogin(final String phone, final String password) {
        final DatabaseReference val;
        val = FirebaseDatabase.getInstance().getReference();

        val.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Members").child(phone).exists()){
                    Members memberData = dataSnapshot.child("Members").child(phone).getValue(Members.class);

                    if(memberData.getPhone().equals(phone)){
                        if(memberData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"You already logged in...",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.onlineMembers = memberData;
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect.Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "No account found with this " + phone + " number", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"You must create account first",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
