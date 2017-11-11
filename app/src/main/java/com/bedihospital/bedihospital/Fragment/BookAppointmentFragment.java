package com.bedihospital.bedihospital.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bedihospital.bedihospital.Activity.MainActivity;
import com.bedihospital.bedihospital.R;
import com.bedihospital.bedihospital.SearchResult;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Vasu on 27-Oct-17.
 */

public class BookAppointmentFragment extends Fragment {

    ArrayAdapter<String> cityArrayAdapter, specialityArrayAdapter;

    Spinner citySelectorSpinner, specialitySelectorSpinner;

    Button appointmentSearch;

    LinearLayout appointment_linaer_layout;

    String cityName, specialityName;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.book_appointment, container, false);

        citySelectorSpinner = (Spinner) rootView.findViewById(R.id.appointmentCitySelector);
        specialitySelectorSpinner = (Spinner) rootView.findViewById(R.id.appointmentSpecialitySelector);

        getActivity().setTitle("Book Appointment");

        // city list categories
        List<String> cityList = new ArrayList<String>();
        cityList.add("Panchkula");
        cityList.add("Haryana");
        cityList.add("Chandigarh");

        // speciality list categories
        List<String> speacilityList = new ArrayList<String>();
        speacilityList.add("Cardiologist");
        speacilityList.add("Heart Surgen");
        speacilityList.add("Orthopedist");
        speacilityList.add("Neurologist");

        // array adapter for city
        cityArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, cityList);
        cityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySelectorSpinner.setAdapter(cityArrayAdapter);

        //array adapter for speciality
        specialityArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, speacilityList);
        specialityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialitySelectorSpinner.setAdapter(specialityArrayAdapter);

        //fetching city spinner
        citySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                cityName = adapterView.getItemAtPosition(pos).toString();//city name
                //Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        specialitySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //speciality
                specialityName = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        appointmentSearch = (Button)rootView.findViewById(R.id.appointmentSearch);
        appointment_linaer_layout = (LinearLayout)rootView.findViewById(R.id.appointment_linear_layout);

        appointmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if interbet connection is on
                if(isNetworkAvailable()) {

                    //starting search activity and sending city and speciality
                    Intent intent = new Intent(getActivity(), SearchResult.class);
                    intent.putExtra("city", cityName);
                    intent.putExtra("speciality", specialityName);
                    startActivity(intent);
                }
                else {
                    //new way to toast
                    Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    //internet connection check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
