package com.example.kachucool.gapshap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {


    private Button mbutton,mbutton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mbutton=(Button)findViewById(R.id.button1);
        mbutton1=(Button)findViewById(R.id.button3);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

        mbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(reg_intent);
            }
        });


    }




}
