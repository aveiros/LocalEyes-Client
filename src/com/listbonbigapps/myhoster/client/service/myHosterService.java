package com.listbonbigapps.myhoster.client.service;

import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.listbonbigapps.myhoster.client.util.XmppReceiverEvents;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class myHosterService extends Service {
    private static final String TAG = "myHosterService";
    
    private XMPPConnection connection;
    private ConnectionConfiguration config;

    /* gmail xmpp server */
    /* for testing proposes */
    private final String HOST = "talk.google.com";
    private final int PORT = 5222;
    private final String SERVICE = "gmail.com";

    /* service private */
    private final IBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
	Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.d(TAG, "Received start id " + startId + ": " + intent);
	return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
	/* XMPP CONNECTION DEBUG */
	// Connection.DEBUG_ENABLED = true;
	Log.d(TAG, "onStart");
    }

    @Override
    public void onDestroy() {
	Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
	return binder;
    }

    public class ServiceBinder extends Binder {
	public myHosterService getService() {
	    return myHosterService.this;
	}
    }

    private ConnectionListener XmppConnectionLister = new ConnectionListener() {
	@Override
	public void reconnectionSuccessful() {
	    System.out.println("reconnectionSuccessful");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
	    System.out.println("reconnectionFailed");
	}

	@Override
	public void reconnectingIn(int arg0) {
	    System.out.println("reconnectingIn");
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
	    broadcastMessage(new Intent(XmppReceiverEvents.ConnectionClose));
	}

	@Override
	public void connectionClosed() {
	    broadcastMessage(new Intent(XmppReceiverEvents.ConnectionClose));
	}
    };

    private ChatManagerListener XmppChatManagerListener = new ChatManagerListener() {
	@Override
	public void chatCreated(final Chat chat, final boolean createdLocally) {
	    System.out.println("chatCreated");
	    chat.removeMessageListener(XmppChatMessengerListener);
	    chat.addMessageListener(XmppChatMessengerListener);
	}
    };

    private MessageListener XmppChatMessengerListener = new MessageListener() {
	public void processMessage(Chat chat, Message message) {
	    if (message != null && message.getBody() != null) {
		System.out.println("[ " + message.getFrom() + "]: " + message.getBody());

		Bundle extras = new Bundle();
		extras.putString("from", message.getFrom().split("/")[0]);
		extras.putString("message", message.getBody().toString());

		Intent intent = new Intent(XmppReceiverEvents.UserReceivedMessage);
		intent.putExtras(extras);

		broadcastMessage(intent);
	    }
	}
    };

    private RosterListener XmppRosterListener = new RosterListener() {
	@Override
	public void presenceChanged(Presence presence) {
	    Bundle extras = new Bundle();
	    extras.putString("user", presence.getFrom().split("/")[0]);
	    extras.putString("status", presence.getType().toString());

	    Intent intent = new Intent(XmppReceiverEvents.UserStatusChanged);
	    intent.putExtras(extras);

	    broadcastMessage(intent);
	}

	@Override
	public void entriesUpdated(Collection<String> c) {
	    String[] names = c.toArray(new String[c.size()]);

	    Bundle extras = new Bundle();
	    extras.putString("action", "update");
	    extras.putStringArray("data", names);

	    Intent intent = new Intent(XmppReceiverEvents.RosterChanged);
	    intent.putExtras(extras);

	    broadcastMessage(intent);
	}

	@Override
	public void entriesDeleted(Collection<String> c) {
	    String[] names = c.toArray(new String[c.size()]);

	    Bundle extras = new Bundle();
	    extras.putString("action", "delete");
	    extras.putStringArray("data", names);

	    Intent intent = new Intent(XmppReceiverEvents.RosterChanged);
	    intent.putExtras(extras);

	    broadcastMessage(intent);
	}

	@Override
	public void entriesAdded(Collection<String> c) {
	    String[] names = c.toArray(new String[c.size()]);

	    Bundle extras = new Bundle();
	    extras.putString("action", "add");
	    extras.putStringArray("data", names);

	    Intent intent = new Intent(XmppReceiverEvents.RosterChanged);
	    intent.putExtras(extras);

	    broadcastMessage(intent);
	}
    };

    public void connectXmpp(final String username, final String password) {
	if (hasConnection()) {
	    Log.d(TAG, "Xmpp connection already exists");
	    return;
	}

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		config = new ConnectionConfiguration(HOST, PORT, SERVICE);
		config.setReconnectionAllowed(true);
		connection = new XMPPConnection(config);

		try {
		    connection.connect();
		    broadcastMessage(new Intent(XmppReceiverEvents.ConnectionOpen));
		} catch (XMPPException e) {
		    connection = null;
		    Log.d(TAG, e.toString());
		    broadcastMessage(new Intent(XmppReceiverEvents.ConnectionError));
		}

		try {
		    connection.login(username, password);
		    broadcastMessage(new Intent(XmppReceiverEvents.LogInSuccess));
		    Presence presence = new Presence(Presence.Type.available);
		    connection.sendPacket(presence);
		    attachXmppListeners();
		} catch (XMPPException e) {
		    connection = null;
		    broadcastMessage(new Intent(XmppReceiverEvents.LogInError));
		    Log.d(TAG, e.toString());
		}
	    }
	}).start();
    }

    protected void attachXmppListeners() {
	if (hasConnection()) {
	    connection.addConnectionListener(XmppConnectionLister);
	    connection.getChatManager().addChatListener(XmppChatManagerListener);
	    connection.getRoster().addRosterListener(XmppRosterListener);
	}
    }

    protected void removeXmppListeners() {
	if (hasConnection()) {
	    connection.removeConnectionListener(XmppConnectionLister);
	    connection.getChatManager().removeChatListener(XmppChatManagerListener);
	    connection.getRoster().removeRosterListener(XmppRosterListener);
	}
    }

    public void disconnectXmpp() {
	if (hasConnection()) {
	    connection.disconnect();
	    removeXmppListeners();
	    connection = null;
	}
    }

    public boolean hasConnection() {
	return connection != null;
    }

    public void sendMessage(String to, String text) {
	if (hasConnection()) {
	    Log.i(TAG, "Sending text [" + text + "] to [" + to + "]");
	    Message msg = new Message(to, Message.Type.chat);
	    msg.setBody(text);
	    connection.sendPacket(msg);
	}
    }

    private void broadcastMessage(Intent intent) {
	getBaseContext().sendBroadcast(intent);
    }
}