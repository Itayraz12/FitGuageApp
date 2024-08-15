package com.example.fitgaugeproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        if (intent != null) {
            Log.d(TAG, "Intent action: " + intent.getAction());
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Log.d(TAG, "Intent extra: " + key + " -> " + extras.get(key));
                }
            } else {
                Log.d(TAG, "Intent has no extras");
            }
        } else {
            Log.e(TAG, "Received null intent");
            return;
        }

        if ("com.example.fitgaugeproject.ACTION_GEOFENCE_EVENT".equals(intent.getAction())) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            


            if (geofencingEvent == null) {
                Log.e(TAG, "GeofencingEvent is null. Intent: " + intent + " | Action: " + intent.getAction());
                return;
            }

            if (geofencingEvent.hasError()) {
                int errorCode = geofencingEvent.getErrorCode();
                Log.e(TAG, "Geofencing error: " + errorCode + ". Event: " + geofencingEvent);
                return;
            }

            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            Log.d(TAG, "Geofence transition detected: " + geofenceTransition);

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null && !triggeringGeofences.isEmpty()) {
                for (Geofence geofence : triggeringGeofences) {
                    Log.d(TAG, "Triggered Geofence ID: " + geofence.getRequestId());
                    sendNotification(context, geofence.getRequestId(), geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER);
                }
            } else {
                Log.d(TAG, "No triggering geofences found");
            }
        } else {
            Log.e(TAG, "Received unexpected intent action: " + intent.getAction());
        }
    }


    private void sendNotification(Context context, String geofenceId, boolean entering) {
        String notificationText = entering ? "You have entered the gym area: " + geofenceId : "You have exited the gym area: " + geofenceId;

        Log.d(TAG, "Preparing to send notification: " + notificationText); // Log notification text

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Gym Proximity Alert")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notification permissions granted, sending notification");
            notificationManager.notify(0, builder.build());
        } else {
            Log.e(TAG, "Notification permissions not granted. Make sure to request POST_NOTIFICATIONS permission.");
        }
    }
}
