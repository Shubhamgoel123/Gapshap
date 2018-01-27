package com.example.kachucool.gapshap;


import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View mMainView;
    private DatabaseReference mrequestdatabase,musersdatabase,mrootref;
    private FirebaseAuth mAuth;
    private RecyclerView mrequestview;
    private FirebaseUser mcurrentuser;
    private String current_user_id="";

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView= inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        current_user_id=mAuth.getCurrentUser().getUid();

        mrequestdatabase=FirebaseDatabase.getInstance().getReference().child("RequestFragment").child(current_user_id);
        musersdatabase=FirebaseDatabase.getInstance().getReference().child("Users");

        mrootref=FirebaseDatabase.getInstance().getReference();

        mrequestview=(RecyclerView)mMainView.findViewById(R.id.friends_list);
        mcurrentuser=mAuth.getCurrentUser();

        mrequestdatabase.keepSynced(true);
        mrequestview.setHasFixedSize(true);
        mrequestview.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,RequestFragment.UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, RequestFragment.UsersViewHolder>(
                Users.class,
                R.layout.requests_single_layout,
                RequestFragment.UsersViewHolder.class,
                mrequestdatabase

        ) {
            @Override
            protected void populateViewHolder(final RequestFragment.UsersViewHolder usersviewHolder, Users users, int position) {

                final String userid=getRef(position).getKey();

                musersdatabase.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            String username=dataSnapshot.child("name").getValue().toString();
                            String image=dataSnapshot.child("image").getValue().toString();
                            String online=dataSnapshot.child("online").getValue().toString();
                            String status=dataSnapshot.child("status").getValue().toString();

                            usersviewHolder.setImage(image);
                            usersviewHolder.setName(username);
                            usersviewHolder.setStatus(status);

                            final String req_id=dataSnapshot.getKey();

                            usersviewHolder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final String current_date= DateFormat.getDateInstance().format(new Date());

                                    Map friendsMap=new HashMap();
                                    friendsMap.put("Friends/" + current_user_id + "/" + req_id +"/date",current_date);
                                    friendsMap.put("Friends/" + req_id + "/" + current_user_id +"/date",current_date);

                                    friendsMap.put("Friend_req/" + current_user_id + "/" + req_id, null);
                                    friendsMap.put("Friend_req/" + req_id + "/" + current_user_id,null);

                                    friendsMap.put("RequestFragment/" + current_user_id + "/" + req_id,null);

                                    System.out.println(req_id+"hlo"+current_user_id);

                                    mrootref.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                            if(databaseError==null)
                                            {
                                                usersviewHolder.decline.setVisibility(View.INVISIBLE);
                                                usersviewHolder.accept.setText("Friends");
                                            }
                                            else
                                            {
                                                String error=databaseError.getMessage();
                                                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });

                                }
                            });

                            usersviewHolder.decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final DatabaseReference mfrienddatabase,request;
                                    mfrienddatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
                                    request=FirebaseDatabase.getInstance().getReference().child("RequestFragment");

                                    mfrienddatabase.child(current_user_id).child(req_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mfrienddatabase.child(req_id).child(current_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    request.child(current_user_id).child(req_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mrequestview.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        Button accept,decline;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

            accept=mview.findViewById(R.id.usersaccept);
            decline=mview.findViewById(R.id.usersdecline);
        }

        public void setName(String name) {

            TextView musername=(TextView)mview.findViewById(R.id.usersstatusrequest);
            musername.setText(name);

        }

        public void setStatus(String status) {

            TextView muserstatus=(TextView)mview.findViewById(R.id.userdisplay);
            muserstatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView userimage=(CircleImageView)mview.findViewById(R.id.usersingleimagerequest);
            Picasso.with(mview.getContext()).load(image).placeholder(R.drawable.deault_avatar).into(userimage);
        }

        public void setOnline(String image) {

        }
    }
}