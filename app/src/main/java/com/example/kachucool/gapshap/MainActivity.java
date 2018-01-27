package com.example.kachucool.gapshap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mtoolbar;
    private ViewPager mviewpager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DatabaseReference mUserRef;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar=(Toolbar)findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("gapshap");

        if(mAuth.getCurrentUser()!=null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mviewpager=(ViewPager)findViewById(R.id.main_tabpager);
        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mSectionsPagerAdapter);
        mTabLayout=(TabLayout)findViewById(R.id.main_tabs);

        mTabLayout.setupWithViewPager(mviewpager);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("jijijj");
                    mUserRef.child("online").setValue(true);

                } else {
                    // User is signed out
                    sendtostart();
                }
                // ...
            }
        };

    }

    private void sendtostart() {

        Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mAuth!=null && mAuthListener!=null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null)
        {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout)
        {
            FirebaseAuth.getInstance().signOut();
            sendtostart();
        }

        if(item.getItemId()==R.id.main_accountsettings)
        {
           Intent settingintent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingintent);
        }

        if(item.getItemId()==R.id.main_allusers)
        {
            Intent userintent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(userintent);
        }
        return true;
    }
}
