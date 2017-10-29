package com.bedihospital.bedihospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bedihospital.bedihospital.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "AddToDatabase";

    //views
    AutoCompleteTextView mEmailField;
    EditText mPasswordField, mNamefield, mContactField;
    Button registerButton;

    // firebase authentication
    private FirebaseAuth mAuth;

    //[START declare_database_ref]
    private DatabaseReference mDatabaseRefrence;

    //fetching details
    String email, loginPassword, registerPassword, name, contact;

    //validating
    boolean emailValid, passwordValid, nameValid, contactValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fetching ids
        mEmailField = (AutoCompleteTextView) findViewById(R.id.registerEmail);
        mPasswordField = (EditText) findViewById(R.id.registerPassword);
        mNamefield = (EditText) findViewById(R.id.registerName);
        mContactField = (EditText) findViewById(R.id.registerContact);
        registerButton = (Button) findViewById(R.id.registerButton);


        mAuth = FirebaseAuth.getInstance();
        // [START initialize_database_ref]
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference("Users"); // Users is the table where details are stored

        //getting email and password from login activity
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        loginPassword = bundle.getString("password");

        mEmailField.setText(email);//setting email field from login activity

        // register button listner
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                registerPassword = mPasswordField.getText().toString();
                name = mNamefield.getText().toString();
                contact = mContactField.getText().toString();

                //Toast.makeText(RegisterActivity.this, email, Toast.LENGTH_SHORT).show();

                //validating details
                if (registerPassword.matches(loginPassword)) {
                    passwordValid = true;
                } else {
                    mPasswordField.setError("Password not matched");
                }
                if (name.length() > 2) {
                    nameValid = true;
                } else {
                    mNamefield.setError("Name is required");
                }
                if (android.util.Patterns.PHONE.matcher(contact).matches()) {
                    contactValid = true;
                } else {
                    mContactField.setError("Invalid contact");
                }

                if (passwordValid && nameValid && contactValid) {

                    startSignIn();//firebase auth
                    addToDatabase();//data store in firebase


                } else {
                    //Toast.makeText(RegisterActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    // adding details to firebase database
    private void addToDatabase() {
        FirebaseUser user = mAuth.getCurrentUser();
        final String userID = user.getUid();
        //Log.d(TAG ,userID);
        final User userDetails = new User(name, email, contact);
        mDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userDetails.getName()).exists()) {
                    Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabaseRefrence.child(userID).setValue(userDetails);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //internet connection check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //adding user to firebase auth
    private void startSignIn() {

        final ProgressDialog progressDialog1 = ProgressDialog.show(this,"Registering","Please wait",true);

        mAuth.createUserWithEmailAndPassword(email, registerPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog1.dismiss();
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

//                  starting main activity
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finishAffinity();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    if(isNetworkAvailable()) {

                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "User already exists",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{

                        Toast.makeText(RegisterActivity.this, "Check internet connection.", Toast.LENGTH_SHORT).show();
                    }
                    //updateUI(null);
                }

                // progressDialog.cancel();

            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

