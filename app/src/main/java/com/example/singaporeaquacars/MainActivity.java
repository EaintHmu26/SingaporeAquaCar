package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity implements GameView.OnCoinCountChangeListener, Clicker.ClickerUpdateListener {

    private static final String TAG = "Main";
    private Clicker clicker;
//    private TextView coinsTextView;
    public static final String EXTRA_SHOW_NOTIFICATION_PERMISSION = "extra_notification";
    private static final String CHANNEL_ID = "game_notification_channel";
    private GameView gameView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clicker = new Clicker();
        clicker.setUpdateListener(this);
        clicker.loadGameProgressFromDB(this);
        clicker.startAutoClick();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);

//        coinsTextView = new TextView(this);
//        coinsTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        rootLayout.addView(coinsTextView);

        gameView = new GameView(this);
        gameView.setOnCoinCountChangeListener(this);

        LinearLayout.LayoutParams gameViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1.0f);
        gameView.setLayoutParams(gameViewParams);
        rootLayout.addView(gameView);

        setContentView(rootLayout);
        createNotificationChannel();
    }

    @Override
    public void onCoinCountChanged(int newCoinCount) {
        clicker.setTotalCoinsEarned(newCoinCount);
        clicker.saveGameProgressToDB(this);
//        updateCoinsTextView(newCoinCount);
    }

    private void loadCoinCount() {
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int coinCount = clicker.getTotalCoinsEarned();//prefs.getInt("TotalCoins", 0); // 0 is a default value in case there's nothing saved yet
        onUpdateCoins(coinCount); // Make sure to implement this method to update the UI accordingly
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicker.loadGameProgressFromDB(this); // Refresh game progress
        Log.d(TAG, "clicker coins per click" + clicker.getCurrentCoinsPerClick());
        clicker.startAutoClick();
        gameView.refreshPurchasedItems();
        loadCoinCount();
//        updateCoinsTextView(clicker.getTotalCoinsEarned());
      
        // Cancel any set alarms as the user is back
        Intent notificationIntent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        }

        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        boolean autoCoinPurchased = prefs.getBoolean("AutoCoinPurchased", false);

        if (autoCoinPurchased) {
            // Activate auto-clicker if not already active
            clicker.activateContinuousAutoClickUpgrade(this);
            // Reset the flag
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("AutoCoinPurchased", false);
            editor.apply();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"DB is saving on pause event");
        clicker.saveGameProgressToDB(this);
        clicker.stopAutoClick();
        // Assume the user is no longer actively using the app
        Intent serviceIntent = new Intent(this, ReminderService.class);
        startService(serviceIntent);
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"DB is saving on stop event");
        clicker.saveGameProgressToDB(this); // Save game progress
        clicker.stopAutoClick();
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"DB is saving on destroy event");
        clicker.saveGameProgressToDB(this); // Attempt to save game progress
        clicker.stopAutoClick();
        // Check if the app is not switching between configurations
        if (!isChangingConfigurations()) {
            Intent serviceIntent = new Intent(this, ReminderService.class);
            startService(serviceIntent);
        }
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }
    @Override
    public void onUpdateCoins(int totalCoins) {
        if (gameView != null) {
            gameView.setCoinCount(totalCoins); // Update GameView's coin count
        }
    }

//    private void updateCoinsTextView(int totalCoins) {
//        coinsTextView.setText("Coins: $" + totalCoins); // This sets the text you want to show
//    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}