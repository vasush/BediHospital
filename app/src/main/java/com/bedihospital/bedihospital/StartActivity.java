package com.bedihospital.bedihospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class StartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView  skipExplore;

    static int flag = 0;

    AutoCompleteTextView loginEmail;
    EditText loginPassword;
    Button login_registerButton;

    String email, password;
    boolean emailValid, passwordValid;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new RecyclerAdapter(this));

        loginEmail = (AutoCompleteTextView)findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        login_registerButton = (Button) findViewById(R.id.login_registerButton);

        mAuth = FirebaseAuth.getInstance();//fireauth reference

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

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
                    Toast.makeText(StartActivity.this, "need to regis", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();

                //is success move to main activity
                if(task.isSuccessful()) {

                    checkUserExist();
                    Log.d( "login: ","Success");

                }
                else {
                    //moving to register activity
                    if(isNetworkAvailable()) {
                        //Toast.makeText(StartActivity.this, "Not a member", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        startActivity(intent);
                    }
                    //network issues
                    else {
                        Toast.makeText(StartActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
