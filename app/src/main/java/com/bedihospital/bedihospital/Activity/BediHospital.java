package com.bedihospital.bedihospital.Activity;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Vasu on 28-Oct-17.
 */
// connection with firebase
public class BediHospital extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
