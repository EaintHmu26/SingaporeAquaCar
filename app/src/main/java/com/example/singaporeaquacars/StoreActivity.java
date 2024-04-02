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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.singaporeaquacars.Clicker;

import org.w3c.dom.Text;

public class StoreActivity extends Activity {

    private static final String TAG = "StoreActivity";

    private Clicker clicker;
    private TextView coinsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clicker = new Clicker();
        clicker.loadGameProgressFromDB(this);
        int currentCoins = clicker.getTotalCoinsEarned();
        //currentCoins = 50;

        // Create a FrameLayout as the root layout
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Create and set up the ImageView for the background
        ImageView backgroundImageView = new ImageView(this);
        backgroundImageView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // maintain the aspect ratio
        backgroundImageView.setImageResource(R.drawable.store_background);

        // Add the ImageView to the FrameLayout
        frameLayout.addView(backgroundImageView);

        // Create the ScrollView which will contain your mainLayout
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Create the main layout that contains your UI elements
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Define padding and imageSize
        int padding = (int) (25 * getResources().getDisplayMetrics().density);
        int imageSize = (int) (0.5 * getResources().getDisplayMetrics().widthPixels); // smaller size for icons

        // Create and set up the TextView for the coin count at the top.
        coinsTextView = new TextView(this);
        coinsTextView.setText("Coins: " + currentCoins);
        coinsTextView.setTextSize(20);
        coinsTextView.setTextColor(Color.WHITE); // Set a color that contrasts with your background.
        coinsTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams coinsTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        coinsTextParams.gravity = Gravity.CENTER_HORIZONTAL;
        coinsTextParams.setMargins(0, padding, 0, padding);
        coinsTextView.setLayoutParams(coinsTextParams);

        // Add the TextView to the main layout before the upgrade sections.
        mainLayout.addView(coinsTextView);

        mainLayout.addView(createUpgradeSection(
                R.drawable.x2_coin_icon, "x2 Coin - $30", imageSize, padding));
        mainLayout.addView(createUpgradeSection(
                R.drawable.autocoin_icon, "Auto Coin - $50", imageSize, padding));
        mainLayout.addView(createUpgradeSection(
                R.drawable.car_wings_cheapest,"Wings 1 - $1000", imageSize, padding));
        mainLayout.addView(createUpgradeSection(
                R.drawable.car_wings_middle,"Wings 2 - $1500", imageSize, padding));
        mainLayout.addView(createUpgradeSection(
                R.drawable.car_wings_ex, "Wings 3 - $2000", imageSize, padding));
        mainLayout.addView(createUpgradeSection(
                R.drawable.car_submarine, "Submarine - $3000", imageSize, padding));

        // ... add your UI elements to mainLayout ...

        // Add the ScrollView containing your mainLayout to the FrameLayout
        scrollView.addView(mainLayout);
        frameLayout.addView(scrollView);

        // Finally, set the FrameLayout as the content view
        setContentView(frameLayout);

        // Add the back button dynamically
        addButtonToLayout(mainLayout);
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        clicker = new Clicker();
//
//        // The main layout for the activity
//        LinearLayout mainLayout = new LinearLayout(this);
//        mainLayout.setOrientation(LinearLayout.VERTICAL);
//        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//
//        mainLayout.setBackgroundResource(R.drawable.store_background);
//
//
//        // Define padding and imageSize
//        int padding = (int) (16 * getResources().getDisplayMetrics().density);
//        int imageSize = (int) (80 * getResources().getDisplayMetrics().density); // smaller size for icons
//
//        // Add all upgrade sections to the mainLayout
//        mainLayout.addView(createUpgradeSection(
//                R.drawable.x2_coin_icon, "x2 Coin - $30", imageSize, padding));
//        mainLayout.addView(createUpgradeSection(
//                R.drawable.autocoin_icon, "Auto Coin - $50", imageSize, padding));
//        mainLayout.addView(createUpgradeSection(
//                R.drawable.car_wings_cheapest,"Wings 1 - $1000", imageSize, padding));
//        mainLayout.addView(createUpgradeSection(
//                R.drawable.car_wings_middle,"Wings 2 - $1500", imageSize, padding));
//        mainLayout.addView(createUpgradeSection(
//                R.drawable.car_wings_ex, "Wings 3 - $2000", imageSize, padding));
//
//        // Now create the ScrollView and add the mainLayout to it
//        ScrollView scrollView = new ScrollView(this);
//        scrollView.addView(mainLayout);
//
//
//
//        // Finally, set the main layout as the content view
//        setContentView(scrollView);
//
//        // Add the back button dynamically
//        addButtonToLayout(mainLayout);
//    }

    private void addButtonToLayout(LinearLayout layout) {
        // Create the back button
        Button backButton = new Button(this);
        backButton.setText("Back to Game");

        // Set layout parameters for the button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = 16 * (int) getResources().getDisplayMetrics().density;
        backButton.setLayoutParams(params);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Add the button to the layout
        layout.addView(backButton);
    }

    private LinearLayout createUpgradeSection(int drawableId, String buttonText, int imageSize, int padding) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, padding); // Add some margin below each section
        layout.setLayoutParams(layoutParams);

        // Create and set up the ImageView
        ImageView imageView = new ImageView(this);
        // Set a light gray background color to the ImageView
        //imageView.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
        imageView.setImageResource(drawableId);
        // Set larger dimensions for the ImageView
        int imageWidth = (int) (120 * getResources().getDisplayMetrics().density); // 120dp
        int imageHeight = (int) (120 * getResources().getDisplayMetrics().density); // 120dp, you can adjust this to keep the aspect ratio if needed
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
        imageParams.setMargins(0, 0, 0, padding / 2); // Add some margin below the ImageView
        imageView.setLayoutParams(imageParams);

        layout.addView(imageView);

        // Create and set up the Button
        Button button = new Button(this);
        button.setText(buttonText);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Check if the current button is the autocoin button
        if (drawableId == R.drawable.autocoin_icon) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle autocoin button click here
                    // Call the method specific to autocoin button
                    clicker.activateAutoClickUpgrade(StoreActivity.this);

                    // Update coins display
                    updateCoinsDisplay(clicker.getTotalCoinsEarned());

                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("AutoCoinPurchased", true);

                    editor.apply();

                    clicker.saveGameProgressToDB(StoreActivity.this);

                }
            });
        } else if (drawableId == R.drawable.x2_coin){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicker.upgradeClicker(StoreActivity.this);

                    // Update coins display
                    updateCoinsDisplay(clicker.getTotalCoinsEarned());

                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("X2CoinPurchased", true);

                    editor.apply();

                    clicker.saveGameProgressToDB(StoreActivity.this);
                }
            });

        } else if (drawableId == R.drawable.car_wings_cheapest){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("CarWingsCheapestPurchased", true);

                    editor.apply();


                }
            });

        } else if (drawableId == R.drawable.car_wings_middle){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("CarWingsMiddlePurchased", true);

                    editor.apply();


                }
            });

        } else if (drawableId == R.drawable.car_wings_ex){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("CarWingsExPurchased", true);

                    editor.apply();


                }
            });

        } else if (drawableId == R.drawable.car_submarine){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("CarSubmarinePurchased", true);

                    editor.apply();


                }
            });

        }

        layout.addView(button);

        return layout;
    }

    public void updateCoinsDisplay(int coins) {
        coinsTextView.setText("Coins: " + coins);
    }

    private void saveGameProgress() {
        clicker.saveGameProgressToDB(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "saving game progress");
        saveGameProgress();
    }


}
