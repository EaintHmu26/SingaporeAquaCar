package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class Shark {

    private final Bitmap[] shark = new Bitmap[5];
    private int fishX, fishY, velocity, fishFrame;
    private final int distancePerFrame = 20; // Distance for each frame change
    private int initialX; // Initial X position when the fish is reset

    private final Random random;
    private final int screenWidth, screenHeight; // Store screen dimensions

    public Shark(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        int newWidth = screenWidth /3; // Example: fish width to be 1/10th of screen width

        for (int i = 0; i < shark.length; i++) {
            // Load the original bitmap
            Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("shark_" + (i + 1), "drawable", context.getPackageName()));

            // Calculate the new height to maintain the aspect ratio
            int newHeight = (originalBitmap.getHeight() * newWidth) / originalBitmap.getWidth();

            // Scale the original bitmap to the new size
            shark[i] = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        }
        random = new Random();
        resetPosition(screenWidth, screenHeight); // Adjust to take screen dimensions
    }

    public void update() {
        fishX += velocity; // Move fish across the screen
        // If fish moves off screen, reset to the right for continuous loop
        if (fishX > screenWidth) { // Check if the fish is off-screen to the right
            resetPosition(screenWidth, screenHeight);
        }
        int distanceMoved = fishX - initialX;
        fishFrame = (distanceMoved / distancePerFrame) % shark.length;
    }

    public void resetPosition(int screenWidth, int screenHeight) {
        initialX = -random.nextInt(100) - getWidth();
        fishX = initialX; // Set fishX to initialX
        fishY = random.nextInt(screenHeight);
        velocity = 2 + random.nextInt(3); // Adjust for a slower speed
        fishFrame = 0; // Start with the first frame
    }

    // Correct the setY method
    public void setY(int y) {
        this.fishY = y; // Corrected from setting fishX to fishY
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

    public void setX(int x) {
        this.fishX = x;
    }


    public int getWidth() {
        return shark[0].getWidth();
    }

    public int getHeight() {
        return shark[0].getHeight();
    }
}

