package com.example.antdashboard.models;

public class User {
    private String fullname;
    private String email;
    private String contact;
    private String emContact;
    private String address;
    private String password;
    private String token;
    private String _id;
    private boolean success;
    private String message;
    private String age;
    private User user;


    public User getUser() {
        return user;
    }

    public User(boolean success, String message, String token){
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public User(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public User(String fullname, String email, String contact, String emContact, String address, String password, String age){
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.emContact = emContact;
        this.address  =address;
        this.password = password;
        this.age = age;


    }

    public User(String fullname, String email, String contact, String emContact, String address, String age) {
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.emContact = emContact;
        this.address = address;
        this.age = age;
    }

    public User(String fullname, String email, String contact, String emContact) {
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.emContact = emContact;
    }

    public String getAge() {
        return age;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public String get_id() {
        return _id;
    }


    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getEmContact() {
        return emContact;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
