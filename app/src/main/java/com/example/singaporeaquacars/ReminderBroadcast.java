package com.example.singaporeaquacars;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class ReminderBroadcast extends BroadcastReceiver {
    private static final String TAG = "ReminderBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast received. Preparing to send notification.");

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "game_notification_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Game Reminder")
                .setContentText("Are you there? Your car needs some love!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        }
        //troubleshoot
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notification permission granted. Sending notification.");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
            Log.d(TAG, "Notification sent.");
        } else {
            Log.d(TAG, "Notification permission NOT granted. Cannot send notification.");
        }
    }

}
