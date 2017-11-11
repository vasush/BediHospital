package com.bedihospital.bedihospital.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bedihospital.bedihospital.DoctorDetails;
import com.bedihospital.bedihospital.Fragment.BookAppointmentFragment;
import com.bedihospital.bedihospital.Fragment.EmergencyCallFragment;
import com.bedihospital.bedihospital.Fragment.FindDoctorFragment;
import com.bedihospital.bedihospital.Fragment.HealthOffersFragment;
import com.bedihospital.bedihospital.Fragment.HomeFragment;
import com.bedihospital.bedihospital.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    Menu nav_menu;
    DrawerLayout drawer;
    TextView userName, userEmail, homeFragmentText, homeFragmentInternetText;
    CircleImageView userLogo;

    LinearLayout home_fragment_appointment, home_fragment_doctor, home_fragment_health, home_fragment_emergency, homeFragmentLinearLayoutGrid;
    ImageView home_fragment_image;

    DatabaseReference mRef;
    FirebaseUser firebaseUser;
    NavigationView navigationView;

    FrameLayout homeFragmentFrameLayout;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles = {"Home", "Book an Appointment", "Find a doctor", "Health Offers", "Emergency Numbers"};

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_APPOINTMENT = "appointment";
    private static final String TAG_DOCTOR = "doctors";
    private static final String TAG_HEALTH = "health";
    private static final String TAG_EMERGENCY = "emergency";
    public static String CURRENT_TAG = TAG_HOME;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    ProgressBar homeFragmentProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //fetching linear layout ids
        home_fragment_appointment = (LinearLayout)findViewById(R.id.home_fragment_appointment);
        home_fragment_doctor = (LinearLayout)findViewById(R.id.home_fragment_doctor);
        home_fragment_health = (LinearLayout)findViewById(R.id.home_fragment_health);
        home_fragment_emergency = (LinearLayout)findViewById(R.id.home_fragment_emergency);

        //progress bar
        homeFragmentProgressBar = findViewById(R.id.homeFragmentProgressBar);

        homeFragmentLinearLayoutGrid = findViewById(R.id.homeFragmentLinearLayoutGrid);
        homeFragmentFrameLayout = findViewById(R.id.content_main);
        homeFragmentText = findViewById(R.id.homeFragmentText);
        homeFragmentInternetText = findViewById(R.id.homeFragmentInternetText);

        home_fragment_image = (ImageView)findViewById(R.id.home_fragment_image); //id for image


        //url for home page image
        String url = "https://firebasestorage.googleapis.com/v0/b/bedi-hospital.appspot.com/o/home_page_image.jpg?alt=media&token=279f6483-4f86-47a6-9272-58d9bf61f679";

        //using glide to load the url into image view
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {//loading progress bar till the image loads
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        //on faliure dissmiss progress bar and toast the error
                        homeFragmentProgressBar.setVisibility(View.GONE);

                        //check for network availability
                        if(isNetworkAvailable()) {
                            //new way to toast
                            Snackbar.make(homeFragmentFrameLayout, "Something went wrong", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        //if not then show internet error message
                        else {
                            homeFragmentInternetText.setVisibility(View.VISIBLE);
                        }


                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                       //on success dissmiss the progress bar
                        homeFragmentProgressBar.setVisibility(View.GONE);

                        //hide internet error message
                        homeFragmentInternetText.setVisibility(View.GONE);

                        //make text view and grid visible
                        homeFragmentLinearLayoutGrid.setVisibility(View.VISIBLE);
                        homeFragmentText.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .thumbnail(0.5f).into(home_fragment_image);//using glide to bring home image

        homeFragmentButtonClick(home_fragment_appointment, 1);//calling appointment fragment
        homeFragmentButtonClick(home_fragment_doctor, 2); //calling doctor fragment
        homeFragmentButtonClick(home_fragment_health, 3); //calling health fragment
        homeFragmentButtonClick(home_fragment_emergency, 4); //calling emergency fragment

        //navigation drawer fetch
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        nav_menu = navigationView.getMenu();

        mHandler = new Handler();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        //fetching ids for text view to display text on navigation drawer
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        userLogo = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.userLogo);

        mRef = FirebaseDatabase.getInstance().getReference();




        Bundle bundle = getIntent().getExtras();
        if(bundle !=  null) {
            String mainActivityMessage = bundle.getString("mainActivityMessage");
            if(mainActivityMessage != null && mainActivityMessage.equals("coming from start activity to go to doctor activity")) {

                Intent intent = new Intent(MainActivity.this, DoctorDetails.class);
                intent.putExtra("mainActivity", "coming from main activity");
                startActivity(intent);
                finish();
            }
        }

    }//on create ends

    //onStart is used to dynamically load the contents
    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // logout-login navigation butoon visibility
        loginLogoutFirebase();
    }

    //internet connection check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //handle click of button on home page
    private void homeFragmentButtonClick(LinearLayout linearLayout, final int nav) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Injnjkb", Toast.LENGTH_SHORT).show();
                switch (nav){
                    case 1:   //loads appointment fragment
                        navItemIndex = nav;
                        CURRENT_TAG = TAG_APPOINTMENT;
                        loadHomeFragment();
                        break;

                    case 2:   //loads doctor fragment
                        navItemIndex = nav;
                        CURRENT_TAG = TAG_DOCTOR;
                        loadHomeFragment();
                        break;

                    case 3:   //loads health fragment
                        navItemIndex = nav;
                        CURRENT_TAG = TAG_HEALTH;
                        loadHomeFragment();
                        break;

                    case 4:   //loads emergency fragment
                        navItemIndex = nav;
                        CURRENT_TAG = TAG_EMERGENCY;
                        loadHomeFragment();
                        break;
                }
            }
        });
    }

    private void loginLogoutFirebase() {

        if (firebaseUser != null) {

           // Toast.makeText(this, "inside login and logot method", Toast.LENGTH_SHORT).show();

            String email = firebaseUser.getEmail();
            String name = firebaseUser.getDisplayName();

            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

                if (user.getProviderId().equals("google.com")) {
                    //loading image of the user if he sign in from google account
                    Uri imageUri = firebaseUser.getPhotoUrl();//getting image uri from firebase
                    Glide.with(this).load(imageUri.toString()).thumbnail(0.5f).into(userLogo);//using glide to bring image

                    userName.setText(name);
                }
                //user whose email id not google
                else {

                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users");//linking user to app
                    final String uId = firebaseUser.getUid();//current id

//                    mref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            UserInformation user = dataSnapshot.child(uId).getValue(UserInformation.class);
//                            Log.d( "user name: ",user.getName());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });


                    DatabaseReference newRef = mref.child(uId);//in child current id
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = String.valueOf(dataSnapshot.child("name").getValue());//getting value of name
                            userName.setText(name);//setting name
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }


            Log.d("firebase providers: ", firebaseUser.getProviderData().toString());

            userEmail.setVisibility(View.VISIBLE);//email visibility on
            userEmail.setText(email);//setting email from database

            userName.setVisibility(View.VISIBLE);//name visibility on
            //showUserName(userName);//to show name from database


            //if user is logged in
            nav_menu.findItem(R.id.nav_logoutAccount).setVisible(true);//logout visible
            nav_menu.findItem(R.id.nav_loginAccount).setVisible(false);//login invisible

        } else {
            // if user is logged out
            nav_menu.findItem(R.id.nav_logoutAccount).setVisible(false);//logout invisible
            nav_menu.findItem(R.id.nav_loginAccount).setVisible(true);//login visible (in case of skip and explore

            userEmail.setVisibility(View.INVISIBLE);//email visibility gone
            userName.setVisibility(View.GONE);//name visibility gone
        }
    }



    //physical back button control
    @Override
    public void onBackPressed() {

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        //changing navigation drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d("onBackPressed: ", "nav closed");
            drawer.closeDrawers();
            return;
        } else {
            super.onBackPressed();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            finishAffinity();
        } else {
            finish();
        }


    }

    //method called when main activity ersumes from login activity an it loads home fragment without any transition
    private void loadHomeFragmentFromBackButton() {

        // selecting appropriate nav menu item
        selectNavMenu(0);

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                //android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_main, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu(navItemIndex);

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_main, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                return new HomeFragment();
            case 1:
                // appointment
                return new BookAppointmentFragment();
            case 2:
                // doctor fragment
                return new FindDoctorFragment();
            case 3:
                // health fragment
                return new HealthOffersFragment();

            case 4:
                // emergency fragment
                return new EmergencyCallFragment();

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu(int navItem) {
        navigationView.getMenu().getItem(navItem).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_book_appointment:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_APPOINTMENT;
                        break;
                    case R.id.nav_find_a_doctor:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_DOCTOR;
                        break;
                    case R.id.nav_health_offers:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_HEALTH;
                        break;
                    case R.id.nav_call:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_EMERGENCY;
                        break;
                    case R.id.nav_share:
                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();

                        //sharing the app
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String link = "https://play.google.com/store?hl=en";
                        intent.putExtra(Intent.EXTRA_TEXT, link);
                        startActivity(Intent.createChooser(intent, "Share via"));
                        return true;

                    case R.id.nav_send:
                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    case R.id.nav_logoutAccount:
                        logout();
                        break;
                    case R.id.nav_loginAccount:
                        login();
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });
    }

    private void login() {

        //delaying the load of activity on login b'coz it takes few sec to code nav drawer
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 500ms
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.putExtra("loginItem", "coming from main");//this is to hide skip and explore in login of main
                //startActivity(intent);
                startActivityForResult(intent, 1);

            }
        }, 1000);


    }

    private void logout() {

        //show progress dialog
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Logout", "Please wait", true);
        //delaying the process
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finishAffinity();
                progressDialog.dismiss();
            }
        }, 1500);

        FirebaseAuth.getInstance().signOut();//signing out

        nav_menu.findItem(R.id.nav_logoutAccount).setVisible(false);// setting logout visibility
        nav_menu.findItem(R.id.nav_loginAccount).setVisible(true);// setting login visibility
    }

    //this method works when the activity resumes and we want to show home fragment on returning from activity on back button click
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {


                if (shouldLoadHomeFragOnBackPress) {
                    // checking if user is on other navigation menu
                    // rather than home
                    if (navItemIndex != 0) {
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        selectNavMenu(0);
                        loadHomeFragmentFromBackButton();//new method for replacing fragments but without transition
                        return;
                    }
                }

            }
        }

    }
}

