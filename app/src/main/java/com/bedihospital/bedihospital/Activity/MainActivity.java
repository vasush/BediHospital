package com.bedihospital.bedihospital.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bedihospital.bedihospital.Fragment.BookAppointmentFragment;
import com.bedihospital.bedihospital.Fragment.EmergencyCallFragment;
import com.bedihospital.bedihospital.Fragment.FindDoctorFragment;
import com.bedihospital.bedihospital.Fragment.HealthOffersFragment;
import com.bedihospital.bedihospital.Fragment.HomeFragment;
import com.bedihospital.bedihospital.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Menu nav_menu;
    DrawerLayout drawer;
    TextView userName, userEmail;
    CircleImageView userLogo;

    DatabaseReference mRef;
    FirebaseUser firebaseUser;
    NavigationView navigationView;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles = {"Home", "Book an ppointment", "Find a doctor", "Health Offers", "Emergency Numbers"};

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // logout-login navigation butoon visibility
        loginLogoutFirebase();

    }

    private void loginLogoutFirebase() {

        if (firebaseUser != null) {

            String email = firebaseUser.getEmail();
            String name = firebaseUser.getDisplayName();

            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("google.com")) {
                    //loading image of the user if he sign in from google account
                    Uri imageUri = firebaseUser.getPhotoUrl();//getting image uri from firebase
                    Glide.with(this).load(imageUri.toString()).thumbnail(0.5f).into(userLogo);//using glide to bring image

                    userName.setText(name);
                }
            }


            Log.d("firebase providers: ", firebaseUser.getProviderData().toString());

            userEmail.setVisibility(View.VISIBLE);//email visibility on
            userEmail.setText(email);//setting email from database

            userName.setVisibility(View.VISIBLE);//name visibility on
            showUserName(userName);//to show name from database


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

    private void showUserName(final TextView name) {

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
            drawer.closeDrawers();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            finishAffinity();
        } else {
            finish();
        }

        super.onBackPressed();
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

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

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
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
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
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

}

