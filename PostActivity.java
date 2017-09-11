package com.example.abhinav.myblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    ImageButton imageButton;
    private static int GALLERY_REQUEST=1;
    Button button;
    Uri uri;
    EditText title,disc;
    private StorageReference storageRef;
   private ProgressDialog prog;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference data;
    DatabaseReference ref1;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageButton=(ImageButton)findViewById(R.id.imgBtn);
        title=(EditText)findViewById(R.id.post);
        disc=(EditText)findViewById(R.id.full);
        button=(Button)findViewById(R.id.btn);
        prog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference();
        data= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                startActivityForResult(intent1,GALLERY_REQUEST);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting()
    {
        prog.setMessage("Posting Your Image....");
        final String title1=title.getText().toString();
        final String desc=disc.getText().toString();
        StorageReference filepath=storageRef.child("Blog_images").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // @SuppressWarnings("VisibleForTests")
                 ref1=data.push();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                ref1.child("title").setValue(title1);
                ref1.child("description").setValue(desc);
                 ref1.child("images").setValue(downloadUrl.toString());
                ref1.child("uid").setValue(mCurrentUser.getUid());

                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ref1.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    prog.dismiss();
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something Wrong Happened...Try Later", Toast.LENGTH_SHORT);
                                }

                            }
                        });
                    }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            uri=data.getData();
            Toast.makeText(getApplicationContext(),uri.getLastPathSegment(),Toast.LENGTH_LONG).show();
            imageButton.setImageURI(uri);
        }

}
    }

