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

    public Clicker() {
        currentCoinsPerClick = 1; // Initial value
        totalCoinsEarned = 0;
        handlerThread = new HandlerThread("AutoClickThread");
        handlerThread.start();
        autoClickHandler = new Handler(handlerThread.getLooper());

        autoClickRunnable = new Runnable() {
            @Override
            public void run() {
                handleClick(); // Automatically click every 15 seconds

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
        totalCoinsEarned += currentCoinsPerClick;
        if (updateListener != null) {
            new Handler(Looper.getMainLooper()).post(() -> updateListener.onUpdateCoins(totalCoinsEarned));
        }
        System.out.println("Clicker clicking at: " + currentCoinsPerClick);
        System.out.println("Clicking updated: " + totalCoinsEarned);
    }

    public void upgradeClicker(Context context) {
        // Double the current coins per click when upgrading
        System.out.println("Attempting to upgrade clicker");
        int upgradeCost = 30;
        if (totalCoinsEarned >= upgradeCost) {
            // Deduct the upgrade cost from the total coins earned
            System.out.println("Upgraded Clicker");
            totalCoinsEarned -= upgradeCost;

            // Double the current coins per click when upgrading
            currentCoinsPerClick *= 2;
        } else {
            // Display a Toast message for insufficient funds
            Toast.makeText(context, "Insufficient coins to upgrade", Toast.LENGTH_SHORT).show();
        }
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
        if (!continuousClickActive) {
            System.out.println("starting autoclicker");
            continuousClickActive = true;
            autoClickHandler.postDelayed(continuousClickRunnable, 1000); // Start immediately

            // Schedule deactivation of continuous clicking after 30 seconds
            autoClickHandler.postDelayed(() -> {
                stopContinuousClicking();
            }, AUTO_CLICK_DURATION);
        }
    }

    public void deductAutoClickerCoin(Context context){
        int upgradeCost = 50;
        if (totalCoinsEarned >= upgradeCost) {
            // Deduct the upgrade cost from the total coins earned
            totalCoinsEarned -= upgradeCost;

            // Activate auto-click upgrade for 30 seconds
        } else {
            // Display a message or handle insufficient funds
            Toast.makeText(context, "Insufficient coins to upgrade", Toast.LENGTH_SHORT).show();
        }
    }
//    public void activateAutoClickUpgrade(Context context) {
//        System.out.println("starting autoclicker");
//
//        final int incrementInterval = 1000; // Increment every second, for example
//        autoClickHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Check if auto-clicker is still active
//                if (autoClickUpgradeActive) {
//                    // Increase coins
//                    totalCoinsEarned += 1; // Increase by 1 or your desired amount
//
//                    // Notify the listener
//                    if (updateListener != null) {
//                        updateListener.onUpdateCoins(totalCoinsEarned);
//                    }
//
//                    // Schedule the next increment
//                    autoClickHandler.postDelayed(this, incrementInterval);
//                }
//            }
//        }, incrementInterval);
//
//        autoClickHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                deactivateAutoClickUpgrade();
//            }
//        }, AUTO_CLICK_DURATION);
//
//    }

    public void stopContinuousClicking() {
        if (continuousClickActive) {
            System.out.println("Stopping autoclicker upgrade");
            autoClickHandler.removeCallbacks(continuousClickRunnable);
            continuousClickActive = false;
        }
    }

    public boolean isContinuousClickActive() {
        return continuousClickActive;
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
