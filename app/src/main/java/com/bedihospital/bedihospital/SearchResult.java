package com.bedihospital.bedihospital;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bedihospital.bedihospital.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends AppCompatActivity {

    TextView searchResultQuantity;
    String speciality_city;
    static List<String> value;
    DatabaseReference mRef;
    RecyclerView searchResultRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String city = bundle.getString("city");
        String speciality = bundle.getString("speciality");

        //setting recycler view
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        speciality_city = speciality + "_" + city;
        speciality_city = speciality_city.toLowerCase();

        //listView = findViewById(R.id.listView);

        //Toast.makeText(this, speciality_city, Toast.LENGTH_SHORT).show();

        searchResultQuantity = findViewById(R.id.searchresultQuantity);
        //mRef = new Firebase("https://bedi-hospital.firebaseio.com/users/G9OOGZfTt8N2Du2Lgeh1NcP9TGX2");
        mRef = FirebaseDatabase.getInstance().getReference();

        value = new ArrayList<>();

       // this code is used to fetch data from database based on city and speciality
        com.google.firebase.database.Query query = mRef.child("doctors")
                    .orderByChild("speciality_city").equalTo(speciality_city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if data does not exists
                if(!dataSnapshot.exists()) {
                    //do something
                   //appointmentSearchResult.setText("No record found.");
                }
                //iterate through whole object of database with spaeicif details
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if( ds.exists()) {

                        Log.d( "user name: ",ds.getValue(User.class).getName());
                        //getting name of the doctor

                        value.add(ds.getValue(User.class).getName());


                    }
                    Log.d( "user name: ","ds has no childern");
                }
                //Toast.makeText(SearchResult.this, s1 + " " + s2, Toast.LENGTH_SHORT).show();
                Log.d( "List value size: ",Integer.toString(value.size()));

                    doSomething( value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //used to retrieve list of doctor filtered by given details
        //adapter iterate through whole obejct in firebase and return what is asked for
        FirebaseRecyclerAdapter<User, SearchResultViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, SearchResultViewHolder>(
                        User.class,R.layout.search_result_card_view_content,SearchResultViewHolder.class,
                        mRef.child("doctors")
                                .orderByChild("speciality_city").equalTo(speciality_city)
                ) {
                    @Override
                    protected void populateViewHolder(SearchResultViewHolder viewHolder, User model, int position) {

                        viewHolder.searchCardViewDrName.setText(model.getName());//settting doctor name
                        viewHolder.searchCardViewDrSpeciality.setText(model.getSpeciality());//setting doctor speciality
                        viewHolder.searchCardViewDrFare.setText(model.getFare());//setting doctor's fare

                        //Log.d("populateViewHolder: ",Integer.toString(list.size()));

                    }
                };
        searchResultRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

       // Log.d("item count: ",Integer.toString(searchResultRecyclerView.getAdapter().getItemCount()));
    }

    //view holder class
    public static class SearchResultViewHolder extends RecyclerView.ViewHolder{

        TextView searchCardViewDrName, searchCardViewDrSpeciality, searchCardViewDrFare;
        public SearchResultViewHolder(View itemView) {
            super(itemView);

            searchCardViewDrName = itemView.findViewById(R.id.searchCardViewDrName);
            searchCardViewDrSpeciality = itemView.findViewById(R.id.searchCardViewDrSpeciality);
            searchCardViewDrFare = itemView.findViewById(R.id.searchCardViewDrFare);

        }
    }

    //this method counts the no of result fetched and accordingly display the result text
    private void doSomething(List<String> value) {

        if( value.size() == 1 ) {
            searchResultQuantity.setText(value.size()+ " Record found");
        }
        else if(value.size() > 1 ) {
            searchResultQuantity.setText(value.size()+ " Records found");
        }
        else {
            searchResultQuantity.setText("No record found");

        }
    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
