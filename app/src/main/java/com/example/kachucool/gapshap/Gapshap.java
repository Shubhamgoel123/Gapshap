package com.example.kachucool.gapshap;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Kachucool on 04-10-2017.
 */

public class Gapshap extends Application {

    private DatabaseReference muserdatabase;
    private FirebaseAuth mauth;
    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picaaso offline

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

       mauth=FirebaseAuth.getInstance();

        if(mauth.getCurrentUser()!=null) {
            String user_id = mauth.getCurrentUser().getUid();
            muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            muserdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        muserdatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        muserdatabase.child("online").setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }
}
