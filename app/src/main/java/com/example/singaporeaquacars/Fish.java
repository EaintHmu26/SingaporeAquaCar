package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class Fish {

    Bitmap fish[] = new Bitmap[5];
    int fishX, fishY, velocity, fishFrame;

    Random random;

    public Fish(Context context){
        fish[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_1);
        fish[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_2);
        fish[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_3);
        fish[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_4);
        fish[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_5);
        random = new Random();
        resetPosition();

    }
    public Bitmap getBitmap(){
        return fish[fishFrame];
    }
    public int getWidth(){
        return fish[0].getWidth();
    }
    public int getHeight(){
        return fish[0].getHeight();
    }
    public void resetPosition(){
        fishX = GameView.dWidth + random.nextInt(1200);
        fishY = random.nextInt(300);
        velocity = 8 + random.nextInt(13);
        fishFrame=0;
    }



}
