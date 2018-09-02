package com.camerrow.camerrowproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

     private EditText mEmailTV;
     private EditText mPasswordTV;

     private Button mLoginBtn;
     private Button mRegisterNowBtn;

     private FirebaseAuth mAuth;
     private DatabaseReference mDatabaseUsers;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mProgressDialog = new ProgressDialog(this);


        mEmailTV = (EditText) findViewById(R.id.lEmailTF);
        mPasswordTV = (EditText) findViewById(R.id.lPasswordTF);

        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mRegisterNowBtn = (Button) findViewById(R.id.lRegisterBtn);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();

            }
        });

        mRegisterNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to Register Activity
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });
    }

    private void checkLogin() {

        String email = mEmailTV.getText().toString().trim();
        String password = mPasswordTV.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Login in...");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        mProgressDialog.dismiss();
                        checkUserExist();

                    } else {

                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {
                    Toast.makeText(LoginActivity.this, "Please set up your account.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
