package com.lisbonbigapps.myhoster.client.data;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

import android.os.Parcel;
import android.os.Parcelable;

public enum Status implements Parcelable {
    chat,

    available,

    away,

    xa,

    dnd,

    invisible,

    unavailable,

    connection,

    unsubscribed;

    public static Status createStatusMode(Presence presence) {
	if (presence.getType() == Presence.Type.unavailable) {
	    return Status.unavailable;
	}

	final Mode mode = presence.getMode();
	if (mode == Mode.away) {
	    return Status.away;
	} else if (mode == Mode.chat) {
	    return Status.chat;
	} else if (mode == Mode.dnd) {
	    return Status.dnd;
	} else if (mode == Mode.xa) {
	    return Status.xa;
	} else {
	    return Status.available;
	}
    }

    public Mode getMode() {
	if (this == Status.away) {
	    return Mode.away;
	} else if (this == Status.chat) {
	    return Mode.chat;
	} else if (this == Status.dnd) {
	    return Mode.dnd;
	} else if (this == Status.xa) {
	    return Mode.xa;
	} else if (this == Status.available) {
	    return Mode.available;
	}
	
	throw new IllegalStateException();
    }

    public int getStringResourceId() {
	// if (this == Status.available)
	// return R.string.available;
	// else if (this == Status.dnd)
	// return R.string.dnd;
	// else if (this == Status.xa)
	// return R.string.xa;
	// else if (this == Status.chat)
	// return R.string.chat;
	// else if (this == Status.away)
	// return R.string.away;
	// else if (this == Status.unsubscribed)
	// return R.string.unsubscribed;
	// else if (this == Status.invisible)
	// return R.string.invisible;
	// return R.string.unavailable;
	return 1;
    }

    public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
	public Status createFromParcel(Parcel in) {
	    return Status.values()[in.readInt()];
	}

	public Status[] newArray(int size) {
	    return new Status[size];
	}
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
	out.writeInt(ordinal());
    }

    @Override
    public int describeContents() {
	return 0;
    }
}
