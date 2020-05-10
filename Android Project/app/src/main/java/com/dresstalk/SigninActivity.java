package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SigninActivity extends AppCompatActivity {

    private Button signBtn;
    private EditText inputPhone, inputFname, inputLname, inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        inputPhone = (EditText) findViewById(R.id.sign_phone_input);
        inputFname = (EditText) findViewById(R.id.sign_name1_input);
        inputLname = (EditText) findViewById(R.id.sign_name2_input);
        inputPassword = (EditText) findViewById(R.id.sign_password_input);
        signBtn = (Button) findViewById(R.id.sign_create_btn);
        loadingBar = new ProgressDialog(this);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    CreateAccount();
                }
            }
        });
    }

    private void CreateAccount() {
        String phone = inputPhone.getText().toString();
        String fname = inputFname.getText().toString();
        String lname = inputLname.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter phone number..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fname)){
            Toast.makeText(this,"Please enter First name...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lname)){
            Toast.makeText(this,"Please enter Last name...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password...",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Create account");
            loadingBar.setMessage("Please wait until checking information");
            loadingBar.setCancelable(false);
            loadingBar.show();

            emailValidate(phone, fname, lname, password);
        }
     }

    private void emailValidate(final String phone, final String fname, final String lname, final String password) {
        final DatabaseReference val;
        val = FirebaseDatabase.getInstance().getReference();

        val.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Members").child(phone).exists())){

                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("phone",phone);
                    userInfo.put("First_name",fname);
                    userInfo.put("Last_name",lname);
                    userInfo.put("password",password);

                    val.child("Members").child(phone).updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SigninActivity.this, "Your account created..! please log in..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(SigninActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(SigninActivity.this, "Plz try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SigninActivity.this, "This " + phone + " already have an account.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(SigninActivity.this, "Try again in another phone number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SigninActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
