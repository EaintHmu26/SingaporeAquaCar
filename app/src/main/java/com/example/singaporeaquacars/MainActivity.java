package com.example.singaporeaquacars;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_NOTIFICATION_PERMISSION = "extra_notification";
    private static final String CHANNEL_ID = "game_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameView gameView = new GameView(this);
        setContentView(gameView);

//            EdgeToEdge.enable(this);
//            setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(gameView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createNotificationChannel();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Check if the app is not switching between configurations
        if (!isChangingConfigurations()) {
            Intent serviceIntent = new Intent(this, ReminderService.class);
            startService(serviceIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When the activity is paused, you assume the user is no longer actively using the app.
        Intent serviceIntent = new Intent(this, ReminderService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the activity resumes, cancel the alarm as the user is back in the app.
        Intent notificationIntent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }

    public void startGame (GameView view){}
}