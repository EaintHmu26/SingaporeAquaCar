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
    ArrayList<PurpleFish> purpleFish, robotFish, shark;
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
        car = BitmapFactory.decodeResource(getResources(), R.drawable.original_car);

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, dWidth, dHeight, true);

//        purpleFish = new ArrayList<>();
//        robotFish = new ArrayList<>();
//        shark = new ArrayList<>();
//        initializeFish();
//
//        for (int i = 0; i < 3; i++) {
//            PurpleFish apurpleFish = new PurpleFish(context);
//            purpleFish.add(apurpleFish);
//            RobotFish arobotFish = new RobotFish(context);
//            robotFish.add(arobotFish);
//            Shark ashark = new Shark(context);
//            shark.add(ashark);
//        }
        handler = new Handler();
        runnable = this::invalidate; // Replace anonymous Runnable with lambda expression

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
//
//    // Initialization of Fish Objects
//    private void initializeFish() {
//        for (int i = 0; i < 3; i++) {
//            PurpleFish apurpleFish = new PurpleFish(context);
//            purpleFish.add(apurpleFish);
//            RobotFish arobotFish = new RobotFish(context);
//            robotFish.add(arobotFish);
//            Shark ashark = new Shark(context);
//            shark.add(ashark);
//        }
//    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBg.getWidth() != getWidth() || scaledBg.getHeight() != getHeight()) {
            scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, getWidth(), getHeight(), true);
        }

        canvas.drawBitmap(scaledBg, 0, 0, null);
//        for (int i = 0; i < purpleFish.size(); i++) {
//            canvas.drawBitmap(purpleFish.get(i).getBitmap(), purpleFish.get(i).purpleFishX, purpleFish.get(i).purpleFishY, null);
//            purpleFish.get(i).fishFrame++;
//            if (purpleFish.get(i).fishFrame > 4) {
//                purpleFish.get(i).fishFrame = 0;
//            }
//            purpleFish.get(i).purpleFishX -= purpleFish.get(i).velocity;
//            if (purpleFish.get(i).purpleFishX < -purpleFish.get(i).getWidth()) {
//                purpleFish.get(i).resetPosition();
//            }
//            canvas.drawBitmap(robotFish.get(i).getBitmap(), robotFish.get(i).purpleFishX, robotFish.get(i).purpleFishY, null);
//            robotFish.get(i).fishFrame++;
//            if (robotFish.get(i).fishFrame > 4) {
//                robotFish.get(i).fishFrame = 0;
//            }
//            robotFish.get(i).fishFrame += robotFish.get(i).velocity;
//            if (robotFish.get(i).purpleFishX > (dWidth + robotFish.get(i).getWidth())) {
//                robotFish.get(i).resetPosition();
//            }
//            canvas.drawBitmap(shark.get(i).getBitmap(), shark.get(i).purpleFishX, shark.get(i).purpleFishY, null);
//            shark.get(i).fishFrame++;
//            if (shark.get(i).fishFrame > 4) {
//                shark.get(i).fishFrame = 0;
//            }
//            shark.get(i).fishFrame += shark.get(i).velocity;
//            if (shark.get(i).purpleFishX > (dWidth + shark.get(i).getWidth())) {
//                shark.get(i).resetPosition();
//            }
//        }

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



