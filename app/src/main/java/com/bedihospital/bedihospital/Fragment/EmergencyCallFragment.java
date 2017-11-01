package com.bedihospital.bedihospital.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bedihospital.bedihospital.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasu on 27-Oct-17.
 */

public class EmergencyCallFragment extends Fragment {
    LinearLayout ambulance, helpLine, bloodBank;
    String number = "09111891118";
    String number2 = "01722668111";

    int REQUESTCODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.emergency_call, container, false);

       ambulance = (LinearLayout)rootView.findViewById(R.id.ambulance);
       bloodBank = (LinearLayout)rootView.findViewById(R.id.bloodBank);
       helpLine = (LinearLayout)rootView.findViewById(R.id.helpLine);

        call(ambulance, number);
        call(helpLine, number2);
        call(bloodBank, number);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    //this code is for giving runtime permissions
    private void call(LinearLayout linearLayout, final String number) {

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {



                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {
                        startActivity(intent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
               // startActivity(intent);
            }
        });

    }


    //runtime permissions handler
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
