package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity implements GameView.OnCoinCountChangeListener, Clicker.ClickerUpdateListener {

    private Clicker clicker;
    private GameView gameView;
    private static final String CHANNEL_ID = "game_notification_channel";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // Enable full screen
        clicker = new Clicker();
        clicker.setUpdateListener(this);
        clicker.loadGameProgressFromDB(this);
        clicker.startAutoClick();

        // Set ContentView to the game view directly or use a layout that includes the GameView
        gameView = new GameView(this);
        setContentView(gameView); // Set the GameView as the content of MainActivity
        gameView.setOnCoinCountChangeListener(this);

        // Ensure the content view extends beneath system bars and control immersive mode
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()); // Hide system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        createNotificationChannel();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // Ensure views layout under system bars

        // This line will keep the screen on while the activity is in view
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCoinCountChanged(int newCoinCount) {
        clicker.setTotalCoinsEarned(newCoinCount);
        clicker.saveGameProgressToDB(this);
//        updateCoinsTextView(newCoinCount);
    }

    private void loadCoinCount() {
        int coinCount = clicker.getTotalCoinsEarned();//prefs.getInt("TotalCoins", 0); // 0 is a default value in case there's nothing saved yet
        onUpdateCoins(coinCount); // Make sure to implement this method to update the UI accordingly
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicker.loadGameProgressFromDB(this); // Refresh game progress
        clicker.startAutoClick();
        gameView.refreshPurchasedItems();
        loadCoinCount();
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        boolean autoCoinPurchased = prefs.getBoolean("AutoCoinPurchased", false);

        if (autoCoinPurchased) {
            // Activate auto-clicker if not already active
            clicker.activateContinuousAutoClickUpgrade();
            // Reset the flag
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("AutoCoinPurchased", false);
            editor.apply();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        clicker.saveGameProgressToDB(this);
        clicker.stopAutoClick();
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        clicker.saveGameProgressToDB(this); // Save game progress
        clicker.stopAutoClick();
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clicker.saveGameProgressToDB(this); // Attempt to save game progress
        clicker.stopAutoClick();
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