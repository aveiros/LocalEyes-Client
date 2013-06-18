package com.lisbonbigapps.myhoster.client.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessagesDataSource {
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TIMESTAMP, SQLiteHelper.COLUMN_SENDER, SQLiteHelper.COLUMN_RECEIVER, SQLiteHelper.COLUMN_MESSAGE };

    public MessagesDataSource(Context context) {
	dbHelper = new SQLiteHelper(context);
    }

    public Message addMessage(String sender, String receiver, String message) {
	if (sender == null || receiver == null || message == null) {
	    return null;
	}

	SQLiteDatabase db = dbHelper.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(SQLiteHelper.COLUMN_TIMESTAMP, "" + (System.currentTimeMillis() / 1000L));
	values.put(SQLiteHelper.COLUMN_SENDER, sender);
	values.put(SQLiteHelper.COLUMN_RECEIVER, receiver);
	values.put(SQLiteHelper.COLUMN_MESSAGE, message);

	long insertId = db.insert(SQLiteHelper.TABLE_MESSAGES, null, values);

	Cursor cursor = db.query(SQLiteHelper.TABLE_MESSAGES, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

	cursor.moveToFirst();
	Message newMessage = cursorToMessage(cursor);

	cursor.close();
	db.close();

	return newMessage;
    }

    public Message getMessage(int id) {
	return null;
    }

    public void deleteMessage(Message message) {
	SQLiteDatabase db = dbHelper.getWritableDatabase();

	long id = message.getId();
	System.out.println("message deleted with id: " + id);
	db.delete(SQLiteHelper.TABLE_MESSAGES, SQLiteHelper.COLUMN_ID + " = " + id, null);

	db.close();
    }

    public void deleteAllMessages() {
	SQLiteDatabase db = dbHelper.getWritableDatabase();

	db.delete(SQLiteHelper.TABLE_MESSAGES, null, null);

	db.close();
    }

    public List<Message> getMessages(int limit) {
	SQLiteDatabase db = dbHelper.getWritableDatabase();

	List<Message> messages = new ArrayList<Message>();

	Cursor cursor = db.query(SQLiteHelper.TABLE_MESSAGES, allColumns, null, null, null, null, null);

	int counter = 0;
	cursor.moveToFirst();
	while (!cursor.isAfterLast() || counter < limit) {
	    counter += 1;
	    Message message = cursorToMessage(cursor);
	    messages.add(message);
	    cursor.moveToNext();
	}

	cursor.close();

	db.close();
	return messages;
    }

    public List<Message> getMessagesWith(String user1, String user2, int limit) {
	SQLiteDatabase db = dbHelper.getWritableDatabase();

	List<Message> messages = new ArrayList<Message>();

	String where = "(%s = '%s' and %s = '%s') or (%s = '%s' and %s = '%s')";
	String fWhere = String.format(where, SQLiteHelper.COLUMN_SENDER, user1, SQLiteHelper.COLUMN_RECEIVER, user2, SQLiteHelper.COLUMN_SENDER, user2, SQLiteHelper.COLUMN_RECEIVER, user1);
	Cursor cursor = db.query(SQLiteHelper.TABLE_MESSAGES, allColumns, fWhere, null, null, null, null);

	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	    Message message = cursorToMessage(cursor);
	    messages.add(message);
	    cursor.moveToNext();
	}

	db.close();
	return messages;
    }

    public List<Message> getAllMessages() {
	SQLiteDatabase db = dbHelper.getWritableDatabase();

	List<Message> messages = new ArrayList<Message>();

	Cursor cursor = db.query(SQLiteHelper.TABLE_MESSAGES, allColumns, null, null, null, null, null);

	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	    Message message = cursorToMessage(cursor);
	    messages.add(message);
	    cursor.moveToNext();
	}

	cursor.close();

	db.close();
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
