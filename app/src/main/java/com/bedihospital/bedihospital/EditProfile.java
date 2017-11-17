package com.bedihospital.bedihospital;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bedihospital.bedihospital.Activity.MainActivity;
import com.bedihospital.bedihospital.Activity.StartActivity;
import com.bedihospital.bedihospital.Fragment.ProfileFragment;
import com.bedihospital.bedihospital.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    CircleImageView editProfileImage;
    AutoCompleteTextView editProfileUserName, editProfileEmail;
    EditText editProfileContact;
    Button editProfileSaveButton;
    RelativeLayout editTextRelativeLayout;
    ProgressBar editProfileProgessBar;

    FirebaseAuth mAuth;
    DatabaseReference databaseRefrence;

    String updatedName, updatedEmail, updatedContact, registeredName = "", registeredEmail = "", registeredContact = "";

    boolean nameValid, contactValid, emailValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fetching ids
        editProfileImage = findViewById(R.id.editProfileImage);
        editProfileUserName = findViewById(R.id.editProfileUserName);
        editProfileEmail = findViewById(R.id.editProfileEmail);
        editProfileContact = findViewById(R.id.editProfileContact);
        editProfileSaveButton = findViewById(R.id.editProfileSaveButton);
        editTextRelativeLayout = findViewById(R.id.editTextRelativeLayout);
        editProfileProgessBar = findViewById(R.id.editProfileProgessBar);

        databaseRefrence = FirebaseDatabase.getInstance().getReference(); //database refrence


        Log.d( "email: ",  registeredEmail);
        Log.d( "contact: ",editProfileContact.getText().toString() );

        //visibility of save button
        if (editProfileUserName.getText().toString().equals(registeredName)
                && editProfileEmail.getText().toString().equals(registeredEmail)
                && editProfileContact.getText().toString().equals(registeredContact)) {

            editProfileSaveButton.setVisibility(View.GONE);
        } else {
            editProfileSaveButton.setVisibility(View.VISIBLE);
        }


        //save button on click
        editProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatedName = editProfileUserName.getText().toString();//getting name
                updatedEmail = editProfileEmail.getText().toString();//getting email
                updatedContact = editProfileContact.getText().toString();//getting contact

                //validating details
                if (updatedName.length() > 2) {
                    nameValid = true;
                } else {
                    editProfileUserName.setError("Name is required");
                }
                if (Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
                    emailValid = true;
                } else {
                    editProfileEmail.setError("Email is incorrect");
                }
                if (android.util.Patterns.PHONE.matcher(updatedContact).matches() && updatedContact.length() == 10) {
                    contactValid = true;
                } else {
                    editProfileContact.setError("Invalid contact");
                }
                if (emailValid && nameValid && contactValid  && isNetworkAvailable()) {

                    if (editProfileUserName.getText().toString().equals(registeredName)
                            && editProfileEmail.getText().toString().equals(registeredEmail)
                            && editProfileContact.getText().toString().equals(registeredContact)) {

                    }
                    else{

                        editProfileProgessBar.setVisibility(View.VISIBLE);
                        editProfileUserName.setVisibility(View.GONE);
                        editProfileEmail.setVisibility(View.GONE);
                        editProfileContact.setVisibility(View.GONE);
                        editProfileImage.setVisibility(View.GONE);
                        editProfileSaveButton.setVisibility(View.GONE);

                        updateDatabase();//updating database

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 500ms
                                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                editProfileProgessBar.setVisibility(View.GONE);
                            }
                        }, 2000);

                    }
                }
                else {
                    Snackbar snackbar = Snackbar.make(editTextRelativeLayout, "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    //updating database
    private void updateDatabase() {

        if (mAuth.getCurrentUser() != null) {

            String uId = mAuth.getCurrentUser().getUid();
            User user = new User(updatedName, updatedEmail, updatedContact);
            databaseRefrence.child("users").child(uId).setValue(user);
        }
    }

    //To verify network availability:
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //visibility of save button
        if (editProfileUserName.getText().toString().equals(registeredName)
                && editProfileEmail.getText().toString().equals(registeredEmail)
                && editProfileContact.getText().toString().equals(registeredContact)) {

            editProfileSaveButton.setVisibility(View.GONE);
        } else {
            editProfileSaveButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //visibility of save button
        if (editProfileUserName.getText().toString().equals(registeredName)
                && editProfileEmail.getText().toString().equals(registeredEmail)
                && editProfileContact.getText().toString().equals(registeredContact)) {

            editProfileSaveButton.setVisibility(View.GONE);
        } else {
            editProfileSaveButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            for (UserInfo user : mAuth.getCurrentUser().getProviderData()) {
                //user sign in from google api
                if (user.getProviderId().equals("google.com")) {

                    //loading image of the user if he sign in from google account
                    Uri imageUri = mAuth.getCurrentUser().getPhotoUrl();//getting image uri from firebase
                    Glide.with(this).load(imageUri.toString()).thumbnail(0.5f).into(editProfileImage);//using glide to bring image

                    registeredName = mAuth.getCurrentUser().getDisplayName();//getting value of name
                    editProfileUserName.setText(registeredName);//setting name

                    registeredContact = mAuth.getCurrentUser().getEmail();
                    editProfileEmail.setText(registeredEmail);
                    // break;
                }
                //user sign in via email and password
                else {
                    String uId = mAuth.getCurrentUser().getUid();
                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users").child(uId);//linking user to app

                    mref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            registeredName = String.valueOf(dataSnapshot.child("name").getValue());//getting value of name
                            editProfileUserName.setText(registeredName);//setting name

                            registeredContact = String.valueOf(dataSnapshot.child("contact").getValue());//getting contat cf user
                            editProfileContact.setText(registeredContact);//setting contact

                            registeredEmail = String.valueOf(dataSnapshot.child("email").getValue());//getting email
                            editProfileEmail.setText(registeredEmail);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
