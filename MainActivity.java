package com.example.abhinav.myblog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
RecyclerView recyclerView;
    ImageView imageView;
    String comment;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private DatabaseReference refLike;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    Blog blog;
    private boolean mLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        ref= FirebaseDatabase.getInstance().getReference().child("users");
        refLike= FirebaseDatabase.getInstance().getReference().child("Likes");
        ref.keepSynced(true);
        blog=new Blog();
        imageView=(ImageView)findViewById(R.id.image_like);
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("Outside","Outside");
                if(firebaseAuth.getCurrentUser()==null)
                {

                    Log.i("Inside","Inside");
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                  //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getApplicationContext(),"I am in main",Toast.LENGTH_LONG).show();
                }


            }
        };
        recyclerView=(RecyclerView)findViewById(R.id.blog_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkExistUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkExistUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        final FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setName(model.getUsername());
                viewHolder.setImage(getApplicationContext(),model.getImages());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_LONG).show();
                    }
                });
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLike=true;
                        refLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mLike) {
                                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                        refLike.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        mLike=false;

                                    } else {
                                        refLike.child(firebaseAuth.getCurrentUser().getUid()).setValue("Random Value");
                                        mLike = false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });
                viewHolder.imageview1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                      viewHolder.editText.setVisibility(View.VISIBLE);

                    comment=viewHolder.editText.getText().toString();
                        if(comment!=null) {
                            viewHolder.fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Toast.makeText(getApplicationContext(), comment, Toast.LENGTH_LONG).show();
                                    databaseReference.child(post_key).child(firebaseAuth
                                            .getCurrentUser().getUid()).child("comment").setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Comments uploaded", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Comments uploading error", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Null uploaded", Toast.LENGTH_LONG).show();
                        }



                    }
                });


            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void checkExistUser() {
        if(firebaseAuth.getCurrentUser()!=null) {
            final String user_id = firebaseAuth.getCurrentUser().getUid();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ImageView imageView,imageview1;
        EditText editText;
       FloatingActionButton fab;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            imageView=(ImageView)itemView.findViewById(R.id.image_like);
          //  imageblog=(ImageView)itemView.findViewById(R.id.image_blog);
            imageview1=(ImageView)itemView.findViewById(R.id.comments);
            editText=(EditText)itemView.findViewById(R.id.edit_comments);
            fab=(FloatingActionButton)itemView.findViewById(R.id.fab);
            editText.setVisibility(View.INVISIBLE);

        }

        public void setTitle(String title)
        {
            TextView blog_title=(TextView)mView.findViewById(R.id.blog_title);
            blog_title.setText(title);

        }
        public void setDesc(String description)
        {
            TextView blog_desc=(TextView)mView.findViewById(R.id.blog_desc);
            blog_desc.setText(description);
        }
        public void setName(String username)
        {
            TextView blog_name=(TextView)mView.findViewById(R.id.id_name);
            blog_name.setText(username);
        }
        public void setImage(Context context,String image)
        {
           ImageView blog_image=(ImageView) mView.findViewById(R.id.image_blog);
            Picasso.with(context).load(image).into(blog_image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.add)
        {
               startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.logout)
        {
            firebaseAuth.signOut();
        }
        return true;

    }
}
