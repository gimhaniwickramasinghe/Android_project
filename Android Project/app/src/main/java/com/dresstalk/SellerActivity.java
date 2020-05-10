package com.dresstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerActivity extends AppCompatActivity {

    private String categoryName, description, price, pname, saveDate, saveTime;
    private Button addNewProductBtn;
    private ImageView productImg;
    private EditText productName, productDescription, productPrice;
    private static final int chooseimg = 1;
    private Uri imageUri;
    private String productKey, downloadImg;
    private StorageReference imageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        categoryName = getIntent().getExtras().get("category").toString();
        imageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        addNewProductBtn = (Button) findViewById(R.id.add_product_btn);
        productImg = (ImageView) findViewById(R.id.add_product_img);
        productName = (EditText) findViewById(R.id.product_name);
        productDescription = (EditText) findViewById(R.id.product_description);
        productPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProductData();
            }
        });
    }



    private void openGallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,chooseimg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == chooseimg && requestCode == RESULT_OK && data != null){
            Toast.makeText(this, "Select image", Toast.LENGTH_SHORT).show();
        }
        else{
            imageUri = data.getData();
            productImg.setImageURI(imageUri);
        }
    }

    private void checkProductData() {
        description = productDescription.getText().toString();
        price = productPrice.getText().toString();
        pname = productName.getText().toString();

        if(imageUri == null){
            Toast.makeText(this, "Image is compulsory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname)){
            Toast.makeText(this,"Give name for product",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Write about product",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(this,"Enter price",Toast.LENGTH_SHORT).show();
        }
        else{
            storeProductInfo();
        }
    }

    private void storeProductInfo() {

        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Please wait until adding product");
        loadingBar.setCancelable(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM DD, YYYY");
        saveDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveTime = currentTime.format(calendar.getTime());

        productKey = saveDate + saveTime;

        final StorageReference FilePath = imageRef.child(imageUri.getLastPathSegment() + productKey + ".jpg");

        final UploadTask uploadTask = FilePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(SellerActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerActivity.this,"Image uploaded",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SellerActivity.this,SellerChooseItemActivity.class);
                startActivity(intent);

                loadingBar.dismiss();
                Toast.makeText(SellerActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                /*
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful()) {
                           throw task.getException();
                       }
                       downloadImg = FilePath.getDownloadUrl().toString();
                       return FilePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImg = task.getResult().toString();
                            Toast.makeText(SellerActivity.this, "Got product image url successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfo();
                        }
                    }
                });*/
            }
        });
    }

    private void saveProductInfo() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("Category",categoryName);
        productMap.put("Date",saveDate);
        productMap.put("Description",description);
        productMap.put("Image",downloadImg);
        productMap.put("pid",productKey);
        productMap.put("Name",pname);
        productMap.put("Price",productPrice);
        productMap.put("Time",saveTime);

        productRef.child(productKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(SellerActivity.this,SellerChooseItemActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(SellerActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(SellerActivity.this, "Error: " + message , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
