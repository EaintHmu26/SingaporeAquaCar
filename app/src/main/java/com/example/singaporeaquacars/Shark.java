package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Shark {

    Bitmap shark[] = new Bitmap[5];
    int fishX, fishY, velocity, fishFrame;

    Random random;

    public Shark(Context context){
        shark[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_1);
        shark[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_2);
        shark[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_3);
        shark[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_4);
        shark[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_5);
        random = new Random();
        resetPosition();

    }
    public Bitmap getBitmap(){
        return shark[fishFrame];
    }
    public int getWidth(){
        return shark[0].getWidth();
    }
    public int getHeight(){
        return shark[0].getHeight();
    }
    public void resetPosition(){
        fishX = GameView.dWidth + random.nextInt(1200);
        fishY = random.nextInt(300);
        velocity = 8 + random.nextInt(13);
        fishFrame=0;
    }


}
