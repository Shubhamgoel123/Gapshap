package com.example.kachucool.gapshap;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView displayname,status,friends;
    private ImageView mprofileimage;
    private Button mprofilesendreq,mprofiledeclinebtn;

    private DatabaseReference mprofiledatabase,mfrienddatabase,mfriendssbdatabase,mnotificationdatabase,mRootRef,request;

    private ProgressDialog mprogressdialog;
    private String current_status;
    private FirebaseUser mcurrentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mprogressdialog=new ProgressDialog(ProfileActivity.this);
        String userid=getIntent().getStringExtra("user_id");
        mprofiledatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mfrienddatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mfriendssbdatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mnotificationdatabase=FirebaseDatabase.getInstance().getReference().child("Notifications");
        request=FirebaseDatabase.getInstance().getReference().child("RequestFragment");
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();

        final String user_id=getIntent().getStringExtra("user_id");
        displayname=(TextView)findViewById(R.id.profile_name);
        status=(TextView)findViewById(R.id.profile_status);
        friends=(TextView)findViewById(R.id.profile_friends);

        mprofileimage=(ImageView)findViewById(R.id.profile_imageView);
        mprofilesendreq=(Button)findViewById(R.id.profile_sendRequest_button);
        mprofiledeclinebtn=(Button)findViewById(R.id.profile_decline_frd_req);

        mprofiledeclinebtn.setVisibility(View.INVISIBLE);
        mprofiledeclinebtn.setEnabled(false);

        current_status="not_friends";
        mprogressdialog.setTitle("Loading user data");
        mprogressdialog.setMessage("Please wait");
        mprogressdialog.setCanceledOnTouchOutside(false);
        mprogressdialog.show();



        mprofiledatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name=dataSnapshot.child("name").getValue().toString();
                String status_name=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                displayname.setText(display_name);
                status.setText(status_name);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.deault_avatar).into(mprofileimage);

                //Friends List / Reaquest feature

                mfrienddatabase.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if(dataSnapshot.hasChild(user_id))
                        {
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received"))
                            {

                                current_status="req_received";
                                mprofilesendreq.setText("Accept Friend Request");

                                mprofiledeclinebtn.setVisibility(View.VISIBLE);
                                mprofiledeclinebtn.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                            {

                                current_status="req_sent";
                                mprofilesendreq.setText("Cancel  Friend Request");

                                mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                mprofiledeclinebtn.setEnabled(false);
                            }
                            mprogressdialog.dismiss();
                        }
                        else
                        {
                            mfriendssbdatabase.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        current_status="friends";
                                        mprofilesendreq.setText("unfriend this person");

                                        mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                        mprofiledeclinebtn.setEnabled(false);
                                    }

                                    mprogressdialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mprogressdialog.dismiss();
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mprofilesendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mprofilesendreq.setEnabled(false);

                // Not friends State
                if(current_status.equals("not_friends"))
                {

                    DatabaseReference newNotificationref=mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationid=newNotificationref.getKey();

                    HashMap<String,String> notificationData=new HashMap<String, String>();
                    notificationData.put("from",mcurrentuser.getUid());
                    notificationData.put("type","request");

                    Map requestmap=new HashMap();


                    Map request1=new HashMap();
                    request1.put("req_type","received");


                    request.child(user_id).child(mcurrentuser.getUid()).setValue(request1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                    requestmap.put("Friend_req/"+ mcurrentuser.getUid()+ "/" + user_id +"/request_type","sent");
                    requestmap.put("Friend_req/"+ user_id + "/"+ mcurrentuser.getUid() + "/request_type","received");
                    requestmap.put("Notifications/"+user_id+"/"+newNotificationid,notificationData);

                    mRootRef.updateChildren(requestmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError!=null)
                            {
                                Toast.makeText(ProfileActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }

                            mprofilesendreq.setEnabled(true);
                            current_status="req_sent";
                            mprofilesendreq.setText("Cancel Friend Request");
                        }
                    });
                }

                // Cancel state

                if(current_status.equals("req_sent"))
                {
                    mfrienddatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfrienddatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    request.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mprofilesendreq.setEnabled(true);
                                            current_status="not_friends";
                                            mprofilesendreq.setText("Send Friend Request");

                                            mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                            mprofiledeclinebtn.setEnabled(false);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                // Request Received State

                if(current_status.equals("req_received"))
                {
                    final String current_date= DateFormat.getDateInstance().format(new Date());

                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/" + mcurrentuser.getUid() + "/" + user_id +"/date",current_date);
                    friendsMap.put("Friends/" + user_id + "/" + mcurrentuser.getUid() +"/date",current_date);

                    friendsMap.put("Friend_req/" + mcurrentuser.getUid() + "/" +user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mcurrentuser.getUid(),null);

                    friendsMap.put("RequestFragment/" + user_id + "/" + mcurrentuser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null)
                            {
                                mprofilesendreq.setEnabled(true);
                                current_status="friends";
                                mprofilesendreq.setText("Unfriend the person");

                                mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                mprofiledeclinebtn.setEnabled(false);
                            }
                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }


                //Unfriend

                if(current_status.equals("friends"))
                {

                    Map umfriendMap=new HashMap();
                    umfriendMap.put("Friends/"+ mcurrentuser.getUid() + "/" + user_id,null);
                    umfriendMap.put("Friends/"+ user_id + "/" + mcurrentuser.getUid(),null);

                    mRootRef.updateChildren(umfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null)
                            {
                                mprofilesendreq.setEnabled(true);
                                current_status="friends";
                                mprofilesendreq.setText("Unfriend the person");

                                mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                mprofiledeclinebtn.setEnabled(false);
                            }
                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }

            }
        });

        mprofiledeclinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mfrienddatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mfrienddatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                request.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mprofilesendreq.setEnabled(true);
                                        current_status="not_friends";
                                        mprofilesendreq.setText("Send Friend Request");

                                        mprofiledeclinebtn.setVisibility(View.INVISIBLE);
                                        mprofiledeclinebtn.setEnabled(false);
                                    }
                                });
                            }
                        });
                    }
                });


            }
        });

    }
}
