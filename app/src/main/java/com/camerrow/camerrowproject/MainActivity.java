package com.camerrow.camerrowproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import com.camerrow.camerrowproject.services.LocationMonitoringService;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements PersonalDialog.PersonalDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private boolean mAlreadyStartedService = false;
    private TextView mMsgView;

    //Toolbar
    private Toolbar mToolbar;


    //Fragments
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabasePersonal;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabseFriends;

    private String user_id;

    private RecyclerView mFriendsSearchRecyclerView;
    private ArrayList<CamerrowUser> camerrowUserArrayList;
    private SearchAdapter searchAdapter;
    private LinearLayout mSearchLinearLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        //remove border between action bar and app bar (return it if using action bar
//        getSupportActionBar().setElevation(0);


        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Camerrow");
//        mToolbar.inflateMenu(R.menu.main_menu);

//        //search items divider - Not Working
//        DividerItemDecoration myDivider = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
//        myDivider.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.costume_divider));

        mFriendsSearchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        mFriendsSearchRecyclerView.setHasFixedSize(true);
        mFriendsSearchRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mFriendsSearchRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,LinearLayoutManager.VERTICAL));
        camerrowUserArrayList = new ArrayList<>();

        mSearchLinearLayout = (LinearLayout) findViewById(R.id.searchLinearLayout);
        mSearchLinearLayout.bringToFront();






        mMsgView = (TextView) findViewById(R.id.msgView);

        //Fragments
        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        //set second fragment to launch first
        mViewPager.setCurrentItem(1);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        //Firebase init
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabseFriends = FirebaseDatabase.getInstance().getReference().child("Friends");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mDatabasePersonal = FirebaseDatabase.getInstance().getReference().child("Personal");
        mDatabasePersonal.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };


        if(mAuth.getCurrentUser()!=null)
            user_id = mAuth.getCurrentUser().getUid();


        startService(new Intent(MainActivity.this, LocationMonitoringService.class));


        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("Recieved:", "Location");
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
                            Log.d("Location:", latitude + "," + longitude);

                            storeInDatabase(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        } else
                            Log.d("Location:", "is Null");
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

//        sendDataToFragment();




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


//    private void sendDataToFragment() {
//        Bundle bundle = new Bundle();
//        bundle.putString("edttext", "From Activity");
//        // set Fragmentclass Arguments
//        Fragmentclass fragobj = new Fragmentclass();
//        fragobj.setArguments(bundle);
//    }

    private void storeInDatabase(Double latitude, Double longitude) {

        mDatabaseUsers.child(user_id).child("location").child("longitude").setValue(longitude);
        mDatabaseUsers.child(user_id).child("location").child("latitude").setValue(latitude);
    }



    // Options Menu Creator
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        //** For MaterialSearchView
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setMenuItem(item);

        //**
//        **For SearchView (also change in menu.xml**
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!s.isEmpty())
                    setAdapter(s);
                else{
                    camerrowUserArrayList.clear();
                    mFriendsSearchRecyclerView.removeAllViews();
                }

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    //  Menu on click functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout)
            logout();

        if (item.getItemId() == R.id.action_myProfile)
            startMyProfileActivity();
        return super.onOptionsItemSelected(item);
    }

    private void startMyProfileActivity() {
        Intent myProfileIntent = new Intent(MainActivity.this, MyProfileActivity.class );
        myProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myProfileIntent);

    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(user_id)){

                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void logout() {
//        //Stop location sharing service to app server.........
//
        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
//        //Ends................................................
        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check user exist
        //checkUserExist();

        //Register AuthStateListener
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServices();
    }


    /**
     * Step 1: Check Google Play services
     */
    private void checkGooglePlayServices() {


        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            checkInternetConnection(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean checkInternetConnection(DialogInterface dialog) {


        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            locationMonitorService();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (checkInternetConnection(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                locationMonitorService();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void locationMonitorService() {


        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService && mMsgView != null) {

            mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }



    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                locationMonitorService();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }


    }

    @Override
    protected void onDestroy() {

        //Stop location sharing service to app server.........

        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................


        super.onDestroy();
    }


    @Override
    public void sendBack(PersonalObject personalObject) {
        Map mPersonalObjectMap = new HashMap();
        mPersonalObjectMap.put("name",personalObject.getName());
        mPersonalObjectMap.put("latitude",personalObject.getLatitude());
        mPersonalObjectMap.put("longitude",personalObject.getLongitude());
        mPersonalObjectMap.put("image",personalObject.getImage());

//        mDatabaseUsers.child(user_id).child("personal").push().setValue(mPersonalObjectMap);
        mDatabasePersonal.child(user_id).push().setValue(mPersonalObjectMap);


    }

    //search data
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

                searchAdapter = new SearchAdapter(MainActivity.this, camerrowUserArrayList, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(mViewPager.getCurrentItem()!=1)
                            mViewPager.setCurrentItem(1);
                        if(!isLongClick){
                            if(!camerrowUserArrayList.get(position).getDatabaseKey().equals(user_id))
                                addFriend(camerrowUserArrayList.get(position));
                        } else {
                            removeFriend(camerrowUserArrayList.get(position));
                        }
                        camerrowUserArrayList.clear();
                        mFriendsSearchRecyclerView.removeAllViews();
                        hideSoftKeyboard(MainActivity.this);



                    }
                });

                mFriendsSearchRecyclerView.setAdapter(searchAdapter);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
