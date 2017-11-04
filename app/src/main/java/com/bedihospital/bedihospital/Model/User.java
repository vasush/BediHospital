package com.bedihospital.bedihospital.Model;

/**
 * Created by Vasu on 29-Oct-17.
 */

// for adding details of user to firebase database
public class User {
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getSpaciality_city() {
        return spaciality_city;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public void setSpaciality_city(String spaciality_city) {
        this.spaciality_city = spaciality_city;
    }

    public String name, email, contact, city, speciality, spaciality_city, fare;

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public User(String name, String email, String contact) {

        this.name = name;
        this.email = email;
        this.contact = contact;

    }

    public  User(String name, String email) {

        this.name = name;
        this.email = email;
    }

}
