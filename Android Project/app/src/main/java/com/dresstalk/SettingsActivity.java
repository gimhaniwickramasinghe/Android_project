package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dresstalk.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText fname,lname,address,phone;
    private TextView closeBtn,updateBtn,changeBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImage = findViewById(R.id.stn_profile_image);
        fname = findViewById(R.id.stn_f_name);
        lname = findViewById(R.id.stn_l_name);
        phone = findViewById(R.id.stn_phone);
        address = findViewById(R.id.stn_address);
        closeBtn = findViewById(R.id.close_stn_btn);
        updateBtn = findViewById(R.id.update_stn_btn);
        changeBtn = findViewById(R.id.set_profile_image);

        userInfoDisplay(profileImage,fname,lname,address,phone);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("Clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateUserInfo();
                }
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "Clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Members");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("First_name", fname.getText().toString());
        userMap.put("Last_name", lname.getText().toString());
        userMap.put("address", address.getText().toString());
        userMap.put("phoneOrder", phone.getText().toString());
        ref.child(Prevalent.onlineMembers.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImage.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();

            startActivity(new Intent (SettingsActivity.this,HomeActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if(TextUtils.isEmpty(fname.getText().toString()))
        {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lname.getText().toString()))
        {
            Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address.getText().toString()))
        {
            Toast.makeText(this, "Enter address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone.getText().toString()))
        {
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("Clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("please wait!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null)
        {
            final StorageReference fileRef = storageProfilePicRef
                    .child(Prevalent.onlineMembers.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Members");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("First_name", fname.getText().toString());
                        userMap.put("Last_name", lname.getText().toString());
                        userMap.put("address", address.getText().toString());
                        userMap.put("phoneOrder", phone.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.onlineMembers.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImage, final EditText fname, final EditText lname, final EditText address, final EditText phone) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Members").child(Prevalent.onlineMembers.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String funame = dataSnapshot.child("First_name").getValue().toString();
                        String lsname = dataSnapshot.child("Last_name").getValue().toString();
                        String mphone = dataSnapshot.child("phone").getValue().toString();
                        String maddress = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        fname.setText(funame);
                        lname.setText(lsname);
                        phone.setText(mphone);
                        address.setText(maddress);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
