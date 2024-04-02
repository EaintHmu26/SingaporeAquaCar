package com.example.singaporeaquacars;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClickerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Clicker.db";

    public ClickerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GAME_TABLE = "CREATE TABLE game_progress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "totalCoinsEarned INTEGER NOT NULL," +
                "currentCoinsPerClick INTEGER NOT NULL," +
                "autoClickUpgradeActive INTEGER NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_GAME_TABLE);
        // Insert initial data
        ContentValues initialValues = new ContentValues();
        initialValues.put("totalCoinsEarned", 0); // Default initial value
        initialValues.put("currentCoinsPerClick", 1); // Default initial value
        initialValues.put("autoClickUpgradeActive", 0); // Default initial value
        db.insert("game_progress", null, initialValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for game data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS game_progress");
        onCreate(db);
    }
}
