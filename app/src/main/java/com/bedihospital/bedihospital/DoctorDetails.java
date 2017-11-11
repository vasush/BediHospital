package com.bedihospital.bedihospital;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bedihospital.bedihospital.Activity.MainActivity;
import com.bedihospital.bedihospital.Activity.StartActivity;
import com.bedihospital.bedihospital.Model.DoctorAppoitmentRecord;
import com.bedihospital.bedihospital.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DoctorDetails extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    TextView doctorDetailsName, doctorDetailsString, doctorDate;
    String doctorName, doctorDetail, selectedTime = "0";
    Button bookAppointment;
    GridView timeGridView;

    Session session = null;
    Context context = null;

    ScrollView doctorDetailsScrollView;
    CoordinatorLayout coordinatorLayout;

    ProgressDialog progressDialog;

    Snackbar snackbar;
    String exception = new String("Sunday");

    String[] allMonths = {" ", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    String[] timeHourMinuteAmPm = new String[4];
    String currentHour, currentMinute, currentAmPm;

    String date = null, time = null, docName = null, docSpeciality = null, docCity = null;

    String speciality, city;

    //used for storing date
    String currentDate, currentTime, monthName, pickedDayOfWeek;
    int day, month, year;
    int dayFinal, monthFinal, yearFinal;

    int flag = 0;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    String userEmailId = null, userName = "", userContact = "";

    String currentDay, currentMonth, currentYear, currentDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to get the values send from search result activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            doctorName = bundle.getString("doctorName");//getting doc name
            doctorDetail = bundle.getString("doctorDetail");//getting doc details
            speciality = bundle.getString("doctorSpeciality");//getting doc speciality
            city = bundle.getString("doctorCity");//getting doc city

            setContentView(R.layout.activity_doctor_details);

            //userEmailId = bundle.getString("userEmailId");//getting user email id for sending mail as it is not working on async task
            //userName = bundle.getString("userName");//getting user name for sending mail as it is not working on async task
            //userContact = bundle.getString("userContact");//getting user contact for sending mail as it is not working on async task

            String mainActivityMessage = bundle.getString("mainActivityMessage");
            if (mainActivityMessage != null && mainActivityMessage.equals("coming from start activity to go to doctor activity")) {
                finish();
            }
        }

        context = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fetching ids
        doctorDetailsName = findViewById(R.id.doctorDetailsName);
        doctorDetailsString = findViewById(R.id.doctorDetailsString);
        bookAppointment = findViewById(R.id.bookAppointment);
        doctorDate = findViewById(R.id.doctorDate);
        //doctorTime = findViewById(R.id.doctorTime);
        timeGridView = findViewById(R.id.timeGridView);
        //doctorDetailsScrollView = findViewById(R.id.doctorDetailsScrollView);
        coordinatorLayout = findViewById(R.id.coordinatorlayout);

        CurrentDateTime();//to get current date and time

        databaseReference = FirebaseDatabase.getInstance().getReference(); //database refrence

        //doctorTime.setText(currentTime);//setting current date
        doctorDate.setText(currentDate);//setting current time

        doctorDetailsName.setText(doctorName);//setting doc name
        doctorDetailsString.setText(doctorDetail);//setting doc details

        //pick date text view on click handler
        doctorDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();//method to open date picker dialog

            }
        });

        timeGridView.setAdapter(new TimeAdapter(this));//setting grid view adapter (time adapter class)
        //grid view item on click
        timeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("onItemClick: ", adapterView.getAdapter().getItem(i).toString());
                // timeGridView.setDrawSelectorOnTop(false);
                //view.setSelected(true);//keep item selected or highlighted

                selectedTime = adapterView.getAdapter().getItem(i).toString();//fetching time for clicked item

                //this loop is to seprate am and pm in a string array from the selected time string
                int beg = 0, end = 2;
                for (int m = 0; m < 3; m++) {
                    timeHourMinuteAmPm[m] = selectedTime.substring(beg, end);
                    beg += 3;
                    end += 3;
                }

                if (compareTime()) {
                    bookAppointmentButtonClick();
                }
                //is selected time is less than current time
                else {
//                    snackbar = Snackbar.make(coordinatorLayout, "Can't book this time now.", Snackbar.LENGTH_LONG);
//                    snackbar.show();

                    Snackbar.make(view, "Can't book this time now.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }

                //comparing selected time with curren time

                //Log.d( "onItemClick: ", timeHourMinuteAmPm[0]  + timeHourMinuteAmPm[1] + timeHourMinuteAmPm[2]);
            }
        });

        bookAppointmentButtonClick();

    }//on create ends here

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();//fireauth reference

        //used to get username, user email, user contact
        if (mAuth.getCurrentUser() != null) {

            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("google.com")) {
                    //loading image of the user if he sign in from google account
                    userName = mAuth.getCurrentUser().getDisplayName();
                    userEmailId = mAuth.getCurrentUser().getEmail();
                } else {
                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users");//linking user to app
                    final String uId = mAuth.getCurrentUser().getUid();//current id

                    DatabaseReference newRef = mref.child(uId);//in child current id
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = String.valueOf(dataSnapshot.child("name").getValue());//getting value of name
                            String email = String.valueOf(dataSnapshot.child("email").getValue());//getting value of name
                            String contact = String.valueOf(dataSnapshot.child("contact").getValue());//getting value of name

                            userName = name;
                            userEmailId = email;
                            userContact = contact;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    private void bookAppointmentButtonClick() {

        //book appoinment button click listner
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(DoctorDetails.this, currentTime, Toast.LENGTH_SHORT).show();
                // Log.d( "user email id: ", userEmailId);

                if (mAuth.getCurrentUser() != null) {

                    //Toast.makeText(context, userName + " " + userEmailId + " " + userContact, Toast.LENGTH_SHORT).show();

                    //check if selecetd month, year is previous to current month,year
                    if ((monthFinal < Integer.valueOf(currentMonth)) || (yearFinal < Integer.valueOf(currentYear))) {

                        snackbar = Snackbar.make(coordinatorLayout, "Can't pick previous date", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    //check for previous date
                    else if (dayFinal < Integer.valueOf(currentDay)) {

                        snackbar = Snackbar.make(coordinatorLayout, "Can't book previous date", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    //check for sunday
                    else if (currentDayOfWeek.equals(exception) || pickedDayOfWeek.equals(exception)) {

                        snackbar = Snackbar.make(coordinatorLayout, "Doctor is not available on sunday", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    //check if selected time is null
                    else if (selectedTime.equals("0")) {

                        snackbar = Snackbar.make(coordinatorLayout, "Pick a suitable time", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {

                        if (compareTime()) {

                            //checking whether appointment is already there or not
                            com.google.firebase.database.Query query = databaseReference.child("doctorAppointment")
                                    .orderByChild("time").equalTo(selectedTime);


                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //if data does not exists
                                    if (!dataSnapshot.exists()) {
                                        //do something
                                        //appointmentSearchResult.setText("No record found.");
                                        Log.d("ds exists: ", ",ds does not exists");
                                        confirmDialogBox();

                                    } else {
                                        //iterate through whole object of database with spaeicif details
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if (ds.exists()) {


                                                date = ds.getValue(DoctorAppoitmentRecord.class).getDate();//setting date
                                                time = ds.getValue(DoctorAppoitmentRecord.class).getTime();//setting time
                                                docName = ds.getValue(DoctorAppoitmentRecord.class).getDoctorName();//setting doc name
                                                docSpeciality = ds.getValue(DoctorAppoitmentRecord.class).getSpeciality();//setting spec
                                                docCity = ds.getValue(DoctorAppoitmentRecord.class).getCity();//setting city

                                                if (date.equals(currentDate) && time.equals(selectedTime) && docName.equals(doctorName)
                                                        && docSpeciality.equals(speciality) && docCity.equals(city)) {

                                                    flag = 1;
                                                    break;
                                                }
                                            }
                                        }

                                        Log.d("flag counter: ", Integer.toString(flag));
                                        if (flag == 1) {
                                            //already appoint
                                            snackbar = Snackbar.make(coordinatorLayout, "Appointment is already booked for, " + docName + "." + " Pick some other time", Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                            date = "";
                                            time = "";
                                            docName = "";
                                            docSpeciality = "";
                                            docCity = "";


                                        } else {
                                            //if appointment is not booked then open confirm appointment dialog.
                                            confirmDialogBox();

                                            date = "";
                                            time = "";
                                            docName = "";
                                            docSpeciality = "";
                                            docCity = "";

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        //is selected time is less than current time
                        else {
                            snackbar = Snackbar.make(coordinatorLayout, "Can't book this time now.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    }

                }

                //if not login, open login activity.
                else {
                    snackbar = Snackbar.make(coordinatorLayout, "Need to login first", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    //delaying the process to load login page
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 1.5s
                            Intent intent = new Intent(DoctorDetails.this, StartActivity.class);
                            intent.putExtra("bookingAppointmentLogin", "coming from doctor detail");
                            startActivity(intent);

                        }
                    }, 1500);

                }
                //if(dayFinal==Integer.valueOf(checkDate))
                //Log.d("curr date: ", Integer.toString(dayFinal));

            }
        });

    }


    //creating a dialog box
    private void confirmDialogBox() {

        //progress dialog showing
//        progressDialog = ProgressDialog.show(this, "Searching", "Please wait", true);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DoctorDetails.this);
        View mView = getLayoutInflater().inflate(R.layout.confirm_appointment_dialog, null);

        TextView dialogDocName, dialogDocDate, dialogDocTime;
        Button dialogConfirm, dialogCancel;

        dialogDocName = mView.findViewById(R.id.dialogDocName);
        dialogDocDate = mView.findViewById(R.id.dialogDocDate);
        dialogDocTime = mView.findViewById(R.id.dialogDocTime);
        dialogConfirm = mView.findViewById(R.id.dialogConfirm);
        dialogCancel = mView.findViewById(R.id.dialogCancel);

        //dismising progress dialog
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }

        dialogDocName.setText(doctorName);
        dialogDocDate.setText("Date: " + currentDate);
        dialogDocTime.setText("Time: " + selectedTime);

        mBuilder.setView(mView);
        final AlertDialog alertDialog = mBuilder.create();

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //adding date and time to database of doctorappoitment object.
                addToDatabase();

                Log.d("User name fromConfirm: ", userName + " " + userContact);

                //sending email to the user via java mail api
                sendingEmail();

                //dismiss dialog box.
                alertDialog.dismiss();

                //showing notification
                showNotification();

                //starting main activity on confirming appointment.
                startActivity(new Intent(DoctorDetails.this, StartActivity.class));
                finishAffinity();


            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                // Toast.makeText(DoctorDetails.this, "canceled...", Toast.LENGTH_SHORT).show();
            }
        });

        if (!isFinishing()) {
            alertDialog.show();
        }

    }

    //sending mail to user via java mail api
    private void sendingEmail() {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                //sending mail from the account mention below
                return new PasswordAuthentication("vasush8402@gmail.com", "akinchan");
            }
        });

        Log.d("sending Email ....: ", "executing async task  ");
        Log.d("sendingEmail: ", userEmailId + " " + userName + " " + userContact);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();


    }

    //calss use to send mail
    @SuppressLint("StaticFieldLeak")
    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d("user email id: ", "in async task" + userEmailId);

                //differnt mail id's
                String[] multipleMailId = new String[]{userEmailId, "ramchanderk6@gmail.com"};

                //different content according to different id's
                String[] content = new String[]{"You booked an appointment with " + doctorName + " on "
                        + currentDate + " at " + selectedTime.substring(0, 8) + "." + "<br>" + "Please reach 5 minutes before time."
                        + "<br>" + "<br>" + "Thank you.", userName + ", " + userContact + " has appointment with "
                        + doctorName + ", " + speciality + " from " + city
                        + " on " + currentDate + " at " + selectedTime};

                //creating message twice(depending on no of id's)
                for (int m = 0; m < multipleMailId.length; m++) {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("vasush8402@gmail.com"));
                    //sending mail to userEmail id
                    message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(multipleMailId[m]));
                    message.setSubject("Bedi Hospital");//subject
                    message.setContent(content[m], "text/html; charset=utf-8");//content
                    Transport.send(message);
                }

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            Log.d("on confirm booking: ", "mail sent");
        }
    }

    //showing notification
    private void showNotification() {

        //intent for loading main activity on click of a button
        Intent intent = new Intent(this, MainActivity.class);

        //getting default system ringtone for sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(DoctorDetails.this, 0, intent, 0);

        //set up notification
        final Notification notification = new Notification.Builder(DoctorDetails.this)
                .setContentTitle(doctorName + " appointment")//title
                .setContentText(currentDate + ", " + selectedTime)//message
                .setSmallIcon(R.drawable.bedi_logo)//icon
                .setSound(alarmSound)//sound
                .setContentIntent(pendingIntent).getNotification();//setting intent


        notification.flags = Notification.FLAG_AUTO_CANCEL;//auto cancel on click

        //delaying the process of sending intent notofication.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);
            }
        }, 5000);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);
    }

    //adding time and date to database
    private void addToDatabase() {
        Log.d("addToDatabase: ", "in add to data base...");
        if (mAuth.getCurrentUser() != null) {


            //creating object of User class to send multiple data
            DoctorAppoitmentRecord object = new DoctorAppoitmentRecord(userName, doctorName, speciality, city, currentDate, selectedTime);
            databaseReference.child("doctorAppointment").child(currentDate + ", " + selectedTime + ", " + doctorName.substring(4) + ", "
                    + speciality + ", " + city).setValue(object);//writing to firebase database

        }
    }

    private boolean compareTime() {

        Log.d("currentTime: ", currentHour + currentMinute + currentAmPm);
        Log.d("selectedTime: ", timeHourMinuteAmPm[0] + timeHourMinuteAmPm[1] + timeHourMinuteAmPm[2]);

        //converting am to 1 and pm to 2 for selected time
        if (timeHourMinuteAmPm[2].matches("am") || timeHourMinuteAmPm[2].matches("1")) {
            timeHourMinuteAmPm[2] = "1";
        } else {
            timeHourMinuteAmPm[2] = "2";
        }

        //converting am to 1 and pm to 2 for current time
        if (currentAmPm.matches("a.m.") || currentAmPm.matches("1")) {
            currentAmPm = "1";
        } else {
            currentAmPm = "2";
        }

        Log.d("currentTimeAterConver: ", currentHour + currentMinute + currentAmPm);
        Log.d("selectedTimeAfterCon: ", timeHourMinuteAmPm[0] + timeHourMinuteAmPm[1] + timeHourMinuteAmPm[2]);

        //checking for time for current date only
        if (dayFinal == Integer.valueOf(currentDay) && monthFinal == Integer.valueOf(currentMonth) && yearFinal == Integer.valueOf(currentYear)) {

            //comparing 2 time interval
            if (timeHourMinuteAmPm[2].equals(currentAmPm)) { //selAmPm == currAmPm

                if (timeHourMinuteAmPm[0].equals(currentHour)) { //selHour == currHour

                    if ((Integer.valueOf(timeHourMinuteAmPm[1]) >= Integer.valueOf(currentMinute))) { //selMin >= currMin
                        return true;
                    } else {//selMin <= currMin
                        return false;
                    }
                } else if (currentHour.matches("12") || timeHourMinuteAmPm[0].matches("12")) {//exception when current time is 12

                    if (Integer.valueOf(timeHourMinuteAmPm[0]) < Integer.valueOf(currentHour)) {
                        return true;
                    } else {
                        return false;
                    }

                }
//                else if(timeHourMinuteAmPm[0].matches("12")) {//exception when current time is 12
//
//                    if(Integer.valueOf(timeHourMinuteAmPm[0]) < Integer.valueOf(currentHour)) {
//                        return false;
//                    }
//                    else {
//                        return true;
//                    }
//
//                }
                else if (Integer.valueOf(currentHour) < Integer.valueOf(timeHourMinuteAmPm[0])) {//selHour > currHour
                    return true;
                } else {//selHour < currHour
                    return false;
                }
            } else if (Integer.valueOf(currentAmPm) < Integer.valueOf(timeHourMinuteAmPm[2])) {//selAmPm > currAmPm
                return true;
            } else {//selAmPm < currAmPm
                return false;
            }
        } else {
            return true;
        }

    }


    private void CurrentDateTime() {

        // String date = new SimpleDateFormat("dd").format(new Date());
        String date = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());//this is current date in string
        currentTime = new SimpleDateFormat("hh:mm a").format(new Date());//this is current time in string

        currentHour = new SimpleDateFormat("hh").format(new Date());//this is current time hour in string
        currentMinute = new SimpleDateFormat("mm").format(new Date());//this is current time minute in string
        currentAmPm = new SimpleDateFormat("a").format(new Date());//this is current time am-pm in string

        //currentAmPm = currentAmPm.replaceAll(".", "");
        //Log.d( "CurrentDateTime: ", currentHour + currentMinute + currentAmPm);

        currentDay = new SimpleDateFormat("d").format(new Date());//current day  in string
        currentMonth = new SimpleDateFormat("MM").format(new Date());//current month  in string
        currentYear = new SimpleDateFormat("yyyy").format(new Date());//current year in string

        currentDayOfWeek = new SimpleDateFormat("EEEE").format(new Date());//current day name in string
        pickedDayOfWeek = currentDayOfWeek;

        //currentDayOfWeek = allDays[Integer.valueOf(currentDayOfWeek)];


        dayFinal = Integer.valueOf(currentDay);//setting final day to current day so that when user doesn't selects the day it won't show 0
        monthFinal = Integer.valueOf(currentMonth);//setting final month to current month so that when user doesn't selects the day it won't show 0
        yearFinal = Integer.valueOf(currentYear);//setting final year to current year so that when user doesn't selects the day it won't show 0
        //checkDayOfWeek = Integer.valueOf(dayOfWeek)

        monthName = allMonths[Integer.valueOf(currentMonth)];//setting int values to string name of months

        currentDate = currentDayOfWeek + ", " + currentDay + "-" + monthName + "-" + currentYear;//setting initial date to current date

    }

    //getting date
    private void datePicker() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);//getting year
        month = calendar.get(Calendar.MONTH);//getting month
        day = calendar.get(Calendar.DAY_OF_MONTH);//getting date


        //opening dialog to pick date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, this,
                year, month, day);
        datePickerDialog.show();//showing dialog

//        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == DialogInterface.BUTTON_POSITIVE) {
//                            //selectedTime = "0";
//                        }
//                    }
//                });

    }

    //setting date
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        //getting name of the day picked by user
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(i, i1, i2 - 1);
        pickedDayOfWeek = simpledateformat.format(date);


        monthName = allMonths[monthFinal];//setting int values to string name of months

        //updating date when user selects date from dialog
        currentDate = pickedDayOfWeek + ", " + dayFinal + "-" + monthName + "-" + yearFinal;
        doctorDate.setText(currentDate);//setting updated date on text view
        Log.d("dateTime: ", currentDate);

        // selectedTime = "0";//time is set to zero when date changes b'coz grid view item is getting deselected.

    }

    // back arrow on action bar working
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
