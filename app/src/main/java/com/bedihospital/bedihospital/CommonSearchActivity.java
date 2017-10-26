package com.bedihospital.bedihospital;

import android.graphics.drawable.Drawable;
import android.media.ImageReader;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommonSearchActivity extends AppCompatActivity{

    Spinner citySelectorSpinner, specialitySelectorSpinner;
    TextView commonText;
    ImageView commonImage;

    ArrayAdapter<String> cityArrayAdapter, specialityArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_search);

        citySelectorSpinner = (Spinner) findViewById(R.id.citySelector);
        specialitySelectorSpinner = (Spinner) findViewById(R.id.specialitySelector);
        commonImage = (ImageView)findViewById(R.id.commonImage);
        commonText = (TextView)findViewById(R.id.commonText);

        //display back arrow in action bar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getting the message from main activity
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");

        // city list categories
        List<String> cityList = new ArrayList<String>();
        cityList.add("Panchkula");
        cityList.add("Haryana");
        cityList.add("Chandigarh");

        // speciality list categories
        List<String> speacilityList = new ArrayList<String>();
        speacilityList.add("Cardiologist");
        speacilityList.add("Heart Surgen");
        speacilityList.add("Orthopedist");
        speacilityList.add("Neurologist");

        // array adapter for city
        cityArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cityList);
        cityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        citySelectorSpinner.setAdapter(cityArrayAdapter);

        //array adapter for speciality
        specialityArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, speacilityList);
        specialityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        specialitySelectorSpinner.setAdapter(specialityArrayAdapter);

        // setting differnt body of layout
        if(message.equals("Book An Appointment")) {
            mainBody(commonText, message, "@drawable/ic_book_appointment");
        }
        else  if(message.equals("Health Offers")) {
            mainBody(commonText, message,"@drawable/ic_health_offers");
        }
        else  if(message.equals("Find A Doctor")) {
            mainBody(commonText, message, "@drawable/ic_local_hospital");
        }
        else {
            //mainBody(commonText, message, "@drawable/ic_call");
            setContentView(R.layout.emergency_call);
        }
    }


    public void mainBody(TextView textView, String message, String uri) {
        getSupportActionBar().setTitle(message);
        textView.setText("Please choose the details for "+ message);

        //dyanamivally displaying the image
        int imageResource = getResources().getIdentifier(uri, null,getPackageName());
        Drawable drawable = getResources().getDrawable(imageResource);
        commonImage.setImageDrawable(drawable);
    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
