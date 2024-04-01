package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class PurpleFish {

    private Bitmap[] purpleFish = new Bitmap[5];
    private int frameDelayCounter = 0; // Counter to control frame update delay
    private final int distancePerFrame = 15; // Distance for each frame change
    private int initialX;
    private int fishX;
    private int fishY;
    protected int velocity;
    protected int fishFrame;
    private int screenWidth, screenHeight;
    private Random random;

    public PurpleFish(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth; // Store screen dimensions
        this.screenHeight = screenHeight;
        // Desired width of the fish as a fraction of screen width, adjust this value as needed
        int newWidth = screenWidth / 8; // Example: fish width to be 1/10th of screen width

        // Scale and load each fish bitmap
        for (int i = 0; i < purpleFish.length; i++) {
            // Load the original bitmap
            Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("purple_fish_" + (i + 1), "drawable", context.getPackageName()));

            // Calculate the new height to maintain the aspect ratio
            int newHeight = (originalBitmap.getHeight() * newWidth) / originalBitmap.getWidth();

            // Scale the original bitmap to the new size
            purpleFish[i] = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        }

        random = new Random();
        resetPosition(screenWidth, screenHeight); // Adjust to take screen dimensions
    }

    // Adjust the resetPosition method to accept screen dimensions
    public void resetPosition(int screenWidth, int screenHeight) {
        initialX = -random.nextInt(100) - getWidth();
        fishX = initialX; // Set fishX to initialX
        fishY = random.nextInt(screenHeight);
        velocity = 2 + random.nextInt(3); // Adjust for a slower speed
        fishFrame = 0; // Start with the first frame
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

    public void setX(int x) {
        this.fishX = x;
    }
    public void setY(int y) {
        this.fishY = y;
    }

    // Method to update fish position, can be called on each frame
    public void update() {
        fishX += velocity; // Move fish across the screen
        // If fish moves off screen, reset to the right for continuous loop
        if (fishX > screenWidth) { // Check if the fish is off-screen to the right
            resetPosition(screenWidth, screenHeight);
        }
        int distanceMoved = fishX - initialX;
        fishFrame = (distanceMoved / distancePerFrame) % purpleFish.length;


    }



    // Get fish width for position calculations
    public int getWidth() {
        return purpleFish[0].getWidth();
    }

    // Get fish height, might be useful for collision detection or similar features
    public int getHeight() {
        return purpleFish[0].getHeight();
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
