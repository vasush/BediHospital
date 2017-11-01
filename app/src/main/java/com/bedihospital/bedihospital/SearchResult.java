package com.bedihospital.bedihospital;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchResult extends AppCompatActivity {

    private DatabaseReference mRef;
    private FirebaseUser firebaseUser;
    TextView appointmentSearchResult;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        appointmentSearchResult = (TextView)findViewById(R.id.appointmentSearchResult);
        //mRef = new Firebase("https://bedi-hospital.firebaseio.com/users/G9OOGZfTt8N2Du2Lgeh1NcP9TGX2");
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser != null) {
            currentUserId = firebaseUser.getUid();


            DatabaseReference newRef = mRef.child(currentUserId);
            newRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    appointmentSearchResult.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
