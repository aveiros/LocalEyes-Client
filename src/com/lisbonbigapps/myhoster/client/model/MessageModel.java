package com.lisbonbigapps.myhoster.client.model;

public class MessageModel {
    public String sender;
    public String text;

    public MessageModel(String sender, String text) {
	this.sender = sender;
	this.text = text;
    }
}