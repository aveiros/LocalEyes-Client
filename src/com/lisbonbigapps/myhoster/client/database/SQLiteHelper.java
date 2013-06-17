package com.lisbonbigapps.myhoster.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_TEXT = "text";

    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = String.format(
	    "create table %s (%s integer primary key autoincrement, %s integerth.froschroom.com:27500 not null, %s text not null, %s text not null, %s text not null);", TABLE_MESSAGES, COLUMN_ID,
	    COLUMN_TIMESTAMP, COLUMN_SENDER, COLUMN_RECEIVER, COLUMN_TEXT);

    public SQLiteHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
	database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
	onCreate(db);
    }
}