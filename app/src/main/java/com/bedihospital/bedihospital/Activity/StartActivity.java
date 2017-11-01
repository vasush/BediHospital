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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bedihospital.bedihospital.Model.User;
import com.bedihospital.bedihospital.R;
import com.bedihospital.bedihospital.RecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;


public class StartActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    //creating refrences
    RecyclerView recyclerView;
    TextView skipExplore, forgotPassword;

    AutoCompleteTextView loginEmail;
    EditText loginPassword;
    Button login_registerButton;

    String email, password;//getting text from fields
    boolean emailValid, passwordValid;//validating details

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    LinearLayout linearLayout;

    private GoogleApiClient mGoogleApiClient;
    GoogleSignInButton googleSignIn;
    int RC_SIGN_IN = 1;
    String TAG = "Google sign in";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();//fireauth reference
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
        setContentView(R.layout.activity_start);

        linearLayout = (LinearLayout) findViewById(R.id.loginLinearLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new RecyclerAdapter(this));

        loginEmail = (AutoCompleteTextView) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        login_registerButton = (Button) findViewById(R.id.login_registerButton);


        databaseReference = FirebaseDatabase.getInstance().getReference();//database refrence

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignIn = (GoogleSignInButton) findViewById(R.id.googleSignIn);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        //login - register button click handler
        login_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();

                //validating email and password
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailValid = true;
                } else {
                    loginEmail.setError("Incorrect email");
                }
                if (password.length() >= 6) {
                    passwordValid = true;
                } else {
                    loginPassword.setError("Incorrect password");
                }

                if (emailValid && passwordValid) {

                    //signing with email and password
                    loginButtonClick();
                } else {

                }

            }
        });

        //skip & explore button handler
        skipExplore = (TextView) findViewById(R.id.skipExplore);
        skipExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        //forgot password handler
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //enter email first
                    if (password.length() > 0 || password.length() == 0)
                        loginEmail.setError("Please provide correct email first");
                } else {
                    //method to send email verification first
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(linearLayout, "Open your email to reset password", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }
                            });
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //dismising progress dialog

            } else {
                if (isNetworkAvailable()) {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.getException());
//                    Toast.makeText(StartActivity.this, "Authentication failed.",
//                            Toast.LENGTH_SHORT).show();
                } else {
                    //new way to toast
                    Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            addDetailsToDataBase();
                            //starting main activity on success
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                        } else {
                            if(isNetworkAvailable()) {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(StartActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                //new way to toast
                                Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }

                        // ...
                    }
                });
    }

    //adding value to data base for google account
    private void addDetailsToDataBase() {

        Log.d("addToDatabase: ", "in add to data base...");

        if(mAuth.getCurrentUser() != null) {

            String gName = mAuth.getCurrentUser().getDisplayName();// name of google account
            String gEmail = mAuth.getCurrentUser().getEmail();// email of google account
            String gId = mAuth.getCurrentUser().getUid();// current user id

            User user = new User(gName, gEmail);
            databaseReference.child("users").child(gId).setValue(user);//setting value of name and email in uid under users

        }

    }

    //internet connection check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //loging in to app
    private void loginButtonClick() {

        //progress dialog showing
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Login", "Please wait", true);

        //signing with email and password
        (mAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //dismising progress dialog
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //is success move to main activity
                if (task.isSuccessful()) {

                    // starting activity on success
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();

                    Log.d("login: ", "Success");

                } else {
                    //moving to register activity
                    if (isNetworkAvailable()) {

                        // checking password is incorrect by checking email is present or not
                        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                if (task.getResult().getProviders().isEmpty()) {

                                    Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                }
                                //password is incorrect
                                else {
                                    Snackbar snackbar = Snackbar.make(linearLayout, "Password is incorrect", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            }
                        });
                    }

                    //network issues
                    else {
                        //new way to toast
                        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
