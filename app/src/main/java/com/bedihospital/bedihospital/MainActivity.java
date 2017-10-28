package com.bedihospital.bedihospital;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //navigation drawer fetch
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
//        else {
//            super.onBackPressed();
//        }
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
        //calling method to change the layout on click of an item in navigation drawer
        displaySelectedScreen(id);

        return true;
    }
}

