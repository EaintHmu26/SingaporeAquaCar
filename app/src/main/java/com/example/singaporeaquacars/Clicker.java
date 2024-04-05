package com.example.singaporeaquacars;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Clicker {
    private static final String TAG = "Clicker";
    private int currentCoinsPerClick;
    private int totalCoinsEarned;
    private HandlerThread handlerThread;
    private Handler autoClickHandler;
    private Runnable autoClickRunnable;
    public static final int AUTO_CLICK_INTERVAL = 15000; // 15 seconds
    private static final int AUTO_CLICK_DURATION = 30000; // 30 seconds

    private boolean continuousClickActive = false;
    private Runnable continuousClickRunnable;
    private ClickerUpdateListener updateListener;
    private final Object lock = new Object();
    private volatile boolean pauseAutoClicker = false;

    public Clicker() {
        currentCoinsPerClick = 1; // Initial value
        totalCoinsEarned = 0;
        handlerThread = new HandlerThread("AutoClickThread");
        handlerThread.start();
        autoClickHandler = new Handler(handlerThread.getLooper());

        autoClickRunnable = new Runnable() {
            @Override
            public void run() {
                handleAutoClick(); // Automatically click every 15 seconds

                if (updateListener != null) {
                    // Use the main thread to inform the UI
                    new Handler(Looper.getMainLooper()).post(() -> updateListener.onUpdateCoins(totalCoinsEarned));
                }

                autoClickHandler.postDelayed(this, AUTO_CLICK_INTERVAL);
            }
        };
        continuousClickRunnable = new Runnable() {
            @Override
            public void run() {
                if (continuousClickActive) {
                    handleClick(); // This method increases the coins

                    // Reschedule the continuous click every second
                    autoClickHandler.postDelayed(this, 1000); // 1 second for continuous click
                }
            }
        };
    }

    public void handleClick() {
        synchronized (lock){
            int total = totalCoinsEarned + currentCoinsPerClick;
            if(total >1000000){
                Log.d(TAG,"Coins stopped accumulating");
                if (updateListener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> updateListener.onUpdateCoins(1000000));
                };
            }else{
                totalCoinsEarned = total;
                if (updateListener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> updateListener.onUpdateCoins(totalCoinsEarned));
                };
                Log.d(TAG, "Clicker clicking at: " + currentCoinsPerClick);
                Log.d(TAG, "Clicking updated: " + totalCoinsEarned);
            }
        }
    }

    private void handleAutoClick(){
        synchronized (lock){
            if(!pauseAutoClicker){
                handleClick();
            }else{
                Log.d(TAG,"auto click is paused");
            }
        }
    }

    public void upgradeClicker(Context context) {
            if(currentCoinsPerClick < 2048){
                // Double the current coins per click when upgrading
                Log.d(TAG, "before upgrade clicker "+currentCoinsPerClick);
                currentCoinsPerClick *= 2;
                Log.d(TAG, "Upgraded Clicker " + currentCoinsPerClick);
            }else{
                Log.d(TAG, "Maximum upgrade attained");
                totalCoinsEarned+=30;
            }
    }

    public boolean deductCoins(int amount){
        Log.d(TAG, "Attempting to deduct coins");
        boolean deduct = false;
        if (totalCoinsEarned >= amount) {
            // Deduct the upgrade cost from the total coins earned
            totalCoinsEarned -= amount;
            deduct = true;
        }
        return deduct;
    }

    public void startAutoClick() {
        autoClickHandler.postDelayed(autoClickRunnable, AUTO_CLICK_INTERVAL);
    }

    public void stopAutoClick() {
        autoClickHandler.removeCallbacks(autoClickRunnable);
    }

    public interface ClickerUpdateListener {
        void onUpdateCoins(int totalCoins);
    }

    public void activateContinuousAutoClickUpgrade(Context context) {
        synchronized (lock){
            if (!continuousClickActive) {
                Log.d(TAG,"starting autoclicker");
                continuousClickActive = true;
                pauseAutoClicker = true;
                autoClickHandler.postDelayed(continuousClickRunnable, 1000); // Start immediately

                // Schedule deactivation of continuous clicking after 30 seconds
                autoClickHandler.postDelayed(this::stopContinuousClicking, AUTO_CLICK_DURATION);
            }
        }
    }

    public void stopContinuousClicking() {
        synchronized (lock){
            if (continuousClickActive) {
                System.out.println("Stopping autoclicker upgrade");
                autoClickHandler.removeCallbacks(continuousClickRunnable);
                continuousClickActive = false;
                pauseAutoClicker = false;
            }
        }
    }


    public boolean isContinuousClickActive() {
        return continuousClickActive;
    }

    public void setContinuousClickActive(boolean active){
        this.continuousClickActive = active;
    }
    // Getter and setter methods for currentCoinsPerClick and totalCoinsEarned
    public int getCurrentCoinsPerClick() {
        return currentCoinsPerClick;
    }

    public void setCurrentCoinsPerClick(int currentCoinsPerClick) {
        this.currentCoinsPerClick = currentCoinsPerClick;
    }
    public void setUpdateListener(ClickerUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public int getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setTotalCoinsEarned(int totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
    }


    public void saveGameProgressToDB(Context context) {
        ClickerDbHelper dbHelper = new ClickerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("totalCoinsEarned", totalCoinsEarned);
        values.put("currentCoinsPerClick", currentCoinsPerClick);
        values.put("autoClickUpgradeActive", continuousClickActive ? 1 : 0);
        Log.d("Clicker", "Saving to DB: Total Coins: " + totalCoinsEarned + ", Coins Per Click: " + currentCoinsPerClick);
        // Rest of the save code...

        // Assuming you're always updating the same row, or you could use db.insertWithOnConflict with a unique identifier
        // db.update("game_progress", values, "id = ?", new String[] {"1"});
        try {
            int rowsAffected = db.update("game_progress", values, "id = ?", new String[] {"1"});
            Log.d("Clicker", "Rows affected by update: " + rowsAffected);
        } catch (Exception e) {
            Log.e("Clicker", "Error updating database", e);
        }
        db.close();
    }
    public void loadGameProgressFromDB(Context context) {
        ClickerDbHelper dbHelper = new ClickerDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "game_progress",   // The table to query
                new String[]{"totalCoinsEarned", "currentCoinsPerClick", "autoClickUpgradeActive"}, // The columns to return
                "id = ?",          // The columns for the WHERE clause
                new String[]{"1"}, // The values for the WHERE clause
                null,              // don't group the rows
                null,              // don't filter by row groups
                null               // The sort order
        );
        Log.d("Clicker", "Loaded from DB: Total Coins: " + totalCoinsEarned + ", Coins Per Click: " + currentCoinsPerClick);

        if (cursor.moveToFirst()) {
            int totalCoinsEarnedIndex = cursor.getColumnIndex("totalCoinsEarned");
            int currentCoinsPerClickIndex = cursor.getColumnIndex("currentCoinsPerClick");
            int autoClickUpgradeActiveIndex = cursor.getColumnIndex("autoClickUpgradeActive");

            if (totalCoinsEarnedIndex != -1) {
                totalCoinsEarned = cursor.getInt(totalCoinsEarnedIndex);
                Log.d("Clicker", "Data loaded successfully from DB: total coins earned : " + totalCoinsEarnedIndex + " " + totalCoinsEarned);
            }

            if (currentCoinsPerClickIndex != -1) {
                currentCoinsPerClick = cursor.getInt(currentCoinsPerClickIndex);
                Log.d("Clicker", "Data loaded successfully from DB: current coins per click  : " + currentCoinsPerClickIndex + " " + currentCoinsPerClick);
            }

            if (autoClickUpgradeActiveIndex != -1) {
                continuousClickActive = cursor.getInt(autoClickUpgradeActiveIndex) == 1;
                Log.d("Clicker", "Data loaded successfully from DB:  autoclick upgrade active ? : " + autoClickUpgradeActiveIndex + " " + continuousClickActive);
            }
        }
        cursor.close();
        db.close();
    }
}
