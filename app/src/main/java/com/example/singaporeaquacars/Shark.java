package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class Shark {

    private final Bitmap[] shark = new Bitmap[5];
    private int fishX, fishY, velocity, fishFrame;
    private final Random random;

    public Shark(Context context) {
        shark[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_1);
        shark[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_2);
        shark[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_3);
        shark[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_4);
        shark[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_5);
        random = new Random();
        resetPosition();
    }

    public Bitmap getBitmap() {
        fishFrame++;
        if (fishFrame >= shark.length) {
            fishFrame = 0;
        }
        return shark[fishFrame];
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
        return shark[0].getWidth();
    }

    public int getHeight() {
        return shark[0].getHeight();
    }
}

