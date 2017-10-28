package com.bedihospital.bedihospital;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Vasu on 28-Oct-17.
 */

public class BediHospital extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
