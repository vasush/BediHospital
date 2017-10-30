package com.bedihospital.bedihospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Menu nav_menu;
    DrawerLayout drawerLayout;
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (TextView)findViewById(R.id.userName);
        userEmail= (TextView)findViewById(R.id.userEmail);

        //navigation drawer fetch
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_menu = navigationView.getMenu();



        // logot-login navigation butoon visibility
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            //if user is logged in
            nav_menu.findItem(R.id.nav_logoutAccount).setVisible(true);//logout visible
            nav_menu.findItem(R.id.nav_loginAccount).setVisible(false);//login invisible

        }
        else{
            // if user is logged out
            nav_menu.findItem(R.id.nav_logoutAccount).setVisible(false);//logout invisible
            nav_menu.findItem(R.id.nav_loginAccount).setVisible(true);//login visible (in case of skip and explore

            userName.setVisibility(View.INVISIBLE);// visibility of name gone
            userEmail.setVisibility(View.INVISIBLE);
        }
    }
//physical back button control
    @Override
    public void onBackPressed() {

        // for replacing the last fragment on back press
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            finishAffinity();
        }
        else{
            finish();
        }
    }

//loading the fragments when items are clicked
    private void displaySelectedScreen(int id) {
        Fragment fragment = null;
        String title = "";

        switch (id) {
            case R.id.nav_book_appointment:
                fragment = new BookAppointment();
                title = "Book Appointment";
                break;
            case R.id.nav_find_a_doctor:
                fragment = new FindDoctor();
                title = "Find A Doctor";
                break;
            case R.id.nav_health_offers:
                fragment = new HealthOffers();
                title = "Health Offers";
                break;
            case R.id.nav_call:
                fragment = new EmergencyCall();
                title = "Emergency Numbers";
                break;
        }
//relacing the fragments
        if(fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            getSupportActionBar().setTitle(title);
            ft.addToBackStack("home");
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_share) {

            //sharing the app
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String link = "https://play.google.com/store?hl=en";
            intent.putExtra(Intent.EXTRA_TEXT,link);
            startActivity(Intent.createChooser(intent,"Share via"));
        }
        //logout item click handler, mean it logout and remain on main activity
        else if(id == R.id.nav_logoutAccount) {
            //show progress dialog
            final ProgressDialog progressDialog = ProgressDialog.show(this,"Logout","Please wait",true);
            //delaying the process
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    FirebaseAuth.getInstance().signOut();
                    progressDialog.dismiss();
                }
            }, 1000);

            FirebaseAuth.getInstance().signOut();//signing out

            nav_menu.findItem(R.id.nav_logoutAccount).setVisible(false);// setting logout visibility
            nav_menu.findItem(R.id.nav_loginAccount).setVisible(true);// setting login visibility

            //closing navigation drawer
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

        }
        //login item click handler. goes to start activity
        else if(id == R.id.nav_loginAccount) {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
        }

        //calling method to change the layout on click of an item in navigation drawer
        displaySelectedScreen(id);

        return true;
    }
}

