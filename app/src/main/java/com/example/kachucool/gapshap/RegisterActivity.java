package com.example.kachucool.gapshap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout display, email, password;
    private Button b;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthlistener;
    private Toolbar mtoolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        display = (TextInputLayout) findViewById(R.id.textInputLayout);
        email = (TextInputLayout) findViewById(R.id.textInputLayout2);
        password = (TextInputLayout) findViewById(R.id.textInputLayout3);

        mtoolbar=(Toolbar)findViewById(R.id.registerappbarlayout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true );

        mRegProgress=new ProgressDialog(RegisterActivity.this);

        mAuth = FirebaseAuth.getInstance();
        b = (Button) findViewById(R.id.button2);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayname = display.getEditText().getText().toString();
                String emailid = email.getEditText().getText().toString();
                String mpassword = password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(displayname) || !TextUtils.isEmpty(emailid) || !TextUtils.isEmpty(mpassword))
                {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please Wait while we register");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(displayname, emailid, mpassword);
                }



            }
        });
    }

    private void register_user(final String displayname, String emailid, String mpassword) {
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(emailid, mpassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, "auth failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {


                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid=current_user.getUid();
                            String devicetoken= FirebaseInstanceId.getInstance().getToken();
                            database=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> usermap=new HashMap<String, String>();
                            usermap.put("device_token",devicetoken);
                            usermap.put("name",displayname);
                            usermap.put("status","Hi there using gapshap");
                            usermap.put("image","default");
                            usermap.put("thumb_image","default");

                            database.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                         mRegProgress.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                                    }

                                }
                            });



                        }

                        // ...
                    }
                });
    }
}



