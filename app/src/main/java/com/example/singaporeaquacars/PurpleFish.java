package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;

public class PurpleFish {

    Bitmap purpleFish[] = new Bitmap[5];
    int fishX, fishY, velocity, fishFrame;

    Random random;

    public PurpleFish(Context context){
        purpleFish[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_1);
        purpleFish[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_2);
        purpleFish[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_3);
        purpleFish[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_4);
        purpleFish[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_fish_5);
        random = new Random();
        resetPosition();

    }
    public Bitmap getBitmap(){
        return purpleFish[fishFrame];
    }
    public int getWidth(){
        return purpleFish[0].getWidth();
    }
    public int getHeight(){
        return purpleFish[0].getHeight();
    }
    public void resetPosition(){
        fishX = GameView.dWidth + random.nextInt(1200);
        fishY = random.nextInt(300);
        velocity = 8 + random.nextInt(13);
        fishFrame=0;
    }



}
