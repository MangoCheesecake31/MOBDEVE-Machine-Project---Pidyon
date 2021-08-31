package com.mobdeve.s11.g25.pidyon.model;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobdeve.s11.g25.pidyon.R;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {
    // Attributes
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Methods
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String texts = remoteMessage.getNotification().getBody();
        Log.d("TEST", title + ": " + texts);

        // Setting Channel
        String CHANNEL_ID = "MY NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Pidyon Notify", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationChannelManager = getSystemService(NotificationManager.class);
        notificationChannelManager.createNotificationChannel(channel);

        // Setting Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(texts)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Display Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
