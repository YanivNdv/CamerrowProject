package com.camerrow.camerrowproject;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment{

    private View rootView;

    private Button mAddPersonalBtn;

    private DatabaseReference mDatabaseUsers;
    private String user_id;


    private ListView mPersonalListView;

    private FirebaseListAdapter<PersonalObject> firebaseListAdapter;

    private double longitude;
    private double latitude;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            if(rootView == null) {
                Log.d("rootView", "is null");
                rootView = inflater.inflate(R.layout.fragment_personal, container, false);

                mAddPersonalBtn = (Button) rootView.findViewById(R.id.addPersonalBtn);


                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


                mPersonalListView = (ListView) rootView.findViewById(R.id.personalListView);



                firebaseListAdapter = new FirebaseListAdapter<PersonalObject>(
                        getActivity(),
                        PersonalObject.class,
                        android.R.layout.simple_list_item_1,
                        mDatabaseUsers.child(user_id).child("personal")

                ) {
                    @Override
                    protected void populateView(View v, PersonalObject model, int position) {

                        TextView textView = (TextView) v.findViewById(android.R.id.text1);
                        textView.setText(model.getName());

                    }
                };

                mPersonalListView.setAdapter(firebaseListAdapter);


            }

            mAddPersonalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                }
            });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void openDialog() {


        mDatabaseUsers.child(user_id).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("datasnapshot", String.valueOf( dataSnapshot.child("latitude").getKey()));
                latitude = (double) dataSnapshot.child("latitude").getValue();
                longitude = (double) dataSnapshot.child("longitude").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        PersonalDialog personalDialog = new PersonalDialog(latitude,longitude);
        personalDialog.show(getFragmentManager(), "PersonalDialog");
    }



}
