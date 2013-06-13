package com.lisbonbigapps.myhoster.client.data;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import android.os.Parcel;
import android.os.Parcelable;

public class RosterContact implements Parcelable {
    private String name;
    private String username;
    private String presence;
    private String mode;
    private String status;

    public RosterContact() {
    }

    public RosterContact(Parcel in) {
	readFromParcel(in);
    }

    public RosterContact(String name, String username, String presence, String status) {
	this.name = name;
	this.username = username;
	this.presence = presence;
	this.status = status;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPresence() {
	return presence;
    }

    public void setPresence(String presence) {
	this.presence = presence;
    }

    public String getMode() {
	return mode;
    }

    public void setMode(String mode) {
	this.mode = mode;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public static final RosterContact createInstance(VCard v, RosterEntry r, Presence p) {
	if (r == null) {
	    return null;
	}

	RosterContact contactEntry = new RosterContact();
	contactEntry.name = v == null ? null : v.getField("FN");
	contactEntry.username = r.getUser();
	contactEntry.presence = (p == null || p.getType() == null) ? null : p.getType().toString();
	contactEntry.mode = (p == null || p.getMode() == null) ? null : p.getMode().toString();
	contactEntry.status = (r.getStatus() == null) ? null : r.getStatus().toString();
	return contactEntry;
    }

    public static final Parcelable.Creator<RosterContact> CREATOR = new Parcelable.Creator<RosterContact>() {
	public RosterContact createFromParcel(Parcel source) {
	    return new RosterContact(source);
	}

	public RosterContact[] newArray(int size) {
	    return new RosterContact[size];
	}
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
	out.writeString(this.name);
	out.writeString(this.username);
	out.writeString(this.presence);
	out.writeString(this.mode);
	out.writeString(this.status);
    }

    private void readFromParcel(Parcel in) {
	this.name = in.readString();
	this.username = in.readString();
	this.presence = in.readString();
	this.mode = in.readString();
	this.status = in.readString();
    }

    @Override
    public int describeContents() {
	return 0;
    }
}
