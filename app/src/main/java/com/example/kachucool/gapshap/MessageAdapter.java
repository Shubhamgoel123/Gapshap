package com.example.kachucool.gapshap;

/**
 * Created by Kachucool on 20-10-2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,receivertext;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView time;

        public MessageViewHolder(View view) {
            super(view);

            for(int i=0; i<mMessageList.size(); i++)
                System.out.print(mMessageList.get(i).getMessage()+"678 ");

            System.out.println("");

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            receivertext=(TextView)view.findViewById(R.id.left_textview);
            //time=(TextView)view.findViewById(R.id.time_text_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        mAuth=FirebaseAuth.getInstance();
        String current_user="";
        if(mAuth.getCurrentUser()!=null) {
           current_user = mAuth.getCurrentUser().getUid();
        }

        String from_user = c.getFrom();

        if(from_user.equals(current_user))
        {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.messageText.setText(c.getMessage());
        }
        else
        {
            viewHolder.receivertext.setBackgroundResource(R.drawable.reciver_message);
            viewHolder.receivertext.setTextColor(Color.BLACK);
            viewHolder.receivertext.setText(c.getMessage());
        }



    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



}
