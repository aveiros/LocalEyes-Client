package com.lisbonbigapps.myhoster.client.database;

public class Message {
    long id;
    long timestamp;
    String sender;
    String receiver;
    String text;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    public String getSender() {
	return sender;
    }

    public void setSender(String sender) {
	this.sender = sender;
    }

    public String getReceiver() {
	return receiver;
    }

    public void setReceiver(String receiver) {
	this.receiver = receiver;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    @Override
    public String toString() {
	return text;
    }
}
