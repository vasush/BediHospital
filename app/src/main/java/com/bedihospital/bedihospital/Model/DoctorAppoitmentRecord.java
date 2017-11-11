package com.bedihospital.bedihospital.Model;

/**
 * Created by Vasu on 07-Nov-17.
 */

// this class is created to store and fetch user data for booking appointment
public class DoctorAppoitmentRecord {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DoctorAppoitmentRecord() {

    }

    public DoctorAppoitmentRecord(String userName, String doctorName, String speciality, String city,  String date, String time) {
        this.userName  = userName;
        this.doctorName = doctorName;
        this.speciality = speciality;
        this.city = city;
        this.date = date;
        this.time= time;
    }

    public String userName, doctorName, speciality, city, date, time;

}
