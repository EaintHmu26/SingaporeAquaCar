package com.example.singaporeaquacars;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
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
    ArrayList<RobotFish> robotFish;
    ArrayList<Shark> shark;
    Handler handler;
    Runnable runnable;
    static int carWidth, carHeight;
    Context context;
    int coinCount = 0;
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

        // Scale the background to fill the screen
        scaledBg = Bitmap.createScaledBitmap(home_bkgrd_platform, dWidth, dHeight, true);

        purpleFish = new ArrayList<>();
        robotFish = new ArrayList<>();
        shark = new ArrayList<>();
        // Populate your fish and shark arrays here with actual objects

        handler = new android.os.Handler();
        runnable = () -> invalidate();

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

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scaledBg, 0, 0, null);

        // Loop through each fish and shark array to draw them
        for (PurpleFish fish : purpleFish) {
            canvas.drawBitmap(fish.getBitmap(), fish.getX(), fish.getY(), null);
        }
        for (RobotFish fish : robotFish) {
            canvas.drawBitmap(fish.getBitmap(), fish.getX(), fish.getY(), null);
        }
        for (Shark shark : shark) {
            canvas.drawBitmap(shark.getBitmap(), shark.getX(), shark.getY(), null);
        }

        // Draw the car in the center
        int carX = dWidth / 2 - carWidth / 2;
        int carY = dHeight / 2 - carHeight / 2;
        canvas.drawBitmap(car, carX, carY, null);

        // Display the coin count
        canvas.drawText("Coins: " + coinCount, 20, TEXT_SIZE + 20, coinCountPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            if (touchX >= (dWidth / 2 - carWidth / 2) && touchX <= (dWidth / 2 + carWidth / 2) &&
                    touchY >= (dHeight / 2 - carHeight / 2) && touchY <= (dHeight / 2 + carHeight / 2)) {
                coinCount++;
                invalidate(); // Redraw to show updated coin count
            }
        }
        return true;
    }

    public void pause() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coinCount", coinCount);
        editor.apply();
    }
}



