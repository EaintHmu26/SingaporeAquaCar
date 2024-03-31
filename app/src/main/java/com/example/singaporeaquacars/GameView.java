package com.example.singaporeaquacars;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;


public class GameView extends View {

    public GameView(Context context){
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResources(), R.drawable.homeBackground);
    }

}
