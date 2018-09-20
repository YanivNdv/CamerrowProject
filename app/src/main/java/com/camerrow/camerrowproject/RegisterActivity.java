package com.camerrow.camerrowproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFullNameTV;
    private EditText mUsernameTV;
    private EditText mEmailTV;
    private EditText mPasswordTV;

    private Button mRegisterBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgressDialog = new ProgressDialog(this);

        mFullNameTV = (EditText) findViewById(R.id.rFullNameTF);
        mUsernameTV = (EditText) findViewById(R.id.rUsernameTF);
        mEmailTV = (EditText) findViewById(R.id.rEmailTF);
        mPasswordTV = (EditText) findViewById(R.id.rPasswordTF);

        mRegisterBtn = (Button) findViewById(R.id.registerBtn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String fullName = mFullNameTV.getText().toString().trim();
        final String userName = mUsernameTV.getText().toString().trim();
        final String email = mEmailTV.getText().toString().trim();
        String password = mPasswordTV.getText().toString().trim();

        if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Signing Up...");
            mProgressDialog.show();
                 mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){

                             //get user UID
                             String user_id = mAuth.getCurrentUser().getUid();
                             //db reference to the new user
                             DatabaseReference current_user_db = mDatabaseUsers.child(user_id);
                             //add details of the new user
                             current_user_db.child("name").setValue(fullName);
                             current_user_db.child("username").setValue(userName);
                             current_user_db.child("email").setValue(email);
                             current_user_db.child("image").setValue("default");
                             //dismiss progress dialog
                             mProgressDialog.dismiss();

                             Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                             loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             startActivity(loginIntent);

                         }
                     }
                 });
        }
    }
}
