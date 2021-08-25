package com.mobdeve.s11.g25.pidyon.model;

public class Contact {
    // Attributes
    private String username;
    private String email_address;
    private String contact_id;

    // Constructor
    public Contact(String username, String email_address, String contact_id) {
        this.username = username;
        this.email_address = email_address;
        this.contact_id = contact_id;
    }

    // Methods
    public String getUsername() {
        return username;
    }

    public String getEmailAddress() { return email_address; }

    public String getContactID() {
        return contact_id;
    }
}
