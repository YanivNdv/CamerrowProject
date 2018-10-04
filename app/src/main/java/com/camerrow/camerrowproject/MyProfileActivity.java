package com.camerrow.camerrowproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImageView;
    private TextView mChangeProfileImageTV;
    private EditText mChangeNameField;
    private EditText mChangeUsernameField;
    private EditText mChangeEmailField;

    //firebase
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseUsers;
    private String user_id;

    private static final int GALLERY_INTENT = 2;

    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProfileImageView = (CircleImageView) findViewById(R.id.myProfileImageView);
        mChangeProfileImageTV = (TextView) findViewById(R.id.changeProfileImageTV);
        mChangeNameField = (EditText) findViewById(R.id.changeNameField);
        mChangeUsernameField = (EditText) findViewById(R.id.changeUsernameField);
        mChangeEmailField = (EditText) findViewById(R.id.changeEmailField);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mProgress = new ProgressDialog(this );

        setListeneres();
        setOnClicks();




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    private void setOnClicks() {

        final String prev_name = mChangeNameField.getText().toString();
        mChangeProfileImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyProfileActivity.this, "Clicked on your change image", Toast.LENGTH_SHORT).show();
            }
        });

        mChangeNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("name",prev_name);
            }
        });




    }

    private void openDialog(String field, String prev) {

    }

    private void setListeneres() {
        //name
        mDatabaseUsers.child(user_id).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChangeNameField.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //username
        mDatabaseUsers.child(user_id).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChangeUsernameField.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //email
        mDatabaseUsers.child(user_id).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChangeEmailField.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //profile picture
        mDatabaseUsers.child(user_id).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(mProfileImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            final Uri uri = data.getData();
            final StorageReference filePath = mStorageReference.child("ProfileImages").child(user_id);



            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            mDatabaseUsers.child(user_id).child("image").setValue(uri.toString());
                        }
                    });

                    mProgress.dismiss();
                    Toast.makeText(MyProfileActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MyProfileActivity.this, "Upload Failed, Please try again", Toast.LENGTH_SHORT).show();


                }
            });


        }
    }
}
