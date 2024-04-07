package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity implements GameView.OnCoinCountChangeListener, Clicker.ClickerUpdateListener {

    private static final String TAG = "Main";
    private Clicker clicker;
    private GameView gameView;
    private static final String CHANNEL_ID = "game_notification_channel";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide the title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // enable full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        clicker = new Clicker();
        clicker.setUpdateListener(this);
        clicker.loadGameProgressFromDB(this);
        clicker.startAutoClick();

        gameView = new GameView(this);
        setContentView(gameView);
        gameView.setOnCoinCountChangeListener(this);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()); // Hide system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        createNotificationChannel();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // to keep the screen on as long as the game is running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCoinCountChanged(int newCoinCount) {
        clicker.setTotalCoinsEarned(newCoinCount);
        clicker.saveGameProgressToDB(this);
    }

    private void loadCoinCount() {
        int coinCount = clicker.getTotalCoinsEarned();
        onUpdateCoins(coinCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicker.loadGameProgressFromDB(this);
        Log.d(TAG, "clicker coins per click" + clicker.getCurrentCoinsPerClick());
        clicker.startAutoClick();
        gameView.refreshPurchasedItems();
        loadCoinCount();
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        boolean autoCoinPurchased = prefs.getBoolean("AutoCoinPurchased", false);

        if (autoCoinPurchased) {
            // activate the auto clicker
            clicker.activateContinuousAutoClickUpgrade(this);
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
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"DB is saving on stop event");
        clicker.saveGameProgressToDB(this);
        clicker.stopAutoClick();
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"DB is saving on destroy event");
        clicker.saveGameProgressToDB(this);
        clicker.stopAutoClick();
        if(clicker.isContinuousClickActive()){
            clicker.stopContinuousClicking();
        }
    }

    @Override
    public void onUpdateCoins(int totalCoins) {
        if (gameView != null) {
            gameView.setCoinCount(totalCoins);
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