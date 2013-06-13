package com.lisbonbigapps.myhoster.client.util;

public class MessengerEvents {
    public static final String ConnectionClose = "xmpp.ConnectionClose";
    public static final String ConnectionError = "xmpp.ConnectionError";
    public static final String Connected = "xmpp.Connected";

    public static final String Authenticated = "xmpp.Authenticated";
    public static final String AuthenticationError = "xmpp.AuthenticationError";

    public static final String Contacts = "xmpp.Contacts";
    public static final String RosterAdd = "xmpp.RosterAdd";
    public static final String RosterRemove = "xmpp.RosterRemove";
    public static final String RosterUpdate = "xmpp.RosterUpdate";
    public static final String RosterContactPresenceChanged = "xmpp.RosterContactPresenceChanged";
    public static final String MessageReceived = "xmpp.MessageReceived";
}