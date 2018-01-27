package com.example.kachucool.gapshap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserdatabase,muserref;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayimage;
    private TextView mName;
    private TextView mStatus;

    private Button mstatusbutton,mimage;
    private static final int reqcode=1;

    private StorageReference mImageStorage;

    private ProgressDialog mprogressdialog;
    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mImageStorage= FirebaseStorage.getInstance().getReference();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mUserdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserdatabase.keepSynced(true);

        mDisplayimage=(CircleImageView)findViewById(R.id.setting_image);
        mName=(TextView)findViewById(R.id.textView3);
        mStatus=(TextView)findViewById(R.id.textView4);
        mstatusbutton=(Button)findViewById(R.id.button5);
        mimage=(Button)findViewById(R.id.button4);

        mauth=FirebaseAuth.getInstance();

        mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMGAE"),reqcode);


               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
            }
        });


        mstatusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value=mStatus.getText().toString();
                Intent statusintent=new Intent(SettingsActivity.this,StatusActivity.class);
                statusintent.putExtra("status_value",status_value);
                startActivity(statusintent);
            }
        });


        mUserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if(!image.equals("default")) {
                   // Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.deault_avatar).into(mDisplayimage);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.deault_avatar).into(mDisplayimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.deault_avatar).into(mDisplayimage);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==reqcode && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mprogressdialog=new ProgressDialog(SettingsActivity.this);
                mprogressdialog.setTitle("Uploading Image");
                mprogressdialog.setMessage("Please Wait while we process the image");
                mprogressdialog.setCanceledOnTouchOutside(false);
                mprogressdialog.show();
                Uri resultUri = result.getUri();

                String current_userid=mCurrentUser.getUid();
                final File thumb_file=new File(resultUri.getPath());
                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    StorageReference filepath = mImageStorage.child("profile_images").child(current_userid + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_userid + ".jpg");


                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                       final public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    final public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> thumb_task) {

                                        String thumb_donloadurl = task.getResult().getDownloadUrl().toString();
                                        if (thumb_task.isSuccessful()) {

                                            Map upadte_hashmap = new HashMap();
                                            upadte_hashmap.put("image", download_url);
                                            upadte_hashmap.put("thumb_image", thumb_donloadurl);
                                            mUserdatabase.updateChildren(upadte_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mprogressdialog.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "yoyo", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                mprogressdialog.dismiss();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }



    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }


        return randomStringBuilder.toString();
    }

}
