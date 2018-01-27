package com.example.kachucool.gapshap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity
{

    private Button b;
    private Toolbar mtoolbar;
    private TextInputLayout mloginemail;
    private TextInputLayout mloginpassword;
    private ProgressDialog mloginprogress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthlistener;
    private DatabaseReference muserdatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mtoolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        muserdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mloginprogress=new ProgressDialog(LoginActivity.this);
        b=(Button)findViewById(R.id.login_button);
        mloginemail=(TextInputLayout)findViewById(R.id.login_email);
        mloginpassword=(TextInputLayout)findViewById(R.id.login_password);

        mAuth = FirebaseAuth.getInstance();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mloginemail.getEditText().getText().toString();
                String password=mloginpassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    mloginprogress.setTitle("logging in");
                    mloginprogress.setMessage("please wait while checking");
                    mloginprogress.setCanceledOnTouchOutside(false);
                    mloginprogress.show();
                    loginUser(email,password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            mloginprogress.dismiss();

                            String current_userid=mAuth.getCurrentUser().getUid();
                            String devicetoken= FirebaseInstanceId.getInstance().getToken();

                            muserdatabase.child(current_userid).child("device_token").setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mainintent=new Intent(LoginActivity.this,MainActivity.class);
                                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainintent);
                                    finish();

                                }
                            });



                        }
                        else
                        {
                            mloginprogress.hide();
                            Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}