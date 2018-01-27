package com.example.kachucool.gapshap;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;
    private DatabaseReference mstatusdatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mprogressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();



        mstatusdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mtoolbar=(Toolbar)findViewById(R.id.status_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value=getIntent().getStringExtra("status_value");

        mStatus=(TextInputLayout)findViewById(R.id.status_input);
        mSavebtn=(Button)findViewById(R.id.status_button);

        mStatus.getEditText().setText(status_value);
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogressdialog=new ProgressDialog(StatusActivity.this);
                mprogressdialog.setTitle("Saving Changes");
                mprogressdialog.setMessage("Please wait");
                mprogressdialog.show();
                String status= mStatus.getEditText().getText().toString();
                mstatusdatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mprogressdialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this,"error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}
