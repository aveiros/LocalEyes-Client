package com.lisbonbigapps.myhoster.client.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MessageDataSource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TIMESTAMP, SQLiteHelper.COLUMN_SENDER, SQLiteHelper.COLUMN_RECEIVER, SQLiteHelper.COLUMN_TEXT };

    public MessageDataSource(Context context) {
	dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();
    }

    public void close() {
	dbHelper.close();
    }

    public Message createMessage(String sender, String receiver, String text) {
	if (sender == null || receiver == null || text == null) {
	    return null;
	}

	ContentValues values = new ContentValues();
	values.put(SQLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis() / 1000L);
	values.put(SQLiteHelper.COLUMN_SENDER, sender);
	values.put(SQLiteHelper.COLUMN_RECEIVER, receiver);
	values.put(SQLiteHelper.COLUMN_TEXT, text);

	long insertId = database.insert(SQLiteHelper.TABLE_MESSAGES, null, values);
	Cursor cursor = database.query(SQLiteHelper.TABLE_MESSAGES, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
	cursor.moveToFirst();

	Message newMessage = cursorToMessage(cursor);
	cursor.close();

	return newMessage;
    }

    public void deleteMessage(Message message) {
	long id = message.getId();
	System.out.println("message deleted with id: " + id);
	database.delete(SQLiteHelper.TABLE_MESSAGES, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Message> getAllMessages() {
	List<Message> messages = new ArrayList<Message>();

	Cursor cursor = database.query(SQLiteHelper.TABLE_MESSAGES, allColumns, null, null, null, null, null);
	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	    Message message = cursorToMessage(cursor);
	    messages.add(message);
	    cursor.moveToNext();
	}

	cursor.close();
	return messages;
    }

    private Message cursorToMessage(Cursor cursor) {
	Message message = new Message();

	message.setId(cursor.getLong(0));
	message.setTimestamp(cursor.getLong(1));
	message.setSender(cursor.getString(2));
	message.setReceiver(cursor.getString(3));
	message.setText(cursor.getString(4));

	return message;
    }
}
