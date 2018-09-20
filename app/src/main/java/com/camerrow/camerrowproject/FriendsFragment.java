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

    private EditText mFriendsSearchEditText;
    private RecyclerView mFriendsSearchRecyclerView;
    private ArrayList<CamerrowUser> camerrowUserArrayList;

    private SearchAdapter searchAdapter;
    private LinearLayout mSearchLinearLayout;







    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_friends, container, false);

            mSearchLinearLayout = (LinearLayout) rootView.findViewById(R.id.searchLinearLayout);
            mSearchLinearLayout.bringToFront();


            mFriendsSearchEditText = (EditText) rootView.findViewById(R.id.friendsSearchEditText);
            mFriendsSearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.friendsSearchRecyclerView);

            //search items divider
            DividerItemDecoration myDivider = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL);
            myDivider.setDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.costume_divider));


            mFriendsSearchRecyclerView.addItemDecoration(myDivider );


            camerrowUserArrayList = new ArrayList<>();

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

            //search func

            mFriendsSearchRecyclerView.setHasFixedSize(true);
            mFriendsSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mFriendsSearchRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(),LinearLayoutManager.VERTICAL));

            mFriendsSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().isEmpty()) {
                        setAdapter(s.toString());
                    }
                    else {
                        camerrowUserArrayList.clear();
                        mFriendsSearchRecyclerView.removeAllViews();
                    }
                }
            });



        }


            // Inflate the layout for this fragment
            return rootView;




    }

    private void setAdapter(final String searchedString) {



        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                camerrowUserArrayList.clear();
                mFriendsSearchRecyclerView.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    CamerrowUser camerrowUser = new CamerrowUser();
                    String uid = snapshot.getKey();
                    camerrowUser.setName(snapshot.child("name").getValue().toString());
                    camerrowUser.setUsername(snapshot.child("username").getValue().toString());
                    camerrowUser.setEmail(snapshot.child("email").getValue().toString());
                    camerrowUser.setProfilePicture(snapshot.child("image").getValue().toString());
                    camerrowUser.setDatabaseKey(uid);

                    if(camerrowUser.getName().toLowerCase().contains(searchedString.toLowerCase())){
                        camerrowUserArrayList.add(camerrowUser);
                        counter++;

                    } else if (camerrowUser.getUsername().toLowerCase().contains(searchedString.toLowerCase())) {
                        camerrowUserArrayList.add(camerrowUser);
                        counter++;

                    }

                    if (counter == 15)
                        break;

                }

                searchAdapter = new SearchAdapter(getActivity(), camerrowUserArrayList, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!isLongClick){
                            if(camerrowUserArrayList.get(position).getDatabaseKey() != user_id)
                                addFriend(camerrowUserArrayList.get(position));
                        } else {
                            removeFriend(camerrowUserArrayList.get(position));
                        }
                        mFriendsSearchEditText.setText("");
                        hideSoftKeyboard(getActivity());



                    }
                });

                mFriendsSearchRecyclerView.setAdapter(searchAdapter);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void removeFriend(CamerrowUser camerrowUser) {
        mDatabseFriends.child(user_id).child(camerrowUser.getDatabaseKey()).removeValue();

    }

    private void addFriend(CamerrowUser camerrowUser) {

        DatabaseReference friendDatabase = mDatabseFriends.child(user_id).child(camerrowUser.getDatabaseKey());

        friendDatabase.child("key").setValue(camerrowUser.getDatabaseKey());
        friendDatabase.child("name").setValue(camerrowUser.getName());
        friendDatabase.child("username").setValue(camerrowUser.getUsername());
        friendDatabase.child("email").setValue(camerrowUser.getEmail());
        friendDatabase.child("picture").setValue(camerrowUser.getProfilePicture());



    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


}
