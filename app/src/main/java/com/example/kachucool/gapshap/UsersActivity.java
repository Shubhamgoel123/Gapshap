package com.example.kachucool.gapshap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    private Toolbar mtoolbar;
    private RecyclerView mUserList;
    private DatabaseReference musersdatbase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mtoolbar=(Toolbar)findViewById(R.id.usersappbar);
        mUserList=(RecyclerView)findViewById(R.id.users_list);

        musersdatbase= FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_singlelayout,
                UsersViewHolder.class,
                musersdatbase

        ) {


            @Override
            protected void populateViewHolder(UsersViewHolder usersviewHolder, Users users, int position) {

                usersviewHolder.setName(users.getName());
                usersviewHolder.setStatus(users.getStatus());
                usersviewHolder.setImage(users.getThumb_image());

                final String userid=getRef(position).getKey();
                usersviewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileintent=new Intent(UsersActivity.this,ProfileActivity.class);
                        profileintent.putExtra("user_id",userid);
                        startActivity(profileintent);
                    }
                });

            }
        };

        mUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mview=itemView;
        }

        public void setName(String name) {

            TextView musername=(TextView)mview.findViewById(R.id.userdisplay);
            musername.setText(name);

        }

        public void setStatus(String status) {

            TextView muserstatus=(TextView)mview.findViewById(R.id.userstatussingle);
            muserstatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView userimage=(CircleImageView)mview.findViewById(R.id.usersingleimage);
            Picasso.with(mview.getContext()).load(image).placeholder(R.drawable.deault_avatar).into(userimage);
        }

        public void setOnline(String aTrue) {
        }
    }
}
