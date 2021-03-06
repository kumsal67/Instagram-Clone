package com.example.instagram_clone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PostActivity extends AppCompatActivity {
    EditText editText;
    ImageView imageView;
    Bitmap selectedimae;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editText=findViewById(R.id.etextpost_activity);
        imageView=findViewById(R.id.imageView);
        Button btn=findViewById(R.id.btnupload);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Cliced image");
                if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                }else{
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                selectedimae.compress(Bitmap.CompressFormat.PNG,50,bos);
                byte[]data=bos.toByteArray();
                ParseFile file=new ParseFile("resim.png",data);
                ParseObject object=new ParseObject("posts");
                object.put("comment",editText.getText().toString());
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("photo",file);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null){
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Saved success",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(),FeedActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length>0){
            if (requestCode==2){
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                if (data!=null){
                    Uri uri=data.getData();
                    try {
                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                        selectedimae=bitmap;
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}