package com.camerrow.camerrowproject;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Camerrow extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Newer version of Firebase
        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
