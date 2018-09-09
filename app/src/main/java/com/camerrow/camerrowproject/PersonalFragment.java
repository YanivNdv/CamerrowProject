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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {

    private View rootView;

    private DatabaseReference mDatabaseUsers;
    private String user_id;


    private ListView mPersonalListView;

    private ArrayList<String> mPersonalArrayItems = new ArrayList<>();

    private ArrayAdapter<String> mPersonalArrayItemsAdapter;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            if(rootView == null) {
                Log.d("rootView", "is null");
                rootView = inflater.inflate(R.layout.fragment_personal, container, false);


                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


                mPersonalListView = (ListView) rootView.findViewById(R.id.personalListView);

                mPersonalArrayItemsAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, mPersonalArrayItems);


                mPersonalListView.setAdapter(mPersonalArrayItemsAdapter);

//                FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
//                        getActivity(),
//                        String.class,
//                        android.R.layout.simple_list_item_1,
//                        mDatabaseUsers.child(user_id).child("personal")
//
//                ) {
//                    @Override
//                    protected void populateView(View v, String model, int position) {
//
//                        TextView textView = (TextView) v.findViewById(android.R.id.text1);
//                        textView.setText(model);
//
//                    }
//                };
//
//                mPersonalListView.setAdapter(firebaseListAdapter);


                mDatabaseUsers.child(user_id).child("personal").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String value = dataSnapshot.child("name").getValue().toString();
                        mPersonalArrayItems.add(value);
                        mPersonalArrayItemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        // Inflate the layout for this fragment
        return rootView;
    }


}
