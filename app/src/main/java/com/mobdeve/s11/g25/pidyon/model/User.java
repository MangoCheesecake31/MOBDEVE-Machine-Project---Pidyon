package com.mobdeve.s11.g25.pidyon.model;

public class User {
    // Comments

    // Attributes
    private String username;
    private String email_address;
    private String token;

    // Constructors
    public User() {

    }

    public User(String username, String email_address) {
        this.username = username;
        this.email_address = email_address;
    }

    public User(String username, String email_address, String token) {
        this.username = username;
        this.email_address = email_address;
        this.token = token;
    }

    // Methods
    public String getUsername() {
        return username;
    }

    public String getEmailAddress() {
        return email_address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
