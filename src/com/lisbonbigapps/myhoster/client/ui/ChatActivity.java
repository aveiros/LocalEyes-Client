package com.lisbonbigapps.myhoster.client.ui;

import java.util.ArrayList;

import com.lisbonbigapps.myhoster.client.data.MessageItem;
import com.lisbonbigapps.myhoster.client.data.RosterContact;
import com.lisbonbigapps.myhoster.client.service.LocalService;
import com.lisbonbigapps.myhoster.client.util.MessengerEvents;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;

import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
    private final String TAG = ChatActivity.class.toString();

    private LocalService service;

    private String remoteUser;

    private ArrayList<String> messages;
    private ArrayAdapter<String> messagesAdapter;

    private ListView LvMessages;
    private EditText EtMessage;
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

	this.remoteUser = this.getIntent().getStringExtra("remoteUser");

	BtSend = (Button) findViewById(R.id.chat_send_message);
	BtSend.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		String msg = EtMessage.getText().toString().trim();
		if (msg != null && !msg.equals("")) {
		    clearText();
		    sendMessage(msg);
		}
	    }
	});

	EtMessage = (EditText) findViewById(R.id.chat_input);

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
	this.registerReceiver();
	Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();
	this.unregisterReceiver();
	Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
	super.onStop();
	doUnbindService();
	Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "onDestroy");
    }

    private void connect() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	String username = preferences.getString(PreferencesHelper.Username, "");
	String password = preferences.getString(PreferencesHelper.Password, "");

	if (username == "" || password == "") {
	    return;
	}

	if (hasBindService()) {
	    service.connect(username, password);
	}
    }

    private void disconnect() {
	if (hasBindService()) {
	    service.disconnect();
	}
    }

    private void unregisterReceiver() {
	this.unregisterReceiver(serviceBroadcastReceiver);
    }

    private void registerReceiver() {
	/*
	 * NOTE: Broadcasts are fantastic and in addition if you dont want your
	 * broadcast to go beyond your own process then consider using a
	 * LocalBroadcast
	 */

	// Register BroadcastReceiver to receive messages.
	IntentFilter intentFilter = new IntentFilter();

	intentFilter.addAction(MessengerEvents.Connected);
	intentFilter.addAction(MessengerEvents.ConnectionError);
	intentFilter.addAction(MessengerEvents.ConnectionClose);

	intentFilter.addAction(MessengerEvents.Authenticated);
	intentFilter.addAction(MessengerEvents.AuthenticationError);

	intentFilter.addAction(MessengerEvents.RosterAdd);
	intentFilter.addAction(MessengerEvents.RosterRemove);
	intentFilter.addAction(MessengerEvents.RosterUpdate);
	intentFilter.addAction(MessengerEvents.RosterContactPresenceChanged);

	intentFilter.addAction(MessengerEvents.MessageReceived);

	this.registerReceiver(serviceBroadcastReceiver, intentFilter);
    }

    private void sendMessage(String message) {
	this.addMessage("me", message);
	this.service.sendMessage(this.remoteUser, message);
    }

    private void addMessage(String user, String message) {
	this.messages.add(user + ": " + message);
	this.messagesAdapter.notifyDataSetChanged();
    }

    private void clearText() {
	this.EtMessage.setText("");
    }

    private void doBindService() {
	bindService(new Intent(this, LocalService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
	unbindService(serviceConnection);
    }

    private boolean hasBindService() {
	return this.service == null ? false : true;
    }

    private void processMessage(MessageItem message) {
	if (message == null) {
	    return;
	}

	String sender = message.getName() == null ? message.getUsername() : message.getName();
	if (message.getUsername().equals(remoteUser)) {
	    addMessage(sender, message.getText());
	} else {
	    // sent it to toolbar
	}
    }

    private ServiceBroadcastReceiver serviceBroadcastReceiver = new ServiceBroadcastReceiver();

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = ServiceBroadcastReceiver.class.toString();

	private void onConnectionClose(Intent intent) {
	}

	private void onConnectionError(Intent intent) {
	}

	private void onConnected(Intent intent) {
	}

	private void onAuthenticationError(Intent intent) {
	}

	private void onAuthenticated(Intent intent) {
	}

	private void onRosterContactPresenceChanged(Intent intent) {
	    RosterContact entry = intent.getParcelableExtra("data");
	    if (entry != null) {
		String local = service.getUsername();
		String remote = remoteUser;

		if (local == null) {
		    return;
		}

		if (entry.getUsername().equals(local)) {
		    Log.d(TAG, "local user changed status");
		} else if (entry.getUsername().equals(remote)) {
		    Log.d(TAG, "remote user changed status");
		}
	    }
	}

	private void onMessageReceived(Intent intent) {
	    Log.d(TAG, MessengerEvents.MessageReceived);
	    MessageItem message = intent.getParcelableExtra("data");
	    processMessage(message);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    String action = intent.getAction();
	    if (action != null) {
		if (action.equals(MessengerEvents.Connected)) {
		    this.onConnected(intent);
		} else if (action.equals(MessengerEvents.ConnectionError)) {
		    this.onConnectionError(intent);
		} else if (action.equals(MessengerEvents.ConnectionClose)) {
		    this.onConnectionClose(intent);
		} else if (action.equals(MessengerEvents.Authenticated)) {
		    this.onAuthenticated(intent);
		} else if (action.equals(MessengerEvents.AuthenticationError)) {
		    this.onAuthenticationError(intent);
		} else if (action.equals(MessengerEvents.RosterContactPresenceChanged)) {
		    this.onRosterContactPresenceChanged(intent);
		} else if (action.equals(MessengerEvents.MessageReceived)) {
		    this.onMessageReceived(intent);
		}
	    }
	}
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
	public void onServiceConnected(ComponentName className, IBinder binder) {
	    service = ((LocalService.ServiceBinder) binder).getService();
	    connect();
	}

	public void onServiceDisconnected(ComponentName className) {
	    service = null;
	}
    };
}