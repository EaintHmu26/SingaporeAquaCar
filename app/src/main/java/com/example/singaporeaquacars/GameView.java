package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GameView extends View {

    Bitmap home_bkgrd_platform, car, scaledBg;
    static int dWidth, dHeight;
    ArrayList<PurpleFish> purpleFish;
    //ArrayList<RobotFish> robotFish;

    ArrayList<Shark> shark;
    Handler handler;
    Runnable runnable;
    static int carWidth, carHeight;
    Context context;
    int coinCount;
    Paint coinCountPaint;
    final int TEXT_SIZE = 60;
    SharedPreferences prefs;

    public GameView(Context context){
        super(context);
        this.context = context;

        home_bkgrd_platform = BitmapFactory.decodeResource(getResources(), R.drawable.home_bkgrd_platform);
        Log.d("GameView", "Background loaded");

        car = BitmapFactory.decodeResource(getResources(), R.drawable.original_car);
        Log.d("GameView", "car loaded");


        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, dWidth, dHeight, true);

        purpleFish = new ArrayList<>();
        //robotFish = new ArrayList<RobotFish>();
        shark = new ArrayList<Shark>();
        initializeFish();

        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
//            RobotFish arobotFish = new RobotFish(context);
//            robotFish.add(arobotFish);
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
        for (int i = 0; i < 3; i++) {
            PurpleFish aPurpleFish = new PurpleFish(context, dWidth, dHeight);
            purpleFish.add(aPurpleFish);
            Log.d("GameView", "PurpleFish added");

        }
        //i just want to add one shark
        Shark ashark = new Shark(context, dWidth, dHeight);
        shark.add(ashark);
        Log.d("GameView", "shark added");

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Log.d("GameView", "onDraw called");

        if (scaledBg.getWidth() != getWidth() || scaledBg.getHeight() != getHeight()) {
            scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, getWidth(), getHeight(), true);
        }

        canvas.drawBitmap(scaledBg, 0, 0, null);


        Log.d("GameView", "PurpleFish size: " + purpleFish.size());
//        Log.d("GameView", "RobotFish size: " + robotFish.size());
        Log.d("GameView", "Shark size: " + shark.size());

        //for (int i = 0; i < Math.min(Math.min(purpleFish.size(), robotFish.size()), shark.size()); i++) {
//        for (int i = 0; i < purpleFish.size(); i++) {
////            Log.d("GameView", "onDraw:drawing fishes ");
//            // Draw and animate purpleFish
//            purpleFish.get(i).update();
//            canvas.drawBitmap(purpleFish.get(i).getBitmap(), purpleFish.get(i).getX(), purpleFish.get(i).getY(), null);
//            purpleFish.get(i).fishFrame++;
//            if (purpleFish.get(i).fishFrame > 4) {
//                 purpleFish.get(i).fishFrame = 0;
//            }
//           int newPurpleFishX = purpleFish.get(i).getX() + purpleFish.get(i).velocity;
//           purpleFish.get(i).setX(newPurpleFishX);
//
//            if (purpleFish.get(i).getX() < -purpleFish.get(i).getWidth()) {
//                purpleFish.get(i).resetPosition(dWidth,dHeight);
//            }
//
////    // Draw and animate robotFish (fixed to use correct x-coordinate and velocity)
////    canvas.drawBitmap(robotFish.get(i).getBitmap(), robotFish.get(i).getX(), robotFish.get(i).getY(), null);
////    robotFish.get(i).fishFrame++;
////    if (robotFish.get(i).fishFrame > 4) {
////        robotFish.get(i).fishFrame = 0;
////    }
////           int newRobotFishX = robotFish.get(i).getX() + robotFish.get(i).velocity;
////           robotFish.get(i).setX(newRobotFishX);
////           // Assuming robotFish move left to right
////    if (robotFish.get(i).getX()> (dWidth + robotFish.get(i).getWidth())) {
////        robotFish.get(i).resetPosition();
////    }
////
////    // Draw and animate shark (fixed to use correct x-coordinate and velocity)
////    canvas.drawBitmap(shark.get(i).getBitmap(), shark.get(i).getX(), shark.get(i).getY(), null);
////    shark.get(i).fishFrame++;
////    if (shark.get(i).fishFrame > 4) {
////        shark.get(i).fishFrame = 0;
////    }
////           int newSharkX = shark.get(i).getX() + shark.get(i).velocity;
////           shark.get(i).setX(newSharkX);
////    if (shark.get(i).getX() > (dWidth + shark.get(i).getWidth())) {
////        shark.get(i).resetPosition();
////    }
//        }
//        invalidate();
        for (PurpleFish fish : purpleFish) {
            fish.update();  // Update position and check for looping
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

            // Calculate the car's position
            int carX = dWidth / 2 - carWidth / 2;
            // Adjust the carY variable to match the Y position where the car image actually starts
            int carY = (int) (dHeight / 2 - carHeight / 2 + dHeight * 0.07); // Adjust this factor to move the car down
            // Check if the touch is within the bounds of the car image
            if (touchX >= carX && touchX < (carX + carWidth) && touchY >= carY && touchY < (carY + carHeight)) {
                coinCount++; // Increment coin count only if the car is touched
                invalidate(); // Redraw to show the updated coin count
            }
        }
        return true;
    }
}



