package com.bedihospital.bedihospital;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasu on 27-Oct-17.
 */

public class FindDoctor extends Fragment {
    ArrayAdapter<String> cityArrayAdapter, specialityArrayAdapter;
    Spinner citySelectorSpinner, specialitySelectorSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // layout for finding a doctor
        View rootView = inflater.inflate(R.layout.find_doctor, container, false);

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

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
