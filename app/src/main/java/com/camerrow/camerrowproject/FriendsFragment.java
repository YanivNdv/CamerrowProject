package com.camerrow.camerrowproject;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private View rootView;

    private DatabaseReference mDatabseFriends;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private String user_id;
    private RecyclerView mFriendsRecyclerView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_friends, container, false);

            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabseFriends = FirebaseDatabase.getInstance().getReference().child("Friends");
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mFriendsRecyclerView = (RecyclerView) rootView.findViewById(R.id.friendsRecyclerView);
            mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            mFriendsRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL));



            FirebaseRecyclerAdapter<FriendObject, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<FriendObject, FriendsViewHolder>(
                    FriendObject.class,
                    R.layout.friend_object,
                    FriendsViewHolder.class,
                    mDatabseFriends.child(user_id)) {
                @Override
                protected void populateViewHolder(final FriendsViewHolder viewHolder, final FriendObject model, int position) {

                    final String friendObjectKey = getRef(position).getKey();
                    viewHolder.setName(model.getName());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setImage(model.getImage(),getContext());


                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), model.getKey(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                                Toast.makeText(getActivity(), "Long Clicked on " + personalObjectKey, Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(v.getRootView().getContext(),R.style.MyDialogTheme)
                                    .setMessage("Are you sure you want to remove " + model.getUsername() + "?" )
                                    .setTitle("Remove Friend")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mDatabseFriends.child(user_id).child(friendObjectKey).removeValue();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                            return true;
                        }
                    });
//
                }
            };


            mFriendsRecyclerView.setAdapter(adapter);


        }


            // Inflate the layout for this fragment
            return rootView;




    }

}
