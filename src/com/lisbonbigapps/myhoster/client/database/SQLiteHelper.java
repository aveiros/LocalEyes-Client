package com.lisbonbigapps.myhoster.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "messagesRepository";

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_MESSAGE = "message";

    private static final String DATABASE_CREATE = "create table " + TABLE_MESSAGES + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TIMESTAMP + " text not null, " + COLUMN_SENDER
	    + " text not null, " + COLUMN_RECEIVER + " text not null, " + COLUMN_MESSAGE + " text not null);";

    public SQLiteHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
	onCreate(db);
    }
}