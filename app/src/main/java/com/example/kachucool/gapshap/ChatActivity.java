package com.example.kachucool.gapshap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mchatuser;
    private Toolbar mchattoolbar;
    private DatabaseReference mrootRef;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;

    private FirebaseAuth mAuth;
    private String mcurrentuserid;

    private ImageButton mchataddbtn,mchatsendbtn;
    private EditText mchatmsg;

    private RecyclerView mMessageslist;

    private final List<Messages> messageList=new ArrayList<>();
    private LinearLayoutManager mlinearlayout;

    private MessageAdapter madapter;
    private DatabaseReference mMessageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
            mcurrentuserid=mAuth.getCurrentUser().getUid();

        mchatuser=getIntent().getStringExtra("user_id");
        mMessageDatabase=FirebaseDatabase.getInstance().getReference().child("messages").child(mcurrentuserid).child(mchatuser);

        mchattoolbar=(Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mchattoolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        mchataddbtn=(ImageButton)findViewById(R.id.chat_add_btn);
        mchatsendbtn=(ImageButton)findViewById(R.id.chat_send_btn);
        mchatmsg=(EditText)findViewById(R.id.chat_message_view);

        madapter=new MessageAdapter(messageList);



        mrootRef= FirebaseDatabase.getInstance().getReference();
        String username=getIntent().getStringExtra("user_name");



        getSupportActionBar().setTitle(username);

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view= inflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(action_bar_view);

        // Custom bar items

        mTitleView=(TextView)findViewById(R.id.custom_bar_title);
        mLastSeenView=(TextView)findViewById(R.id.custom_last_seen);
        mProfileImage=(CircleImageView)findViewById(R.id.custom_bar_image);
        mMessageslist=(RecyclerView)findViewById(R.id.messages_list);

        mlinearlayout=new LinearLayoutManager(this);
        mMessageslist.setHasFixedSize(true);
        mMessageslist.setLayoutManager(mlinearlayout);
        //mMessageslist.setAdapter(madapter);
        //loadmessages();

        mTitleView.setText(username);

        mrootRef.child("Users").child(mchatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online= dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.deault_avatar).into(mProfileImage);

                if(online.equals("true"))
                {
                    mLastSeenView.setText("Online");
                }
                else
                {
                    GetTimeAgo getTimeAgo=new GetTimeAgo();
                    long lasttime=Long.parseLong(online);
                    String lastseentime=getTimeAgo.getTimeAgo(lasttime,getApplicationContext());
                    mLastSeenView.setText(lastseentime);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mrootRef.child("Chat").child(mcurrentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mchatuser))
                {
                    Map chataddMap=new HashMap();

                    chataddMap.put("seen",false);
                    chataddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatusermap =new HashMap();

                    chatusermap.put("Chat/"+mcurrentuserid+"/"+mchatuser,chataddMap);
                    chatusermap.put("Chat/"+mchatuser+"/"+mcurrentuserid,chataddMap);

                    mrootRef.updateChildren(chatusermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError!=null)
                            {
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
                            }

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mchatsendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });

    }

    /*private void loadmessages() {

        mrootRef.child("messages").child(mcurrentuserid).child(mchatuser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               Messages message=dataSnapshot.getValue(Messages.class);

                messageList.add(message);
                mMessageslist.scrollToPosition(messageList.size()-1);
                madapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/

    private void sendMessage() {

        final String message=mchatmsg.getText().toString();

        if(!TextUtils.isEmpty(message))
        {
            String current_user_ref="messages/"+mcurrentuserid+"/"+mchatuser;
            String chat_user_ref="messages/"+mchatuser+"/"+mcurrentuserid;

            DatabaseReference user_message_push= mrootRef.child("messages").child(mcurrentuserid).child(mchatuser).push();
            String push_id=user_message_push.getKey();

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mcurrentuserid);

            Map messageUserMap=new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            mchatmsg.setText("");

            mrootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                //    Log.d("CHAT_LOG",databaseError.getMessage().toString());

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Messages,ChatActivity.MessageViewHolder> messageRecyclerViewAdapter=new FirebaseRecyclerAdapter<Messages, ChatActivity.MessageViewHolder>(

                Messages.class,
                R.layout.message_single_layout,
                ChatActivity.MessageViewHolder.class,
                mMessageDatabase

        ) {
            @Override
            protected void populateViewHolder(final ChatActivity.MessageViewHolder viewHolder, Messages message, int position) {

                String list_user_id=getRef(position).getKey();

                mMessageDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            Messages chat=dataSnapshot.getValue(Messages.class);

                            System.out.println(chat.getFrom() +" "+chat.getMessage());
                            viewHolder.setStatus(chat.getFrom(),mcurrentuserid,chat.getMessage());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mMessageslist.setAdapter(messageRecyclerViewAdapter);
    }

    final public static class MessageViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView messageText,receivertext;

        public MessageViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            messageText = (TextView)itemView.findViewById(R.id.message_text_layout);
            receivertext=(TextView)itemView.findViewById(R.id.left_textview);
        }

        public void setStatus(String from_user,String current_user,String message) {

            if(from_user.equals(current_user))
            {
                messageText.setBackgroundResource(R.drawable.message_text_background);
                messageText.setTextColor(Color.BLACK);
                messageText.setText(message);
            }
            else
            {
                receivertext.setBackgroundResource(R.drawable.reciver_message);
                receivertext.setTextColor(Color.BLACK);
                receivertext.setText(message);
            }
        }
    }
}
