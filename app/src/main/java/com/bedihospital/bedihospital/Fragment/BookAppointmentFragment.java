package com.bedihospital.bedihospital.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.book_appointment, container, false);

        citySelectorSpinner = (Spinner) rootView.findViewById(R.id.citySelector);
        specialitySelectorSpinner = (Spinner) rootView.findViewById(R.id.specialitySelector);

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
        citySelectorSpinner.setAdapter(cityArrayAdapter);

        //array adapter for speciality
        specialityArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, speacilityList);
        specialityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        specialitySelectorSpinner.setAdapter(specialityArrayAdapter);

        appointmentSearch = (Button)rootView.findViewById(R.id.appointmentSearch);



        appointmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SearchResult.class));
            }
        });

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



}
