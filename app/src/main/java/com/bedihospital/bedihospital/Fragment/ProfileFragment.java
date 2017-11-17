package com.bedihospital.bedihospital.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bedihospital.bedihospital.Activity.StartActivity;
import com.bedihospital.bedihospital.DoctorDetails;
import com.bedihospital.bedihospital.EditProfile;
import com.bedihospital.bedihospital.Model.DoctorAppoitmentRecord;
import com.bedihospital.bedihospital.Model.User;
import com.bedihospital.bedihospital.R;
import com.bedihospital.bedihospital.SearchResult;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vasu on 16-Nov-17.
 */

public class ProfileFragment extends Fragment {

    CircleImageView fragmentProfileImage;
    TextView fragmentProfileName, fragmentProfileContact;
    Button fragmentProfileSignout, fragmentProfileEditButton;
    RecyclerView fragmentProfileRecyclerView;

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    LinearLayoutManager linearLayoutManager;

    RelativeLayout fragmentProfileRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // layout for finding a doctor
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();//firebase auth for current user


        fragmentProfileImage = rootView.findViewById(R.id.fragmentProfileImage);//image view
        fragmentProfileName = rootView.findViewById(R.id.fragmentProfileName);//text view for name
        fragmentProfileContact = rootView.findViewById(R.id.fragmentProfileContact);//text view for contact
        fragmentProfileSignout = rootView.findViewById(R.id.fragmentProfileSignOut);//signout button
        fragmentProfileEditButton = rootView.findViewById(R.id.fragmentProfileEditButton);//edit profile button
        fragmentProfileRelativeLayout = rootView.findViewById(R.id.fragmentProfileRelativeLayout);

        linearLayoutManager = new LinearLayoutManager(getContext());

        fragmentProfileRecyclerView = rootView.findViewById(R.id.fragmentProfileRecyclerView);//recycler view
        //fragmentProfileRecyclerView.setHasFixedSize(true);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        fragmentProfileRecyclerView.setLayoutManager(linearLayoutManager);

        mRef = FirebaseDatabase.getInstance().getReference();

        settingUserProfile();

        fragmentProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {

                    startActivity(new Intent(getActivity(), EditProfile.class));//launcing edit profile activity
                }
                else {
                    Snackbar snackbar = Snackbar.make(fragmentProfileRelativeLayout, "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        //Toast.makeText(getActivity(), fragmentProfileName.getText().toString(), Toast.LENGTH_SHORT).show();
        //progress dialog showing
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Searching", "Please wait", true);

        //getting booked appointment for all users
        final FirebaseRecyclerAdapter<DoctorAppoitmentRecord, ProfileFragment.ProfieFragmentViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DoctorAppoitmentRecord, ProfileFragment.ProfieFragmentViewHolder>(
                        DoctorAppoitmentRecord.class, R.layout.user_appointment_record_card_view, ProfileFragment.ProfieFragmentViewHolder.class,
                        mRef.child("doctorAppointment")
                                .orderByChild("userName").equalTo(fragmentProfileName.getText().toString())
                ) {

                    @Override
                    protected void populateViewHolder(ProfieFragmentViewHolder viewHolder, final DoctorAppoitmentRecord model, int position) {
                        //dismising progress dialog
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        viewHolder.userRecordCardViewDrName.setText(model.getDoctorName());//settting doctor name
                        viewHolder.userRecordCardViewDrSpeciality.setText(model.getSpeciality());//setting doctor speciality
                        viewHolder.userRecordCardViewDrDate.setText(model.getDate());//getting booked date
                        viewHolder.userRecordCardViewDrTime.setText(model.getTime());//getting booked date


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //starting doctor details intent to set date and time
                                Intent intent = new Intent(getActivity(), DoctorDetails.class);
                                intent.putExtra("doctorName", model.getDoctorName());//sending doc name
                                //intent.putExtra("doctorDetail", model.getDetail());//sending doc detail from firebase
                                intent.putExtra("doctorSpeciality", model.getSpeciality());//sendgin doc speciality
                                intent.putExtra("doctorCity", model.getCity());//sending doc city

                                startActivity(intent);


                            }
                        });

                    }

                };

        fragmentProfileRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

        //sign out button click
        fragmentProfileSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress dialog
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Logout", "Please wait...", true);
                //delaying the process
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        startActivity(new Intent(getActivity(), StartActivity.class));
                        //finishAffinity();
                        progressDialog.dismiss();
                    }
                }, 1500);

                FirebaseAuth.getInstance().signOut();//signing out
            }
        });

        return rootView;
    }

    private void settingUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            for (UserInfo user : mAuth.getCurrentUser().getProviderData()) {
                //user sign in from google api
                if (user.getProviderId().equals("google.com")) {

                    //loading image of the user if he sign in from google account
                    Uri imageUri = mAuth.getCurrentUser().getPhotoUrl();//getting image uri from firebase
                    Glide.with(this).load(imageUri.toString()).thumbnail(0.5f).into(fragmentProfileImage);//using glide to bring image

                    String name = mAuth.getCurrentUser().getDisplayName();//getting value of name
                    fragmentProfileName.setText(name);//setting name
                    // break;
                }
                //user sign in via email and password
                else {
                    String uId = mAuth.getCurrentUser().getUid();
                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users").child(uId);//linking user to app

                    mref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = String.valueOf(dataSnapshot.child("name").getValue());//getting value of name
                            fragmentProfileName.setText(name);//setting name

                            String contact= String.valueOf(dataSnapshot.child("contact").getValue());//getting contat cf user
                            fragmentProfileContact.setText(contact);//setting contact
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

    }

    //view holder class
    public static class ProfieFragmentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView userRecordCardViewDrName, userRecordCardViewDrSpeciality, userRecordCardViewDrDate, userRecordCardViewDrTime;

        public ProfieFragmentViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userRecordCardViewDrName = itemView.findViewById(R.id.userRecordCardViewDrName);
            userRecordCardViewDrSpeciality = itemView.findViewById(R.id.userRecordCardViewDrSpeciality);
            userRecordCardViewDrDate = itemView.findViewById(R.id.userRecordCardViewDrDate);
            userRecordCardViewDrTime = itemView.findViewById(R.id.userRecordCardViewDrTime);



        }
    }

    @Override
    public void onStart() {
        super.onStart();

       // settingUserProfile();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    //internet connection check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
