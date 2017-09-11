package com.example.abhinav.myblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;

public class SetupActivity extends AppCompatActivity {
  EditText display_name;
    Button button1;
    ImageButton image_button;
    private int GALLERY_CODE=1;
    FirebaseAuth mAuth;
    Uri imageUri=null;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth=FirebaseAuth.getInstance();
        ref=FirebaseDatabase.getInstance().getReference().child("users");
        display_name=(EditText)findViewById(R.id.text_display);
        image_button=(ImageButton)findViewById(R.id.image_display);
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);


            }
        });
        button1=(Button)findViewById(R.id.btnsubmit);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setUpAccount();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK)
        {
           imageUri=data.getData();
            image_button.setImageURI(imageUri);
        }
    }

    private void setUpAccount() {
        String name2=display_name.getText().toString();
        final String user_id=mAuth.getCurrentUser().getUid();
        ref.child(user_id).child("name").setValue(name2);
        new Intent(SetupActivity.this,MainActivity.class);

    }
}
