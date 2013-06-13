package com.lisbonbigapps.myhoster.client.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageItem implements Parcelable {
    String name;
    String username;
    String text;

    public MessageItem() {
    }

    public MessageItem(Parcel in) {
	this.readFromParcel(in);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public MessageItem(String username, String text) {
	this.username = username;
	this.text = text;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public static final Parcelable.Creator<MessageItem> CREATOR = new Parcelable.Creator<MessageItem>() {
	public MessageItem createFromParcel(Parcel source) {
	    return new MessageItem(source);
	}

	public MessageItem[] newArray(int size) {
	    return new MessageItem[size];
	}
    };

    @Override
    public void writeToParcel(Parcel target, int flags) {
	target.writeString(this.name);
	target.writeString(this.username);
	target.writeString(this.text);
    }

    private void readFromParcel(Parcel source) {
	this.name = source.readString();
	this.username = source.readString();
	this.text = source.readString();
    }

    @Override
    public int describeContents() {
	return 0;
    }
}
