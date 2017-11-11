package com.bedihospital.bedihospital;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bedihospital.bedihospital.Model.User;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
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

    String city, speciality;
    String findDoctorMessage;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    String userEmailId, userName, userContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting the data from findDoctor and bookAppointment activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            findDoctorMessage = bundle.getString("findDoctorMessage");
            if(findDoctorMessage != null && findDoctorMessage.equals("coming from find doctor")) {
                this.setTitle("Find a Doctor");
            }
            else {
                this.setTitle("Book an Appointment");
            }

            city = bundle.getString("city");
            speciality = bundle.getString("speciality");
        }

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {

            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("google.com")) {
                    //loading image of the user if he sign in from google account
                    userName = mAuth.getCurrentUser().getDisplayName();
                    userEmailId = mAuth.getCurrentUser().getEmail();
                }
                else {
                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users");//linking user to app
                    final String uId = mAuth.getCurrentUser().getUid();//current id

                    DatabaseReference newRef = mref.child(uId);//in child current id
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = String.valueOf(dataSnapshot.child("name").getValue());//getting value of name
                            String email = String.valueOf(dataSnapshot.child("email").getValue());//getting value of name
                            String contact = String.valueOf(dataSnapshot.child("contact").getValue());//getting value of name

                            userName = name;
                            userEmailId = email;
                            userContact = contact;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        //setting recycler view
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //converting string from previous acitivity to lower case to match the ones in firebase databse
        speciality_city = speciality + "_" + city;
        speciality_city = speciality_city.toLowerCase();

        searchResultQuantity = findViewById(R.id.searchresultQuantity);

        mRef = FirebaseDatabase.getInstance().getReference();

        value = new ArrayList<>();

        // this code is used to fetch data from database based on city and speciality
        //but is now used to return the number of data returned
        com.google.firebase.database.Query query = mRef.child("doctors")
                .orderByChild("speciality_city").equalTo(speciality_city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if data does not exists
                if (!dataSnapshot.exists()) {
                    //do something
                    //appointmentSearchResult.setText("No record found.");
                }
                //iterate through whole object of database with spaeicif details
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {

                        Log.d("user name: ", ds.getValue(User.class).getName());
                        //getting name of the doctor

                        value.add(ds.getValue(User.class).getName());


                    }
                    Log.d("user name: ", "ds has no childern");
                }
                //Toast.makeText(SearchResult.this, s1 + " " + s2, Toast.LENGTH_SHORT).show();
                Log.d("List value size: ", Integer.toString(value.size()));

                doSomething(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //progress dialog showing
        progressDialog = ProgressDialog.show(this, "Searching", "Please wait", true);
        //used to retrieve list of doctor filtered by given details
        //adapter iterate through whole obejct in firebase and return what is asked for
        FirebaseRecyclerAdapter<User, SearchResultViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, SearchResultViewHolder>(
                        User.class, R.layout.search_result_card_view_content, SearchResultViewHolder.class,
                        mRef.child("doctors")
                                .orderByChild("speciality_city").equalTo(speciality_city)
                ) {
                    @Override
                    protected void populateViewHolder(final SearchResultViewHolder viewHolder, final User model, int position) {

                        //dismising progress dialog
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        viewHolder.searchCardViewDrName.setText(model.getName());//settting doctor name
                        viewHolder.searchCardViewDrSpeciality.setText(model.getSpeciality());//setting doctor speciality
                        viewHolder.searchCardViewDrFare.setText(model.getFare());//setting doctor's fare

                        if (findDoctorMessage != null && findDoctorMessage.equals("coming from find doctor")) {
                            //not able to click the recycler view
                        } else {

                            //recycler view on click event
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //starting doctor details intent to set date and time
                                    Intent intent = new Intent(SearchResult.this, DoctorDetails.class);
                                    intent.putExtra("doctorName", model.getName());//sending doc name
                                    intent.putExtra("doctorDetail", model.getDetail());//sending doc detail from firebase
                                    intent.putExtra("doctorSpeciality", model.getSpeciality());//sendgin doc speciality
                                    intent.putExtra("doctorCity", model.getCity());//sending doc city

                                    intent.putExtra("userEmailId", userEmailId); //sending user email
                                    intent.putExtra("userName", userName);//sending user name
                                    intent.putExtra("userContact", userContact);//sending user contact
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                };
        searchResultRecyclerView.setAdapter(firebaseRecyclerAdapter);//setting asapter
        firebaseRecyclerAdapter.notifyDataSetChanged();//recycler view updates when data inside gets changed

    }

    //view holder class
    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView searchCardViewDrName, searchCardViewDrSpeciality, searchCardViewDrFare;

        public SearchResultViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            searchCardViewDrName = itemView.findViewById(R.id.searchCardViewDrName);
            searchCardViewDrSpeciality = itemView.findViewById(R.id.searchCardViewDrSpeciality);
            searchCardViewDrFare = itemView.findViewById(R.id.searchCardViewDrFare);

        }
    }

    //this method counts the no of result fetched and accordingly display the result text
    private void doSomething(List<String> value) {

        //dismising progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (value.size() == 1) {
            searchResultQuantity.setText(value.size() + " Record found");
            value.clear();
        } else if (value.size() > 1) {
            searchResultQuantity.setText(value.size() + " Records found");
            value.clear();
        } else {
            searchResultQuantity.setText("No record found");

        }
    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
