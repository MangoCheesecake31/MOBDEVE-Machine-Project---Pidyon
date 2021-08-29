package com.mobdeve.s11.g25.pidyon.model;

public class Contact implements Comparable {
    // Attributes
    private String username;
    private String email_address;
    private String contact_id;
    private String token;

    // Constructor
    public Contact() {

    }

    public Contact(String username, String email_address, String contact_id, String token) {
        this.username = username;
        this.email_address = email_address;
        this.contact_id = contact_id;
        this.token = token;
    }

    // Methods
    public String getUsername() {
        return username;
    }

    public String getEmailAddress() { return email_address; }

    public String getContactID() {
        return contact_id;
    }

    public String getToken() { return token; }

    public void setToken() {
        this.token = token;
    }

    @Override
    public String toString() {
        return username + " " + email_address + " " + contact_id + " " + token;
    }

    @Override
    public int compareTo(Object o) {
        Contact temp = (Contact) o;
        return temp.getUsername().compareTo(this.username);
    }
}
