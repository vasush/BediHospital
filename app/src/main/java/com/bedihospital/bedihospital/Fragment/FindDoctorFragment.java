package com.bedihospital.bedihospital.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bedihospital.bedihospital.R;
import com.bedihospital.bedihospital.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasu on 27-Oct-17.
 */

public class FindDoctorFragment extends Fragment {

    //array adapters
    ArrayAdapter<String> findDoctorCityArrayAdapter, findDoctorSpecialityArrayAdapter;
    //spinners
    Spinner findDoctorCitySelectorSpinner, findDoctorSpecialitySelectorSpinner;

    //search button
    Button findDoctorSearch;

    //string to store city and spec
    String findDoctorCityName, findDoctorSpecialityName;

    //for loading snack bar so that content moves up
    CoordinatorLayout findDoctorCoordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // layout for finding a doctor
        View rootView = inflater.inflate(R.layout.find_doctor, container, false);

        findDoctorCitySelectorSpinner = rootView.findViewById(R.id.findDoctorCitySelector);
        findDoctorSpecialitySelectorSpinner = rootView.findViewById(R.id.findDoctorSpecialitySelector);

        findDoctorSearch = rootView.findViewById(R.id.findDoctorSearch);

        findDoctorCoordinatorLayout = rootView.findViewById(R.id.findDoctorCoordinatorLayout);

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
        findDoctorCityArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, cityList);
        findDoctorCitySelectorSpinner.setAdapter(findDoctorCityArrayAdapter);//setting adtapter for city

        //array adapter for speciality
        findDoctorSpecialityArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, speacilityList);
        findDoctorSpecialitySelectorSpinner.setAdapter(findDoctorSpecialityArrayAdapter);//setting adapter for spec

        //getting city name
        findDoctorCitySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                findDoctorCityName = adapterView.getItemAtPosition(position).toString();//city name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //getting city name
        findDoctorSpecialitySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                findDoctorSpecialityName = adapterView.getItemAtPosition(position).toString();//city name
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findDoctorSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable()) {

                    //starting search activity and sending city and speciality
                    Intent intent = new Intent(getActivity(), SearchResult.class);
                    intent.putExtra("findDoctorMessage","coming from find doctor");
                    intent.putExtra("city", findDoctorCityName);
                    intent.putExtra("speciality", findDoctorSpecialityName);
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
