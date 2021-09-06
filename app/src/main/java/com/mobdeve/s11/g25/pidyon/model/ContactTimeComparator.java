package com.mobdeve.s11.g25.pidyon.model;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class ContactTimeComparator implements Comparator<Contact> {
    // Attributes
    @SuppressLint("SimpleDateFormat")
    private DateFormat format = new SimpleDateFormat("HH:mm:ss");

    // Methods
    @Override
    public int compare(Contact o1, Contact o2) {
        return convertToMilliseconds(o2.getLatestChatTime()) - convertToMilliseconds(o1.getLatestChatTime());
    }

    private int convertToMilliseconds(String time) {
        try {
            Date date = format.parse(time);
            return (int) date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
