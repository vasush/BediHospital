package com.bedihospital.bedihospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bedihospital.bedihospital.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView  skipExplore, forgotPassword;

    AutoCompleteTextView loginEmail;
    EditText loginPassword;
    Button login_registerButton;

    String email, password;
    boolean emailValid, passwordValid;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    LinearLayout linearLayout;

    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();//fireauth reference
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
        setContentView(R.layout.activity_start);

        linearLayout = (LinearLayout)findViewById(R.id.loginLinearLayout);

        list = new ArrayList<>();
        //list.add("vasu@gmail.com");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new RecyclerAdapter(this));

        loginEmail = (AutoCompleteTextView)findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        login_registerButton = (Button) findViewById(R.id.login_registerButton);


        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        //setting adapter for autocomplete email
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StartActivity.this, R.layout.support_simple_spinner_dropdown_item, list);
        loginEmail.setAdapter(arrayAdapter);
        loginEmail.setThreshold(1);

        login_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();


                //validating email and password
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailValid =true;
                }
                else {
                    loginEmail.setError("Incorrect email");
                }
                if(password.length() >= 6) {
                    passwordValid = true;
                }
                else {
                    loginPassword.setError("Incorrect password");
                }

                if(emailValid && passwordValid) {

                    //signing with email and password
                    loginButtonClick();
                }
                else{

                }

            }
        });


        skipExplore = (TextView)findViewById(R.id.skipExplore);
        skipExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
            }
        });

        forgotPassword = (TextView)findViewById(R.id.forgot_password);

        //forgot password handler
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();

                if( !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                    //enter email first
                    if(password.length() > 0 || password.length() == 0)
                        loginEmail.setError("Please provide correct email first");
                }
                else {
                    //method to send email verification first
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(linearLayout, "Open your email to reset password", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }
                            });
                }

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

    private void checkUserExist(){
        final String user_id = mAuth.getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)) {
                    startActivity(new Intent(StartActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    //Toast.makeText(StartActivity.this, "need to regis", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //loging in to app
    private void loginButtonClick() {

        //progress dialog showing
        final ProgressDialog progressDialog = ProgressDialog.show(this,"Login","Please wait",true);

        //signing with email and password
        (mAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //is success move to main activity
                if(task.isSuccessful()) {

                    checkUserExist();
                    Log.d( "login: ","Success");

                }
                else {
                    //moving to register activity
                    if(isNetworkAvailable()) {

                        // checking password is incorrect by checking email is present or not
                        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                if(task.getResult().getProviders().isEmpty()) {

                                    Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("password",password);
                                    startActivity(intent);
                                }
                                else{
                                    Snackbar snackbar = Snackbar.make(linearLayout, "Password is incorrect", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            }
                        });


                        //progressDialog.dismiss();

                    }
                    //network issues

                    else {

                        //new way to toast
                        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

    }
}
