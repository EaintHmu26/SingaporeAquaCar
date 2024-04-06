package com.example.singaporeaquacars;
import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }
}
