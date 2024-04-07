package com.example.singaporeaquacars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StoreActivity extends Activity {

    private static final String TAG = "StoreActivity";
    private Clicker clicker;
    private TextView coinsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        clicker = new Clicker();
        clicker.loadGameProgressFromDB(this);
        clicker.setContinuousClickActive(false);
        int currentCoins = clicker.getTotalCoinsEarned();

        // Update coins display
        coinsTextView = findViewById(R.id.coinsTextView);
        updateCoinsDisplay(currentCoins);

        setUpButtonListeners();
    }

    private void setUpButtonListeners() {
        findViewById(R.id.upgrade_x2_coin).setOnClickListener(v -> handleX2CoinPurchase());
        findViewById(R.id.upgrade_autocoin).setOnClickListener(v -> handleAutoCoinPurchase());
        findViewById(R.id.upgrade_wings1).setOnClickListener(v -> handlePurchase("Wings 1", 1000));
        findViewById(R.id.upgrade_wings2).setOnClickListener(v -> handlePurchase("Wings 2", 1500));
        findViewById(R.id.upgrade_wings3).setOnClickListener(v -> handlePurchase("Wings 3", 2000));
        findViewById(R.id.upgrade_submarine).setOnClickListener(v -> handlePurchase("Submarine", 3000));
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
    }

    private void handleX2CoinPurchase() {
        SharedPreferences prefs = getPreferences();
        int multiplier = prefs.getInt("X2CoinPurchased",1);

        boolean deducted = clicker.deductCoins(30);
        if (deducted && multiplier < 2048) {
            Log.d(TAG,"Calling the upgrade clicker function");
            Log.d(TAG,"Clicker before upgrade "+clicker.getCurrentCoinsPerClick());
            clicker.upgradeClicker(StoreActivity.this);
            // update coins display
            updateCoinsDisplay(clicker.getTotalCoinsEarned());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("X2CoinPurchased",multiplier*2);
            editor.apply();
        } else if (!deducted){
            Log.d(TAG, "Insufficient Coins to deduct for x2 coin upgrade");
            showAlertForInsufficientCoins();

        }  else{
            Log.d(TAG,"X2 is maxed out");
            showAlertForX2MaxedOut();
        }

    }

    private void handleAutoCoinPurchase() {
        boolean deducted = clicker.deductCoins(50);

        if(deducted){
            Log.d(TAG, "Bought the autoclicker upgrade function");

            updateCoinsDisplay(clicker.getTotalCoinsEarned());

            SharedPreferences prefs = getPreferences();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("AutoCoinPurchased", true);
            editor.apply();
            //??
            finish();

        }else{
            Log.d(TAG, "Insufficient Coins to deduct for autoclicker upgrade");
            showAlertForInsufficientCoins();
        }
    }

    private void handlePurchase(String item, int cost) {
        if(clicker.getTotalCoinsEarned() < cost) {
            Log.d(TAG, "Insufficient coins for " + item + ".");
            showAlertForInsufficientCoins();
        }

        if (item.equals("Wings 1")) {
            if (clicker.getTotalCoinsEarned() >= cost) {
                updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);

                SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("CarWingsMiddlePurchased", false);
                editor.putBoolean("CarWingsExPurchased", false);
                editor.putBoolean("CarSubmarinePurchased", false);
                editor.putBoolean("CarWingsCheapestPurchased", true);
                editor.apply();

                clicker.saveGameProgressToDB(StoreActivity.this);
            }
            clicker.upgradeClicker(StoreActivity.this);
            System.out.println("Calling the upgrade clicker function");

            clicker.saveGameProgressToDB(StoreActivity.this);
        } else if (item.equals("Wings 2")) {

            if (clicker.getTotalCoinsEarned() >= cost) {
                updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);

                SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("CarWingsMiddlePurchased", true);
                editor.putBoolean("CarWingsExPurchased", false);
                editor.putBoolean("CarSubmarinePurchased", false);
                editor.putBoolean("CarWingsCheapestPurchased", false);
                editor.apply();

                clicker.saveGameProgressToDB(StoreActivity.this);
            }
            clicker.upgradeClicker(StoreActivity.this);
            System.out.println("Calling the upgrade clicker function");

            clicker.saveGameProgressToDB(StoreActivity.this);
        } else if (item.equals("Wings 3")) {
            if (clicker.getTotalCoinsEarned() >= cost) {
                updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);

                SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("CarWingsMiddlePurchased", false);
                editor.putBoolean("CarWingsExPurchased", true);
                editor.putBoolean("CarSubmarinePurchased", false);
                editor.putBoolean("CarWingsCheapestPurchased", false);
                editor.apply();

                clicker.saveGameProgressToDB(StoreActivity.this);
            }
            clicker.upgradeClicker(StoreActivity.this);
            System.out.println("Calling the upgrade clicker function");

            clicker.saveGameProgressToDB(StoreActivity.this);
        } else{
            if (clicker.getTotalCoinsEarned() >= cost) {
                updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);

                SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("CarWingsMiddlePurchased", false);
                editor.putBoolean("CarWingsExPurchased", false);
                editor.putBoolean("CarSubmarinePurchased", true);
                editor.putBoolean("CarWingsCheapestPurchased", false);
                editor.apply();

                clicker.saveGameProgressToDB(StoreActivity.this);
            }
            clicker.upgradeClicker(StoreActivity.this);
            System.out.println("Calling the upgrade clicker function");

            clicker.saveGameProgressToDB(StoreActivity.this);
        }
    }

    public void updateCoinsDisplay(int coins) {
        coinsTextView.setText("Coins: " + coins);

        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("TotalCoins", coins);
        editor.apply();

        clicker.setTotalCoinsEarned(coins);
        clicker.saveGameProgressToDB(this);
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("GamePrefs", MODE_PRIVATE);
    }

    private void saveGameProgress() {
        clicker.saveGameProgressToDB(this);
    }

    private void showAlertForInsufficientCoins() {
        new AlertDialog.Builder(this)
                .setTitle("Insufficient Coins")
                .setMessage("You do not have enough coins to make this purchase.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showAlertForX2MaxedOut() {
        new AlertDialog.Builder(this)
                .setTitle("X2 Maxed Out")
                .setMessage("You have reached the limit for this upgrade.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Saving Game Progress");
        saveGameProgress();
    }
}