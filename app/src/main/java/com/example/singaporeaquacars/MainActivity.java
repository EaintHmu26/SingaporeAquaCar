package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements Clicker.ClickerUpdateListener {

    private Clicker clicker;
    private TextView coinsTextView;
    public static final String EXTRA_SHOW_NOTIFICATION_PERMISSION = "extra_notification";
    private static final String CHANNEL_ID = "game_notification_channel";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clicker = new Clicker();
        clicker.setUpdateListener(this);
        clicker.loadGameProgressFromDB(this);
        clicker.startAutoClick();

//            EdgeToEdge.enable(this);
//            setContentView(R.layout.activity_main);
//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//                return insets;
//            });
//
//            GameView gameView = new GameView(this);
//            setContentView(gameView);
        // Create a button programmatically

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        coinsTextView = new TextView(this);
        coinsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        coinsTextView.setText("0"); // Initial value
        rootLayout.addView(coinsTextView);

        // Create a Button for clicking
        Button clickButton = new Button(this);
        clickButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        clickButton.setText("Click Me");
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call handleClick() on the instance of CookieClicker
                clicker.handleClick();
                // Update the TextView with the current number of coins
                updateCoinsTextView(clicker.getTotalCoinsEarned());
            }
        });
        rootLayout.addView(clickButton);

        GameView gameView = new GameView(this);
        //            EdgeToEdge.enable(this);
        //            setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(gameView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout.LayoutParams gameViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1.0f);
        gameView.setLayoutParams(gameViewParams);
        rootLayout.addView(gameView);

        setContentView(rootLayout);
        createNotificationChannel();
    }
    @Override
    protected void onResume() {
        super.onResume();
        clicker.loadGameProgressFromDB(this); // Refresh game progress
        clicker.startAutoClick();
        updateCoinsTextView(clicker.getTotalCoinsEarned());
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
        System.out.println("DB is saving on pause event");
        clicker.saveGameProgressToDB(this);
        clicker.stopAutoClick();
        // Assume the user is no longer actively using the app
        Intent serviceIntent = new Intent(this, ReminderService.class);
        startService(serviceIntent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("DB is saving on stop event");
        clicker.saveGameProgressToDB(this); // Save game progress
        clicker.stopAutoClick();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("DB is saving on destroy event");
        clicker.saveGameProgressToDB(this); // Attempt to save game progress
        clicker.stopAutoClick();
        // Check if the app is not switching between configurations
        if (!isChangingConfigurations()) {
            Intent serviceIntent = new Intent(this, ReminderService.class);
            startService(serviceIntent);
        }
    }
    @Override
    public void onUpdateCoins(int totalCoins) {
        updateCoinsTextView(totalCoins); // Call a method to update the TextView with the new total
    }

    private void updateCoinsTextView(int totalCoins) {
        coinsTextView.setText(String.valueOf(totalCoins));
    }
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