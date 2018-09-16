package com.camerrow.camerrowproject;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment{

    private View rootView;

    private Button mAddPersonalBtn;

    //maybe don't need
    private DatabaseReference mDatabaseUsers;
    //used instead
    private DatabaseReference mDatabasePersonal;

    private String user_id;


//    private ListView mPersonalListView;
    private RecyclerView mPersonalRecyclerView;

//    private FirebaseListAdapter<PersonalObject> firebaseListAdapter;

    private double longitude;
    private double latitude;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            if(rootView == null) {
                rootView = inflater.inflate(R.layout.fragment_personal, container, false);

                mAddPersonalBtn = (Button) rootView.findViewById(R.id.addPersonalBtn);

                mDatabasePersonal = FirebaseDatabase.getInstance().getReference().child("Personal");
                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


//                mPersonalListView = (ListView) rootView.findViewById(R.id.personalListView);
                mPersonalRecyclerView = (RecyclerView) rootView.findViewById(R.id.personalRecyclerView);
                mPersonalRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                mPersonalRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL));

                FirebaseRecyclerAdapter<PersonalObject, PersonalViewHolder> adapter = new FirebaseRecyclerAdapter<PersonalObject, PersonalViewHolder>(
                        PersonalObject.class,
                        R.layout.personal_object,
                        PersonalViewHolder.class,
                        mDatabasePersonal.child(user_id)) {
                    @Override
                    protected void populateViewHolder(final PersonalViewHolder viewHolder, final PersonalObject model, int position) {

                        final String personalObjectKey = getRef(position).getKey();
                        viewHolder.setName(model.getName());
//                        viewHolder.setImage(model.getImage(),getContext());
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Clicked on " + personalObjectKey, Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
//                                Toast.makeText(getActivity(), "Long Clicked on " + personalObjectKey, Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(v.getRootView().getContext(),R.style.MyDialogTheme)
                                        .setMessage("Are you sure you want to remove " + model.getName() + "?" )
                                        .setTitle("Remove Location")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mDatabasePersonal.child(user_id).child(personalObjectKey).removeValue();
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


                    }
                };

                mPersonalRecyclerView.setAdapter(adapter);



//
//
//                firebaseListAdapter = new FirebaseListAdapter<PersonalObject>(
//                        getActivity(),
//                        PersonalObject.class,
//                        android.R.layout.simple_list_item_1,
////                        mDatabaseUsers.child(user_id).child("personal")
//                        mDatabasePersonal.child(user_id)
//
//                ) {
//                    @Override
//                    protected void populateView(View v, PersonalObject model, int position) {
//
//                        TextView textView = (TextView) v.findViewById(android.R.id.text1);
//                        textView.setText(model.getName());
//
//                    }
//                };
//
//                mPersonalListView.setAdapter(firebaseListAdapter);


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
