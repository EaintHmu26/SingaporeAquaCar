package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class PurpleFish {

    private Bitmap[] purpleFish = new Bitmap[5];
    private int fishX, fishY, velocity, fishFrame;
    private Random random;

    public PurpleFish(Context context) {
        purpleFish[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_1);
        purpleFish[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_2);
        purpleFish[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_3);
        purpleFish[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_4);
        purpleFish[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_5);
        random = new Random();
        resetPosition();
    }

    // Method to return the current bitmap for drawing
    public Bitmap getBitmap() {
        // Update fish frame for animation
        fishFrame++;
        if (fishFrame >= purpleFish.length) {
            fishFrame = 0;
        }
        return purpleFish[fishFrame];
    }

    // Get X position of fish
    public int getX() {
        return fishX;
    }

    // Get Y position of fish
    public int getY() {
        return fishY;
    }

    // Method to update fish position, can be called on each frame
    public void update() {
        // Move fish across the screen
        fishX -= velocity;
        // If fish moves off screen, reset to the right
        if (fishX < -getWidth()) {
            resetPosition();
        }
    }

    // Reset fish position and velocity
    public void resetPosition() {
        fishX = GameView.dWidth + random.nextInt(1200); // Start off-screen to the right
        fishY = random.nextInt(GameView.dHeight); // Random Y position within screen bounds
        velocity = 8 + random.nextInt(13); // Random velocity
        fishFrame = 0; // Reset animation frame
    }

    // Get fish width for position calculations
    public int getWidth() {
        return purpleFish[0].getWidth();
    }

    // Get fish height, might be useful for collision detection or similar features
    public int getHeight() {
        return purpleFish[0].getHeight();
    }
}
