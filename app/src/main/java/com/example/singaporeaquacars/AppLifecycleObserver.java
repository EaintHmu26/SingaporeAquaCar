package com.example.singaporeaquacars;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
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
        Intent serviceIntent = new Intent(context, ReminderService.class);
        context.stopService(serviceIntent);
    }
}
