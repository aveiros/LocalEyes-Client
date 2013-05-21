package com.listbonbigapps.myhoster.client.ui;

import java.util.ArrayList;

import com.listbonbigapps.myhoster.client.R;
import com.listbonbigapps.myhoster.client.service.myHosterService;
import com.listbonbigapps.myhoster.client.util.XmppReceiverEvents;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class UserChatActivity extends Activity {
    private static final String TAG = "UserChatActivity";

    private myHosterService service;

    private String toUser = "";
    private ArrayList<String> messages;
    private ArrayAdapter<String> messagesAdapter;

    private ListView LvMessages;
    private EditText Etmessage;
    private Button BtSend;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.user_chat, menu);
	return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_user_chat);

	Bundle extras = this.getIntent().getExtras();
	if (extras != null) {
	    String user = extras.getString("user");
	    this.toUser = user == null ? "" : user;
	}

	BtSend = (Button) findViewById(R.id.chat_send_message);
	BtSend.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		String msg = Etmessage.getText().toString().trim();
		if (msg != null && !msg.equals("")) {
		    sendMessage(msg);
		    clearText();
		}
	    }
	});

	Etmessage = (EditText) findViewById(R.id.chat_input);

	LvMessages = (ListView) findViewById(R.id.chat_messages);
	this.messages = new ArrayList<String>();
	this.messagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.messages);
	LvMessages.setAdapter(messagesAdapter);
    }

    @Override
    protected void onStart() {
	super.onStart();
	doBindService();
	Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
	super.onResume();
	this.registerXmmpUserChatReceiver();
	Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();
	this.disconnectXmpp();
	this.unregisterXmmpUserChatReceiver();
	Log.d(TAG, "onPause");
    }

    private void connectXmpp() {
	if (hasService()) {
	    service.connectXmpp("contact.myhoster@gmail.com", "contact12345");
	}
    }

    private void disconnectXmpp() {
	if (hasService()) {
	    service.disconnectXmpp();
	}
    }

    private void unregisterXmmpUserChatReceiver() {
	this.unregisterReceiver(xmppUserChatReceiver);
    }

    private void registerXmmpUserChatReceiver() {
	/*
	 * NOTE: Broadcasts are fantastic and in addition if you dont want your
	 * broadcast to go beyond your own process then consider using a
	 * LocalBroadcast
	 */

	// Register BroadcastReceiver to receive messages.
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(XmppReceiverEvents.ConnectionError);
	intentFilter.addAction(XmppReceiverEvents.ConnectionOpen);
	intentFilter.addAction(XmppReceiverEvents.ConnectionClose);

	intentFilter.addAction(XmppReceiverEvents.LogInSuccess);
	intentFilter.addAction(XmppReceiverEvents.LogInError);

	intentFilter.addAction(XmppReceiverEvents.RosterChanged);
	intentFilter.addAction(XmppReceiverEvents.UserStatusChanged);
	intentFilter.addAction(XmppReceiverEvents.UserReceivedMessage);
	this.registerReceiver(xmppUserChatReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
	super.onStop();
	doUnbindService();
	Log.d(TAG, "onStop");
    }

    private void sendMessage(String text) {
	if (this.hasService()) {
	    Log.d(TAG, "message sent: " + text);
	    this.addMessage("me: " + text);
	    this.service.sendMessage(this.toUser, text);
	}
    }

    private void addMessage(String text) {
	this.messages.add(text);
	this.messagesAdapter.notifyDataSetChanged();
    }

    private void clearText() {
	this.Etmessage.setText("");
    }

    private void doBindService() {
	bindService(new Intent(this, myHosterService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
	unbindService(serviceConnection);
    }

    private boolean hasService() {
	return service != null;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
	public void onServiceConnected(ComponentName className, IBinder binder) {
	    service = ((myHosterService.ServiceBinder) binder).getService();
	    connectXmpp();
	}

	public void onServiceDisconnected(ComponentName className) {
	    service = null;
	}
    };

    public class XmppUserChatReceiver extends BroadcastReceiver {
	private static final String TAG = "XmppUserChatReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
	    String action = intent.getAction();
	    if (action != null) {
		if (action.equals(XmppReceiverEvents.ConnectionOpen)) {
		    this.ConnectionOpen(intent);
		} else if (action.equals(XmppReceiverEvents.ConnectionError)) {
		    this.ConnectionError(intent);
		} else if (action.equals(XmppReceiverEvents.ConnectionClose)) {
		    this.ConnectionClose(intent);
		} else if (action.equals(XmppReceiverEvents.LogInSuccess)) {
		    this.LogInSuccess(intent);
		} else if (action.equals(XmppReceiverEvents.LogInError)) {
		    this.LogInError(intent);
		} else if (action.equals(XmppReceiverEvents.RosterChanged)) {
		    this.RosterChanged(intent);
		} else if (action.equals(XmppReceiverEvents.UserStatusChanged)) {
		    this.UserStatusChanged(intent);
		} else if (action.equals(XmppReceiverEvents.UserReceivedMessage)) {
		    this.UserReceivedMessage(intent);
		}
	    }
	}

	public void ConnectionOpen(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionOpen);

	}

	public void ConnectionError(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionError);

	}

	public void ConnectionClose(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionClose);

	}

	public void LogInSuccess(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.LogInSuccess);

	}

	public void LogInError(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.LogInError);

	}

	public void RosterChanged(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.RosterChanged);

	}

	public void UserStatusChanged(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.UserStatusChanged);

	}

	public void UserReceivedMessage(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.UserReceivedMessage);

	    Bundle extras = intent.getExtras();
	    if (extras != null) {
		String from = extras.getString("from");
		String message = extras.getString("message");
		if (from != null && message != null) {
		    addMessage(from + ": " + message);
		}
	    }
	}
    }

    private XmppUserChatReceiver xmppUserChatReceiver = new XmppUserChatReceiver();
}