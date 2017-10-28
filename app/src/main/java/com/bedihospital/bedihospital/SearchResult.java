package com.bedihospital.bedihospital;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class SearchResult extends AppCompatActivity {

    private Firebase mRef;
    TextView appointmentSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        appointmentSearchResult = (TextView)findViewById(R.id.appointmentSearchResult);
        mRef = new Firebase("https://bedi-hospital.firebaseio.com/Name");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue().toString();
                //Log.d("data Fetched ",value);
                Log.d("data fetched: ","1");
                appointmentSearchResult.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("data fetched: ","error");
            }
        });

    }
}
