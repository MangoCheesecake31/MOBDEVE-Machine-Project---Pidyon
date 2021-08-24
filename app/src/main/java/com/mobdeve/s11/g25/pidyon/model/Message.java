package com.mobdeve.s11.g25.pidyon.model;

public class Message {
    // Attributes
    private String text;
    private String time;
    private String sender;
    private String receiver;

    // Constructors
    public Message() {

    }

    public Message(String text, String sender, String receiver, String time) {
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }

    // Methods
    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTime() { return time;}
}
