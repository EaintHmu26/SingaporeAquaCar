package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Clicker.ClickerUpdateListener {

        private Clicker clicker;
        private TextView coinsTextView;

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
            setContentView(rootLayout);

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

            clicker.startAutoClick();
        }

        @Override
        public void onUpdateCoins(int totalCoins) {
            updateCoinsTextView(totalCoins); // Call a method to update the TextView with the new total
        }

        private void updateCoinsTextView(int totalCoins) {
            coinsTextView.setText(String.valueOf(totalCoins));
        }
        @Override
        protected void onResume() {
            super.onResume();
            clicker.loadGameProgressFromDB(this); // Refresh game progress
            updateCoinsTextView(clicker.getTotalCoinsEarned());
        }
        @Override
        protected void onPause() {
            super.onPause();
            System.out.println("DB is saving on pause event");
            clicker.saveGameProgressToDB(this);
            clicker.stopAutoClick();
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
        }
        public void startGame (GameView view){}
    }