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

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import androidx.annotation.NonNull;

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
    float plusOneX = 0, plusOneY = 0; //position of + animation
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

        int iconWidth = 500;
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
        shark = new ArrayList<>();
        initializeFish();

        loadPurchasedItems();

        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
        }
        Shark ashark = new Shark(context, dWidth, dHeight);
        shark.add(ashark);
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, 30);
            }
        };
        // drawing loop
        handler.post(runnable);

        // car size
        float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
        carWidth = 4 * dWidth / 5;
        carHeight = (int) (carWidth * aspectRatio);
        car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);

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
                for (Shark shark : shark) {
                    // Randomly adjust the speed of each fish
                    float newSpeed = 1 + new Random().nextFloat() * (5 - 1);
                    shark.setSpeed(newSpeed);
                }

                postInvalidate();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void updateCarImage() {
        if(car != null) { // Check to make sure the car bitmap is not null
            float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
            carWidth = 4 * dWidth / 5;
            carHeight = (int) (carWidth * aspectRatio);
            car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);
        }
        invalidate();
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
        updateCarImage();
    }

    public void refreshPurchasedItems() {
        loadPurchasedItems();

        float aspectRatio = (float) car.getHeight() / (float) car.getWidth();
        carWidth = 4 * dWidth / 5;
        carHeight = (int) (carWidth * aspectRatio);
        car = Bitmap.createScaledBitmap(car, carWidth, carHeight, false);

        invalidate(); // Redraw
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
        saveCoinCount();
        postInvalidate();
    }

    private void initializeFish() {
        purpleFish.clear();
        Log.d("GameView", "Initializing fish");
        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
        }
        //i just want to add one shark
        Shark ashark = new Shark(context, dWidth, dHeight);
        shark.add(ashark);
    }

    private void startPlusOneAnimation() {
        final int animationDuration = 500;
        final int frameRate = 30;
        final float deltaY = -30;
        final Handler animationHandler = new Handler(Looper.getMainLooper());
        final long startTime = System.currentTimeMillis();

        Runnable animationRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                float fraction = elapsedTime / (float) animationDuration;
                if (fraction <= 1.0) {
                    plusOneY += deltaY * fraction;
                    plusOneAlpha = 255 - (int)(255 * fraction);
                    plusOnePaint.setAlpha(plusOneAlpha);
                    invalidate();
                    animationHandler.postDelayed(this, frameRate);
                } else {
                    // Animation end
                    showPlusOne = false;
                }
            }
        };
        animationHandler.post(animationRunnable);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBg.getWidth() != getWidth() || scaledBg.getHeight() != getHeight()) {
            scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, getWidth(), getHeight(), true);
        }

        canvas.drawBitmap(scaledBg, 0, 0, null);

        for (PurpleFish fish : purpleFish) {
            fish.update();
            canvas.drawBitmap(fish.getBitmap(), fish.getX(), fish.getY(), null);
        }
        for (Shark sharks : shark) {
            sharks.update();
            canvas.drawBitmap(sharks.getBitmap(), sharks.getX(), sharks.getY(), null);
        }
        invalidate();

        int carX = dWidth / 2 - carWidth / 2;
        int carY = (int) (dHeight / 2 - carHeight / 2 + dHeight * 0.07);
        canvas.drawBitmap(car, carX, carY, null);

        if (showPlusOne) {
            SharedPreferences gamePrefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
            canvas.drawText("+" + gamePrefs.getInt("X2CoinPurchased", 1) , plusOneX, plusOneY, plusOnePaint);
        }

        int iconX = dWidth - storeIcon.getWidth() - 30;
        int iconY = 30;
        canvas.drawBitmap(storeIcon, iconX, iconY, null);

        int textSize = TEXT_SIZE + 8;

        coinCountPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coinCountPaint.setColor(Color.WHITE);
        coinCountPaint.setTextSize(textSize);
        coinCountPaint.setAntiAlias(true);

        Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setTextSize(textSize);
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(20);

        canvas.drawText("Coins: $" + coinCount, 20, textSize + 20, outlinePaint);
        canvas.drawText("Coins: $" + coinCount, 20, textSize + 20, coinCountPaint);    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            int storeIconX = dWidth - storeIcon.getWidth() - 30;
            int storeIconY = 30;
            int storeIconEndX = storeIconX + storeIcon.getWidth();
            int storeIconEndY = storeIconY + storeIcon.getHeight();

            if (touchX >= storeIconX && touchX <= storeIconEndX && touchY >= storeIconY && touchY <= storeIconEndY) {
                openStore();
                return true;
            }

            int carX = dWidth / 2 - carWidth / 2;
            int carY = (int) (dHeight / 2 - carHeight / 2 + dHeight * 0.07);

            if (touchX >= carX && touchX < (carX + carWidth) && touchY >= carY && touchY < (carY + carHeight)) {
                SharedPreferences gamePrefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
                coinCount += gamePrefs.getInt("X2CoinPurchased", 1);
                Log.d("GameView", "clicker count" + clicker.getCurrentCoinsPerClick());
                saveCoinCount();
                showPlusOne = true;
                plusOneX = carX + carWidth / 2;
                plusOneY = carY;
                plusOneAlpha = 255;
                startPlusOneAnimation();
                invalidate();
                if(coinCountChangeListener != null) {
                    coinCountChangeListener.onCoinCountChanged(coinCount);
                }
            }
        }
        return true;
    }

    private void openStore() {
        if (context instanceof Activity) {
            Intent intent = new Intent(context, StoreActivity.class);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_STORE);
        } else {
            Log.e("GameView", "Context used is not an Activity context");
        }
    }
}




