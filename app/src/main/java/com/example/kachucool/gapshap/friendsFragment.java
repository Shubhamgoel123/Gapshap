package com.example.kachucool.gapshap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class friendsFragment extends Fragment {

    private RecyclerView mFriendList;
    private  DatabaseReference mFriendsDatabase,musersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public friendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView= inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList=(RecyclerView)mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();

        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        mFriendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        musersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        musersDatabase.keepSynced(true);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
        }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerViewAdapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_singlelayout,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends friends, int position) {

                viewHolder.setDate(friends.getDate());

                final String list_user_id=getRef(position).getKey();

                musersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String user_name=dataSnapshot.child("name").getValue().toString();
                        String userthumb=dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online"))
                        {
                            String userOnline= dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }


                        viewHolder.setName(user_name);
                        viewHolder.setImage(userthumb);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[]=new CharSequence[]{"Open Profile", "Send Message"};

                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Events

                                        if(i==0)
                                        {
                                            Intent profileintent=new Intent(getContext(),ProfileActivity.class);
                                            profileintent.putExtra("user_id",list_user_id);
                                            startActivity(profileintent);
                                        }

                                        if(i==1)
                                        {
                                            Intent profileintent=new Intent(getContext(),ChatActivity.class);
                                            profileintent.putExtra("user_id",list_user_id);
                                            profileintent.putExtra("user_name",user_name);
                                            startActivity(profileintent);
                                        }

                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mFriendList.setAdapter(friendsRecyclerViewAdapter);
    }

    final public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setDate(String date) {

            TextView userStatusView=(TextView)mView.findViewById(R.id.userstatussingle);
            userStatusView.setText(date);
        }

        public void setName(String name)
        {
            TextView  userNameView=(TextView)mView.findViewById(R.id.userdisplay);
            userNameView.setText(name);
        }

        public void setImage(String image) {
            CircleImageView userimage=(CircleImageView)mView.findViewById(R.id.usersingleimage);
            Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.deault_avatar).into(userimage);
        }

        public void setUserOnline(String online)
        {
            ImageView useronlineview=(ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online.equals("true"))
            {
                useronlineview.setVisibility(View.VISIBLE);
            }
            else
            {
                useronlineview.setVisibility(View.INVISIBLE);
            }
        }
    }
}