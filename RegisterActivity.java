package com.example.abhinav.myblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
   EditText name,email,password;
  Button button;
    FirebaseAuth mAuth;
    DatabaseReference mData;
    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText)findViewById(R.id.edit_name);
        email=(EditText)findViewById(R.id.edit_email);
        mAuth=FirebaseAuth.getInstance();
        prog=new ProgressDialog(this);
        mData=FirebaseDatabase.getInstance().getReference().child("users");
        password=(EditText)findViewById(R.id.edit_password);
        button=(Button)findViewById(R.id.btn_reg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        prog.setMessage("Signing up.........");
        prog.show();
        final String name1=name.getText().toString();
        String email1=email.getText().toString();
        String password1=password.getText().toString();
        if(!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(email1)  && !TextUtils.isEmpty(password1))
        {
            mAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUser=mData.child(user_id);
                        currentUser.child("name").setValue(name1);
                        currentUser.child("image").setValue("default");
                        prog.dismiss();
                         Intent intent=  new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
