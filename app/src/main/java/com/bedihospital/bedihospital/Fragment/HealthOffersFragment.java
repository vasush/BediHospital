package com.bedihospital.bedihospital.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bedihospital.bedihospital.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasu on 27-Oct-17.
 */

public class HealthOffersFragment extends Fragment {
    ArrayAdapter<String> cityArrayAdapter, specialityArrayAdapter;
    Spinner citySelectorSpinner, specialitySelectorSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.health_offers, container, false);

        getActivity().setTitle("Book Appointment");

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



}
