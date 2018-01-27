package com.example.kachucool.gapshap;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.sql.Types.NULL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView mChatList;
    private final List<Messages> messageList=new ArrayList<>();
    private DatabaseReference mchatsDatabase,musersDatabase,mroot;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id="yoyo";

    private View mMainView;
    private String list_user_id="";
    private String message="";

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView= inflater.inflate(R.layout.fragment_chats, container, false);

        mChatList=(RecyclerView)mMainView.findViewById(R.id.chats_list);
        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        mchatsDatabase= FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mchatsDatabase.keepSynced(true);
        musersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        musersDatabase.keepSynced(true);

        mAuth=FirebaseAuth.getInstance();

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Friends,ChatsFragment.ChatsViewHolder> friendsRecyclerViewAdapter=new FirebaseRecyclerAdapter<Friends, ChatsFragment.ChatsViewHolder>(

                Friends.class,
                R.layout.users_singlelayout,
                ChatsFragment.ChatsViewHolder.class,
                mchatsDatabase

        ) {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, Friends friends, int position) {

               list_user_id=getRef(position).getKey();

                musersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String chat_id=dataSnapshot.getKey();
                        final String user_name=dataSnapshot.child("name").getValue().toString();
                        final String userthumb=dataSnapshot.child("thumb_image").getValue().toString();
                        final String online=dataSnapshot.child("online").getValue().toString();

                        if(online.equals("true"))
                        {
                            viewHolder.setStatus("online");
                        }
                        else
                        {
                            GetTimeAgo getTimeAgo=new GetTimeAgo();
                            long lasttime=Long.parseLong(online);
                            String lastseentime=getTimeAgo.getTimeAgo(lasttime,getContext());
                            viewHolder.setStatus(lastseentime);
                        }

                        viewHolder.setName(user_name);
                        viewHolder.setImage(userthumb);

                        messageList.clear();
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                            Intent profileintent=new Intent(getContext(),ChatActivity.class);
                                            profileintent.putExtra("user_id",chat_id);
                                            profileintent.putExtra("user_name",user_name);
                                            startActivity(profileintent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mChatList.setAdapter(friendsRecyclerViewAdapter);
    }

    final public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setStatus(String date) {
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

