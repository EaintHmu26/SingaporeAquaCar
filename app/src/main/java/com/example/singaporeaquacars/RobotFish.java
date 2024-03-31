package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class RobotFish {

    private final Bitmap[] robotFish = new Bitmap[5];
    private int fishX, fishY, velocity, fishFrame;
    private final Random random;

    public RobotFish(Context context) {
        robotFish[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_1);
        robotFish[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_2);
        robotFish[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_3);
        robotFish[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_4);
        robotFish[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_5);
        random = new Random();
        resetPosition();
    }

    public Bitmap getBitmap() {
        fishFrame++;
        if (fishFrame >= robotFish.length) {
            fishFrame = 0;
        }
        return robotFish[fishFrame];
    }

    public int getX() {
        return fishX;
    }

    public int getY() {
        return fishY;
    }

    public void update() {
        fishX -= velocity;
        if (fishX < -getWidth()) {
            resetPosition();
        }
    }

    public void resetPosition() {
        fishX = GameView.dWidth + random.nextInt(1200);
        fishY = random.nextInt(GameView.dHeight);
        velocity = 8 + random.nextInt(13);
        fishFrame = 0;
    }

    public int getWidth() {
        return robotFish[0].getWidth();
    }

    public int getHeight() {
        return robotFish[0].getHeight();
    }
}

