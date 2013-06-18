package com.lisbonbigapps.myhoster.client.service;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import com.lisbonbigapps.myhoster.client.data.MessageItem;
import com.lisbonbigapps.myhoster.client.data.RosterContact;
import com.lisbonbigapps.myhoster.client.util.MessengerEvents;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
    private final String TAG = LocalService.class.toString();

    private XMPPConnection connection;
    private ConnectionConfiguration config;
    private Thread connectionThread;

    /* service private */
    private final IBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
	String host = ServerHelper.getXmppHost();
	int port = ServerHelper.getXmppPort();
	String service = ServerHelper.getXmppService();

	XMPPConnection.DEBUG_ENABLED = true;
	this.config = new ConnectionConfiguration(host, port, service);
	this.config.setReconnectionAllowed(true);
	// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
	ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());

	this.connection = new XMPPConnection(this.config);
	Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.d(TAG, "onStartCommand");
	Log.d(TAG, "Received start id " + startId + ": " + intent);
	return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
	Log.d(TAG, "onStart");
    }

    @Override
    public void onDestroy() {
	this.disconnect();
	Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
	return binder;
    }

    public class ServiceBinder extends Binder {
	public LocalService getService() {
	    return LocalService.this;
	}
    }

    private void onReconnectionSuccessful() {
	System.out.println("reconnectionSuccessful");
    }

    private void onReconnectionFailed() {
	System.out.println("reconnectionFailed");
    }

    private void onReconnectingIn() {
	System.out.println("reconnectingIn");
    }

    private void onConnectionClosedOnError() {
	broadcastMessage(new Intent(MessengerEvents.ConnectionClose));
    }

    private void onConnectionClosed() {
	broadcastMessage(new Intent(MessengerEvents.ConnectionClose));
    }

    private void onChatCreated(final Chat chat, final boolean createdLocally) {
	System.out.println("chatCreated");
	chat.removeMessageListener(messengerListener);
	chat.addMessageListener(messengerListener);
    }

    private void onPresenceChanged(Presence presence) {
	String username = presence.getFrom().split("/")[0];

	RosterContact rosterContact = this.assembleRosterContact(username);
	if (rosterContact == null) {
	    return;
	}

	Intent intent = new Intent(MessengerEvents.RosterContactPresenceChanged);
	intent.putExtra("data", rosterContact);
	broadcastMessage(intent);
    }

    private void onEntriesUpdated(Collection<String> entries) {
	ArrayList<RosterContact> rosterContacts = assembleRosterContacts(entries);

	Intent intent = new Intent(MessengerEvents.RosterUpdate);
	intent.putParcelableArrayListExtra("data", rosterContacts);
	broadcastMessage(intent);
    }

    private void onEntriesDeleted(Collection<String> entries) {
	ArrayList<RosterContact> rosterContacts = assembleRosterContacts(entries);

	Intent intent = new Intent(MessengerEvents.RosterRemove);
	intent.putParcelableArrayListExtra("data", rosterContacts);
	broadcastMessage(intent);
    }

    private void onEntriesAdded(Collection<String> entries) {
	ArrayList<RosterContact> rosterContacts = assembleRosterContacts(entries);

	Intent intent = new Intent(MessengerEvents.RosterAdd);
	intent.putParcelableArrayListExtra("data", rosterContacts);
	broadcastMessage(intent);
    }

    private void onMessage(Chat chat, Message message) {
	if (message != null && message.getBody() != null) {
	    /* TODO: should not be here */
	    VCard card = new VCard();
	    try {
		card.load(connection, chat.getParticipant());
	    } catch (XMPPException e) {
		e.printStackTrace();
	    }

	    MessageItem messageEntry = new MessageItem();
	    messageEntry.setName(card.getField("FN"));
	    messageEntry.setText(message.getBody() == null ? "" : message.getBody().toString());
	    messageEntry.setUsername(message.getFrom().split("/")[0]);

	    Intent intent = new Intent(MessengerEvents.MessageReceived);
	    intent.putExtra("data", messageEntry);
	    broadcastMessage(intent);
	}
    }

    private ArrayList<RosterContact> assembleRosterContacts(Collection<String> entries) {
	ArrayList<RosterContact> items = new ArrayList<RosterContact>();

	for (String username : entries) {
	    VCard vcard = new VCard();
	    try {
		vcard.load(connection, username);
	    } catch (XMPPException e) {
		vcard = null;
	    }

	    RosterEntry rosterEntry = findRosterEntry(username);
	    if (rosterEntry == null) {
		continue;
	    }

	    Presence presence = connection.getRoster().getPresence(username);
	    RosterContact rosterContact = RosterContact.createInstance(vcard, rosterEntry, presence);
	    if (rosterContact == null) {
		continue;
	    }

	    items.add(rosterContact);
	}

	return items;
    }

    private RosterContact assembleRosterContact(String username) {
	if (username == null) {
	    return null;
	}

	VCard vcard = new VCard();
	try {
	    vcard.load(connection, username);
	} catch (XMPPException e) {
	    vcard = null;
	}

	RosterEntry entry = findRosterEntry(username);
	if (entry == null) {
	    return null;
	}

	Presence presence = connection.getRoster().getPresence(username);
	RosterContact rosterContact = RosterContact.createInstance(vcard, entry, presence);
	if (rosterContact == null) {
	    return null;
	}

	return rosterContact;
    }

    public void connect(final String username, final String password) {
	Log.d(TAG, "XMPP CONNECT BEGIN");

	if (this.connectionThread != null && this.connectionThread.isAlive()) {
	    return;
	}

	if (this.connection.isConnected()) {
	    if (this.connection.isAuthenticated()) {
		broadcastMessage(new Intent(MessengerEvents.Authenticated));
		return;
	    }

	    broadcastMessage(new Intent(MessengerEvents.Connected));
	    return;
	}

	this.connectionThread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		Log.d(TAG, "THREAD BEGIN");

		if (!connection.isConnected()) {
		    this.connect();
		}

		if (connection.isConnected() && !connection.isAuthenticated()) {
		    this.login();
		}

		Log.d(TAG, "THREAD END");
	    }

	    protected void connect() {
		Log.d(TAG, "CONNECT BEGIN");

		try {
		    connection.connect();
		    attachListeners();
		    broadcastMessage(new Intent(MessengerEvents.Connected));
		} catch (XMPPException e) {
		    Log.d(TAG, e.toString());
		    broadcastMessage(new Intent(MessengerEvents.ConnectionError));
		}

		Log.d(TAG, "CONNECT END");
	    }

	    protected void login() {
		Log.d(TAG, "LOGIN BEGIN");

		try {
		    connection.login(username, password);
		    broadcastMessage(new Intent(MessengerEvents.Authenticated));
		    Presence presence = new Presence(Presence.Type.available);
		    connection.sendPacket(presence);
		    // attachXmppListeners();
		} catch (XMPPException e) {
		    broadcastMessage(new Intent(MessengerEvents.AuthenticationError));
		    Log.d(TAG, e.toString());
		}

		Log.d(TAG, "LOGIN END");
	    }
	});

	this.connectionThread.start();

	Log.d(TAG, "XMPP CONNECT END");
    }

    public String getUsername() {
	if (!this.connection.isAuthenticated()) {
	    return null;
	}

	return connection.getUser().split("/")[0];
    }

    public RosterContact getOwnContact() {
	return null;
    }

    public Collection<RosterEntry> contacts(boolean offline) {
	Collection<RosterEntry> contacts = new ArrayList<RosterEntry>();

	if (!this.connection.isAuthenticated()) {
	    return contacts;
	}

	Roster roster = connection.getRoster();
	if (offline) {
	    return roster.getEntries();
	}

	Collection<RosterEntry> rosterEntries = roster.getEntries();
	for (RosterEntry rosterEntry : rosterEntries) {
	    ItemStatus status = rosterEntry.getStatus();
	    if (status == null) {
		continue;
	    }

	    if (status.toString().equals("available")) {
		contacts.add(rosterEntry);
	    }
	}

	return contacts;
    }

    public RosterEntry findRosterEntry(String name) {
	if (name == null) {
	    return null;
	}

	if (!this.connection.isAuthenticated()) {
	    return null;
	}

	Roster roster = connection.getRoster();
	Collection<RosterEntry> rosterEntries = roster.getEntries();

	for (RosterEntry rosterEntry : rosterEntries) {
	    String user = rosterEntry.getUser();
	    if (user.equals(name)) {
		return rosterEntry;
	    }
	}

	return null;
    }

    protected void attachListeners() {
	this.connection.addConnectionListener(connectionListener);
	this.connection.getChatManager().addChatListener(chatListener);
	this.connection.getRoster().addRosterListener(rosterListener);
    }

    protected void detachListeners() {
	this.connection.removeConnectionListener(connectionListener);
	this.connection.getChatManager().removeChatListener(chatListener);
	this.connection.getRoster().removeRosterListener(rosterListener);
    }

    public void disconnect() {
	this.unavailable();
	this.connection.disconnect();
	this.detachListeners();
    }

    public void chat() {
	this.chat("");
    }

    public void available() {
	this.available("");
    }

    public void busy() {
	this.busy("");
    }

    public void away() {
	this.away("");
    }

    public void xaway() {
	this.xaway("");
    }

    public void chat(String message) {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	if (message == null) {
	    message = "";
	}

	Presence presence = new Presence(Presence.Type.available, message, 100, Presence.Mode.chat);
	this.setPresence(presence);
    }

    public void xaway(String message) {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	if (message == null) {
	    message = "";
	}

	Presence presence = new Presence(Presence.Type.available, message, 100, Presence.Mode.xa);
	this.setPresence(presence);
    }

    public void available(String message) {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	if (message == null) {
	    message = "";
	}

	Presence presence = new Presence(Presence.Type.available, message, 100, Presence.Mode.available);
	this.setPresence(presence);
    }

    public void busy(String message) {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	if (message == null) {
	    message = "";
	}

	Presence presence = new Presence(Presence.Type.available, message, 100, Presence.Mode.dnd);
	this.setPresence(presence);
    }

    public void away(String message) {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	if (message == null) {
	    message = "";
	}

	Presence presence = new Presence(Presence.Type.available, message, 100, Presence.Mode.away);
	this.setPresence(presence);
    }

    protected void unavailable() {
	if (!this.connection.isAuthenticated()) {
	    return;
	}

	Presence presence = new Presence(Presence.Type.unavailable);
	this.setPresence(presence);
    }

    public boolean hasContact(String username) {
	Roster roster = this.connection.getRoster();
	if (roster != null) {

	}

	return false;
    }

    public boolean addContact(String username) {
	Roster roster = this.connection.getRoster();
	if (roster != null) {
	    try {
		RosterEntry entry = roster.getEntry(username);
		if (entry == null) {
		    roster.createEntry(username, null, null);
		}

		return true;
	    } catch (XMPPException e) {
		e.printStackTrace();
		return false;
	    }
	}

	return false;
    }

    private void setPresence(Presence presence) {
	this.connection.sendPacket(presence);
    }

    public void sendMessage(String to, String text) {
	if (this.connection.isAuthenticated()) {
	    Log.i(TAG, "Sending text [" + text + "] to [" + to + "]");
	    Message msg = new Message(to, Message.Type.chat);
	    msg.setBody(text);
	    connection.sendPacket(msg);
	}
    }

    private void broadcastMessage(Intent intent) {
	getBaseContext().sendBroadcast(intent);
    }

    /* LISTENERS */
    private ConnectionListener connectionListener = new ConnectionListener() {
	@Override
	public void reconnectionSuccessful() {
	    onReconnectionSuccessful();
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
	    onReconnectionFailed();
	}

	@Override
	public void reconnectingIn(int arg0) {
	    onReconnectingIn();
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
	    onConnectionClosedOnError();
	}

	@Override
	public void connectionClosed() {
	    onConnectionClosed();
	}
    };

    private ChatManagerListener chatListener = new ChatManagerListener() {
	@Override
	public void chatCreated(final Chat chat, final boolean createdLocally) {
	    onChatCreated(chat, createdLocally);
	}
    };

    private MessageListener messengerListener = new MessageListener() {
	public void processMessage(Chat chat, Message message) {
	    onMessage(chat, message);
	}
    };

    private RosterListener rosterListener = new RosterListener() {
	@Override
	public void presenceChanged(Presence presence) {
	    onPresenceChanged(presence);
	}

	@Override
	public void entriesUpdated(Collection<String> entries) {
	    // onEntriesUpdated(entries);
	}

	@Override
	public void entriesDeleted(Collection<String> entries) {
	    // onEntriesDeleted(entries);
	}

	@Override
	public void entriesAdded(Collection<String> entries) {
	    onEntriesAdded(entries);
	}
    };
}