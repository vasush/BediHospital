package com.bedihospital.bedihospital.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bedihospital.bedihospital.Model.User;
import com.bedihospital.bedihospital.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    //views
    AutoCompleteTextView mEmailField;
    EditText mPasswordField, mNamefield, mContactField;
    Button registerButton;

    // firebase authentication
    private FirebaseAuth mAuth;

    LinearLayout registerLinearLayout;

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
        registerLinearLayout = (LinearLayout) findViewById(R.id.registerLineraLayout);


        mAuth = FirebaseAuth.getInstance();
        // [START initialize_database_ref]
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference(); //database refrence

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

                    startSignIn();//starting firebase auth

                } else {

                }
            }

        });

    }

    // adding details to firebase database
    private void addToDatabase() {

        Log.d("addToDatabase: ", "in add to data base...");
        if (mAuth.getCurrentUser() != null) {

            User userObject = new User(name, email, contact);//creating object of User class to send multiple data
            final String userId = mAuth.getCurrentUser().getUid();//getting current user's id
            mDatabaseRefrence.child("users").child(userId).setValue(userObject);//writing to firebase database

        }
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

        //progress dialog
        final ProgressDialog progressDialog1 = ProgressDialog.show(this, "Registering", "Please wait", true);

        //method to create user
        mAuth.createUserWithEmailAndPassword(email, registerPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //dismising progress dialog
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.dismiss();
                }
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success");

                    addToDatabase();//data store in firebase

//                  starting main activity
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finishAffinity();

                } else {
                    // If sign up fails, display a message to the user.
                    if (isNetworkAvailable()) {

                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Something went wrong",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        //new way to toast
                        Snackbar snackbar = Snackbar.make(registerLinearLayout, "No internet connection", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                }
            }
        });
    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

