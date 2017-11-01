package com.bedihospital.bedihospital.Model;

/**
 * Created by Vasu on 29-Oct-17.
 */

// for adding details of user to firebase database
public class User {

    public String name, email, contact;

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
