package com.example.singaporeaquacars;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;


public class AppLifecycleObserver implements DefaultLifecycleObserver {
    private Context context;

    // Constructor
    public AppLifecycleObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // App goes into the background
        Intent serviceIntent = new Intent(context, ReminderService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // App comes back to the foreground
        Intent intent = new Intent(context, ReminderBroadcast.class);
        // Make sure this PendingIntent matches the one used in your ReminderService exactly.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        Intent serviceIntent = new Intent(context, ReminderService.class);

        context.stopService(serviceIntent);

    }
}