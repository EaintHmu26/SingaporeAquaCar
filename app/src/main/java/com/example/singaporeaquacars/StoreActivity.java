package com.example.singaporeaquacars;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singaporeaquacars.Clicker;

import org.w3c.dom.Text;

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
        clicker.setContinuousClickActive(0);
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

        boolean deducted = clicker.deductCoins(30);
        if (deducted) {
            Log.d(TAG,"Calling the upgrade clicker function");
            Log.d(TAG,"Clicker before upgrade "+clicker.getCurrentCoinsPerClick());
            clicker.upgradeClicker(StoreActivity.this);
            // update coins display
            updateCoinsDisplay(clicker.getTotalCoinsEarned());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("X2CoinPurchased", true);
            editor.apply();
        } else {
            Log.d(TAG, "Insufficient Coins to deduct for x2 coin upgrade");
        }
    }

    private void handleAutoCoinPurchase() {
        boolean deducted = clicker.deductCoins(50);

        if(deducted){
            Log.d(TAG, "Bought the autoclicker upgrade function");

            // Update coins display
            updateCoinsDisplay(clicker.getTotalCoinsEarned());

            SharedPreferences prefs = getPreferences();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("AutoCoinPurchased", true);
            editor.apply();
            //??
            finish();

        }else{
            Log.d(TAG, "Insufficient Coins to deduct for autoclicker upgrade");
        }
    }

    private void handlePurchase(String item, int cost) {
        if(clicker.getTotalCoinsEarned() < cost) {
            Log.d(TAG, "Insufficient coins for " + item + ".");
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
            // Update coins display
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
            // Update coins display
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
            // Update coins display
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
            // Update coins display
            clicker.saveGameProgressToDB(StoreActivity.this);
        }
    }










//    private void setPurchaseFlag(String key) {
//        SharedPreferences prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
//        prefs.edit().putBoolean(key, true).apply();
//    }




//    private LinearLayout createUpgradeSection(int drawableId, String buttonText, int imageSize, int padding) {
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setGravity(Gravity.CENTER_HORIZONTAL);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams.setMargins(0, 0, 0, padding); // Add some margin below each section
//        layout.setLayoutParams(layoutParams);
//
//        // Create and set up the ImageView
//        ImageView imageView = new ImageView(this);
//        // Set a light gray background color to the ImageView
//        //imageView.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
//        imageView.setImageResource(drawableId);
//        // Set larger dimensions for the ImageView
//        int imageWidth = (int) (120 * getResources().getDisplayMetrics().density); // 120dp
//        int imageHeight = (int) (120 * getResources().getDisplayMetrics().density); // 120dp, you can adjust this to keep the aspect ratio if needed
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
//        imageParams.setMargins(0, 0, 0, padding / 2); // Add some margin below the ImageView
//        imageView.setLayoutParams(imageParams);
//        layout.addView(imageView);
//
//        // Create and set up the Button
//        Button button = new Button(this);
//        button.setText(buttonText);
//        button.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        ));
//
//        // Specific logic for car purchase buttons
//        if (drawableId == R.drawable.car_wings_cheapest ||
//                drawableId == R.drawable.car_wings_middle ||
//                drawableId == R.drawable.car_wings_ex ||
//                drawableId == R.drawable.car_submarine) {
//            // Car purchase logic
//            button.setOnClickListener(v -> {
//                Log.d(TAG, "Attempting to purchase: " + buttonText);
//                purchaseCar(getCarKeyByDrawableId(drawableId));
//            });
//        } else {
//            // Other upgrade logic (x2 coin, autocoin, etc.)
//            button.setOnClickListener(v -> {
//                // Implement logic for non-car upgrades here
//                handleNonCarUpgrade(drawableId);
//            });
//        }
//
//        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        // Check if the current button is the autocoin button
//        if (drawableId == R.drawable.autocoin_icon) {
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Handle autocoin button click here
//                    // Call the method specific to autocoin button
//                    boolean deducted = clicker.deductCoins(50);
//
//                    if(deducted){
//                        Log.d(TAG, "Bought the autoclicker upgrade function");
//
//                        // Update coins display
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned());
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("AutoCoinPurchased", true);
//                        editor.apply();
//
//                    }else{
//                        Log.d(TAG, "Insufficient Coins to deduct for autoclicker upgrade");
//                    }
//                }
//            });
//        } else if (drawableId == R.drawable.x2_coin_icon){
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean deducted = clicker.deductCoins(30);
//                    if(deducted){
//                        Log.d(TAG,"Calling the upgrade clicker function");
//                        clicker.upgradeClicker(StoreActivity.this);
//                        // Update coins display
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned());
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//
//                        editor.putBoolean("X2CoinPurchased", true);
//
//                        editor.apply();
//                    }else{
//                        Log.d(TAG, "Insufficient Coins to deduct for x2 coin upgrade");
//                    }
//                }
//            });
//
//        } else if (drawableId == R.drawable.car_wings_cheapest){
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    int cost = 1000;
//                    if (clicker.getTotalCoinsEarned() >= cost) {
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("CarWingsMiddlePurchased", false);
//                        editor.putBoolean("CarWingsExPurchased", false);
//                        editor.putBoolean("CarSubmarinePurchased", false);
//                        editor.putBoolean("CarWingsCheapestPurchased", true);
//                        editor.apply();
//
//                        clicker.saveGameProgressToDB(StoreActivity.this);
//                    }
//                    clicker.upgradeClicker(StoreActivity.this);
//                    System.out.println("Calling the upgrade clicker function");
//                    // Update coins display
//                    clicker.saveGameProgressToDB(StoreActivity.this);
//                }
//            });
//
//        } else if (drawableId == R.drawable.car_wings_middle){
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    int cost = 1500;
//                    if (clicker.getTotalCoinsEarned() >= cost) {
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("CarWingsMiddlePurchased", true);
//                        editor.putBoolean("CarWingsExPurchased", false);
//                        editor.putBoolean("CarSubmarinePurchased", false);
//                        editor.putBoolean("CarWingsCheapestPurchased", false);
//                        editor.apply();
//
//                        clicker.saveGameProgressToDB(StoreActivity.this);
//                    }
//                    clicker.upgradeClicker(StoreActivity.this);
//                    System.out.println("Calling the upgrade clicker function");
//                    // Update coins display
//                    clicker.saveGameProgressToDB(StoreActivity.this);
//                }
//            });
//
//        } else if (drawableId == R.drawable.car_wings_ex){
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int cost = 2000;
//                    if (clicker.getTotalCoinsEarned() >= cost) {
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("CarWingsMiddlePurchased", false);
//                        editor.putBoolean("CarWingsExPurchased", true);
//                        editor.putBoolean("CarSubmarinePurchased", false);
//                        editor.putBoolean("CarWingsCheapestPurchased", false);
//                        editor.apply();
//
//                        clicker.saveGameProgressToDB(StoreActivity.this);
//                    }
//                    clicker.upgradeClicker(StoreActivity.this);
//                    System.out.println("Calling the upgrade clicker function");
//                    // Update coins display
//                    clicker.saveGameProgressToDB(StoreActivity.this);
//                }
//            });
//
//        } else if (drawableId == R.drawable.car_submarine){
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int cost = 3000;
//                    if (clicker.getTotalCoinsEarned() >= cost) {
//                        updateCoinsDisplay(clicker.getTotalCoinsEarned() - cost);
//
//                        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("CarWingsMiddlePurchased", false);
//                        editor.putBoolean("CarWingsExPurchased", false);
//                        editor.putBoolean("CarSubmarinePurchased", true);
//                        editor.putBoolean("CarWingsCheapestPurchased", false);
//                        editor.apply();
//
//                        clicker.saveGameProgressToDB(StoreActivity.this);
//                    }
//                    clicker.upgradeClicker(StoreActivity.this);
//                    System.out.println("Calling the upgrade clicker function");
//                    // Update coins display
//                    clicker.saveGameProgressToDB(StoreActivity.this);
//                }
//            });
//
//        }
//
//        layout.addView(button);
//
//        return layout;
//    }

//    private String getCarKeyByDrawableId(int drawableId) {
//        if (drawableId == R.drawable.car_wings_cheapest) {
//            return "CarWingsCheapestPurchased";
//        } else if (drawableId == R.drawable.car_wings_middle) {
//            return "CarWingsMiddlePurchased";
//        } else if (drawableId == R.drawable.car_wings_ex) {
//            return "CarWingsExPurchased";
//        } else if (drawableId == R.drawable.car_submarine) {
//            return "CarSubmarinePurchased";
//        } else {
//            return "";
//        }
//    }

//    private void purchaseCar(String purchasedCarKey) {
//        SharedPreferences prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        // Set the selected car as purchased
//        editor.putBoolean(purchasedCarKey, true);
//        editor.apply();
//
//        Log.d(TAG, purchasedCarKey + " now set to purchased");
//
//        // Optional: Save game progress or update UI as needed
//        updateCoinsDisplay(clicker.getTotalCoinsEarned());
//        clicker.saveGameProgressToDB(this);
//    }

//    private void handleNonCarUpgrade(int drawableId) {
//        // This method should contain the logic for handling non-car upgrades
//        // For example, handling autocoin or x2 coin purchase
//        // Remember to save any changes to SharedPreferences and update necessary UI elements
//    }

    public void updateCoinsDisplay(int coins) {
        // Update the TextView to show the current number of coins
        coinsTextView.setText("Coins: " + coins);

        // Save the updated coin count to SharedPreferences for persistence
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("TotalCoins", coins);
        editor.apply();

        // Additionally, since your Clicker class seems to manage game state,
        // ensure the Clicker object's totalCoinsEarned field is also updated.
        clicker.setTotalCoinsEarned(coins); // Assuming you have this setter method

        // Optionally, if you're using the Clicker object to persist data,
        // consider saving the game state here as well.
        clicker.saveGameProgressToDB(this); // This would save to your database or SharedPreferences, wherever your game state is managed.
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("GamePrefs", MODE_PRIVATE);
    }


    private void saveGameProgress() {
        clicker.saveGameProgressToDB(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Saving Game Progress");
        saveGameProgress();
    }
}
