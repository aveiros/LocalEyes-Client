package com.listbonbigapps.myhoster.client.util;

public class XmppReceiverEvents {
    /* xmpp connection events */
    public static final String ConnectionClose = "xmpp.ConnectionClose";
    public static final String ConnectionError = "xmpp.ConnectionError";
    public static final String ConnectionOpen = "xmpp.ConnectionOpen";

    /* xmpp account events */
    public static final String LogInSuccess = "xmpp.LogInSuccess";
    public static final String LogInError = "xmpp.LogInError";

    /* xmpp chat events */
    public static final String RosterChanged = "xmpp.RosterChanged";
    public static final String UserStatusChanged = "xmpp.UserStatusChanged";
    public static final String UserReceivedMessage = "xmpp.UserReceivedMessage";
}