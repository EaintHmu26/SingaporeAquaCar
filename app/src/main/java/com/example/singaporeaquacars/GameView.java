package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;


import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class GameView extends View {
    private ScheduledExecutorService executorService;
    Bitmap home_bkgrd_platform, car, scaledBg;
    static int dWidth, dHeight;
    Bitmap storeIcon;
    ArrayList<PurpleFish> purpleFish;
    ArrayList<Shark> shark;
    Handler handler;
    Runnable runnable;
    static int carWidth, carHeight;
    Context context;
    int coinCount;
    Paint coinCountPaint;
    final int TEXT_SIZE = 60;
    SharedPreferences prefs;
    boolean showPlusOne = false;
    float plusOneX = 0, plusOneY = 0; //position of +1
    int plusOneAlpha = 255; //fading effect
    Paint plusOnePaint;
    private OnCoinCountChangeListener coinCountChangeListener;
    private static final int REQUEST_CODE_STORE = 1;

    private Clicker clicker;




    public GameView(Context context){
        super(context);
        this.context = context;

        clicker = new Clicker();
        clicker.loadGameProgressFromDB(this.getContext());

        home_bkgrd_platform = BitmapFactory.decodeResource(getResources(), R.drawable.home_bkgrd_platform);
        Log.d("GameView", "Background loaded");

        storeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.store);

        // Scale down the store icon
        int iconWidth = 500; // set the width you want for the icon
        float aspectRatio1 = (float) storeIcon.getHeight() / (float) storeIcon.getWidth();
        int iconHeight = (int) (iconWidth * aspectRatio1);
        storeIcon = Bitmap.createScaledBitmap(storeIcon, iconWidth, iconHeight, false);

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, dWidth, dHeight, true);

        purpleFish = new ArrayList<>();
        shark = new ArrayList<Shark>();
        initializeFish();

        loadPurchasedItems();

        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
        }
        //i only want to add one shark
        Shark ashark = new Shark(context, dWidth, dHeight);
        shark.add(ashark);
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // Triggers onDraw
                handler.postDelayed(this, 30); // Adjust FRAME_DELAY as needed
            }
        };
        handler.post(runnable); // Start the drawing loop

        // Resize the car as specified
        float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
        carWidth = 4 * dWidth / 5;
        carHeight = (int) (carWidth * aspectRatio);
        car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);

        coinCountPaint = new Paint();
        coinCountPaint.setColor(Color.WHITE);
        coinCountPaint.setTextSize(TEXT_SIZE);
        coinCountPaint.setAntiAlias(true);

        prefs = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        coinCount = prefs.getInt("coinCount", 0);

        plusOnePaint = new Paint();
        plusOnePaint.setColor(Color.YELLOW);
        plusOnePaint.setTextSize(TEXT_SIZE * 1.5f);
        plusOnePaint.setAntiAlias(true);

        startFishSpeedUpdateTask();
    }

    private void startFishSpeedUpdateTask() {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.d("GameView", "Updating shark speed"); // Logging for debugging

                for (Shark shark : shark) {
                    // Randomly adjust the speed of each fish
                    float newSpeed = 1 + new Random().nextFloat() * (5 - 1); // Example: speed range [1, 5]
                    shark.setSpeed(newSpeed);
                    Log.d("GameView", "Fish speed updated: NewSpeed=" + newSpeed);
                }

                // Make sure to post any UI updates to the main thread
                postInvalidate();
            }
        }, 0, 3, TimeUnit.SECONDS); // Example: adjust every 10 seconds
    }

    private void updateCarImage() {
        // Resize the car as specified
        if(car != null) { // Check to make sure the car bitmap is not null
            float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
            carWidth = 4 * dWidth / 5; // Example resizing logic, adjust as necessary
            carHeight = (int) (carWidth * aspectRatio);
            car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);
        }
        invalidate(); // Redraw the view to reflect changes
    }

    private void loadPurchasedItems() {
        SharedPreferences gamePrefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);

        Log.d("GameView", "Loading purchased items to determine which car to display");

        if (gamePrefs.getBoolean("CarSubmarinePurchased", false)) {
            Log.d("GameView", "Submarine purchased");
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car_submarine);
        } else if (gamePrefs.getBoolean("CarWingsExPurchased", false)) {
            Log.d("GameView", "Wings Ex purchased");
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car_wings_ex);
        } else if (gamePrefs.getBoolean("CarWingsMiddlePurchased", false)) {
            Log.d("GameView", "Wings Middle purchased");
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car_wings_middle);
        } else if (gamePrefs.getBoolean("CarWingsCheapestPurchased", false)) {
            Log.d("GameView", "Wings Cheapest purchased");
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car_wings_cheapest);
        } else {
            Log.d("GameView", "No car purchased, using default");
            car = BitmapFactory.decodeResource(getResources(), R.drawable.original_car);
        }
        // Apply any necessary adjustments to the car's dimensions
        updateCarImage();
    }

    public void refreshPurchasedItems() {
        loadPurchasedItems();

        // Resize the car as specified
        float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
        carWidth = 4 * dWidth / 5;
        carHeight = (int) (carWidth * aspectRatio);
        car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);

        invalidate(); // Redraw the view to reflect changes
    }

    public interface OnCoinCountChangeListener {
        void onCoinCountChanged(int newCoinCount);
    }

    public void setOnCoinCountChangeListener(OnCoinCountChangeListener listener) {
        this.coinCountChangeListener = listener;
    }

    public void saveCoinCount() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coinCount", coinCount);
        editor.apply();
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
        saveCoinCount(); // Save the updated coin count
        postInvalidate(); // Redraw the view with the updated coin count
    }

    // Load bitmap with error checking
    private Bitmap loadBitmapSafe(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (bitmap == null) {
            Log.e("GameView", "Error loading bitmap resource ID: " + resId);
        }
        return bitmap;
    }

    // Efficient bitmap scaling
    private Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        return bitmap != null ? Bitmap.createScaledBitmap(bitmap, width, height, true) : null;
    }

    // Initialization of Fish Objects
    private void initializeFish() {
        purpleFish.clear();
        Log.d("GameView", "Initializing fish");
        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
            //Log.d("GameView", "PurpleFish added");

        }
        //i just want to add one shark
        Shark ashark = new Shark(context, dWidth, dHeight);
        shark.add(ashark);
        //Log.d("GameView", "shark added");

    }

    private void startPlusOneAnimation() {
        final int animationDuration = 500; // Animation duration in milliseconds
        final int frameRate = 30; // How often to update the animation
        final float deltaY = -30; // How much the text moves up
        final Handler animationHandler = new Handler(Looper.getMainLooper());
        final long startTime = System.currentTimeMillis();

        Runnable animationRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                float fraction = elapsedTime / (float) animationDuration;
                if (fraction <= 1.0) {
                    // Update position and alpha
                    plusOneY += deltaY * fraction;
                    plusOneAlpha = 255 - (int)(255 * fraction);
                    plusOnePaint.setAlpha(plusOneAlpha);
                    invalidate(); // Redraw to show animation progress
                    animationHandler.postDelayed(this, frameRate);
                } else {
                    // Animation end
                    showPlusOne = false;
                }
            }
        };
        animationHandler.post(animationRunnable);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //Log.d("GameView", "onDraw called");

        if (scaledBg.getWidth() != getWidth() || scaledBg.getHeight() != getHeight()) {
            scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, getWidth(), getHeight(), true);
        }

        canvas.drawBitmap(scaledBg, 0, 0, null);


        //Log.d("GameView", "PurpleFish size: " + purpleFish.size());
//        Log.d("GameView", "RobotFish size: " + robotFish.size());
        //Log.d("GameView", "Shark size: " + shark.size());

        for (PurpleFish fish : purpleFish) {
            fish.update();  // Update position and check for looping
//            Log.d("GameView", "Drawing fish: X=" + fish.getX() + ", Y=" + fish.getY() + ", Speed=" + fish.getSpeed());

            canvas.drawBitmap(fish.getBitmap(), fish.getX(), fish.getY(), null); // Draw fish
        }
        for (Shark sharks : shark) {
            sharks.update();  // Update position and check for looping
            canvas.drawBitmap(sharks.getBitmap(), sharks.getX(), sharks.getY(), null); // Draw fish
        }
        invalidate(); // Keep the loop going

        int carX = dWidth / 2 - carWidth / 2;
        int carY = (int) (dHeight / 2 - carHeight / 2 + dHeight * 0.07); // Adjust this factor to move the car down
        canvas.drawBitmap(car, carX, carY, null);

        if (showPlusOne) {
            canvas.drawText("+1", plusOneX, plusOneY, plusOnePaint);
        }

        // Draw the store icon at a specific position, e.g., top-right corner
        int iconX = dWidth - storeIcon.getWidth() - 30; // 50 is a margin from the right edge
        int iconY = 30; // 50 is a margin from the top edge
        canvas.drawBitmap(storeIcon, iconX, iconY, null);

        // Display the coin count
        canvas.drawText("Coins: $" + coinCount, 20, TEXT_SIZE + 20, coinCountPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            // Check if the store icon is clicked
            int storeIconX = dWidth - storeIcon.getWidth() - 30; // 30px margin from the right edge
            int storeIconY = 30; // 30px margin from the top edge
            int storeIconEndX = storeIconX + storeIcon.getWidth();
            int storeIconEndY = storeIconY + storeIcon.getHeight();

            if (touchX >= storeIconX && touchX <= storeIconEndX && touchY >= storeIconY && touchY <= storeIconEndY) {
                // The store icon was clicked
                openStore();
                return true;
            }

            // Calculate the car's position
            int carX = dWidth / 2 - carWidth / 2;
            // Adjust the carY variable to match the Y position where the car image actually starts
            int carY = (int) (dHeight / 2 - carHeight / 2 + dHeight * 0.07); // Adjust this factor to move the car down
            // Check if the touch is within the bounds of the car image
            if (touchX >= carX && touchX < (carX + carWidth) && touchY >= carY && touchY < (carY + carHeight)) {
                coinCount += clicker.getCurrentCoinsPerClick(); // Increment coin count only if the car is touched
                Log.d("GameView", "clicker count" + clicker.getCurrentCoinsPerClick());
                saveCoinCount();
                showPlusOne = true;
                plusOneX = carX + carWidth / 2;
                plusOneY = carY;
                plusOneAlpha = 255;
                startPlusOneAnimation();
                invalidate(); // Redraw to show the updated coin count
                if(coinCountChangeListener != null) {
                    coinCountChangeListener.onCoinCountChanged(coinCount);
                }
            }
        }
        return true;
    }

    private void openStore() {
        if (context instanceof Activity) {
            // Intent to start StoreActivity
            Intent intent = new Intent(context, StoreActivity.class);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_STORE);
        } else {
            // Handle the error condition or throw an exception
            Log.e("GameView", "Context used is not an Activity context");
        }
    }
}




