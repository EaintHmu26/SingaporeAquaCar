package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class RobotFish {

    Bitmap robotFish[] = new Bitmap[5];
    int fishX, fishY, velocity, fishFrame;

    Random random;

    public RobotFish(Context context){
        robotFish[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_1);
        robotFish[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_2);
        robotFish[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_3);
        robotFish[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_4);
        robotFish[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot_fish_5);
        random = new Random();
        resetPosition();

    }
    public Bitmap getBitmap(){
        return robotFish[fishFrame];
    }
    public int getWidth(){
        return robotFish[0].getWidth();
    }
    public int getHeight(){
        return robotFish[0].getHeight();
    }
    public void resetPosition(){
        fishX = GameView.dWidth + random.nextInt(1200);
        fishY = random.nextInt(300);
        velocity = 8 + random.nextInt(13);
        fishFrame=0;
    }


}
