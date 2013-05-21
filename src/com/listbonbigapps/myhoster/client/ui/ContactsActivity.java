package com.listbonbigapps.myhoster.client.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.listbonbigapps.myhoster.client.R;
import com.listbonbigapps.myhoster.client.service.myHosterService;
import com.listbonbigapps.myhoster.client.util.XmppReceiverEvents;

public class ContactsActivity extends Activity {
    private static final String TAG = "ContactsActivity";

    private myHosterService service;

    private ArrayList<String> users;
    private ArrayAdapter<String> usersAdapter;

    private Button BtConnect;
    private Button BtDisconnect;
    private ListView LvUsers;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.contacts, menu);
	return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_contacts);

	BtConnect = (Button) this.findViewById(R.id.connect);
	BtDisconnect = (Button) this.findViewById(R.id.disconnect);

	BtConnect.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		connectXmpp();
	    }
	});

	BtDisconnect.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		disconnectXmpp();
	    }
	});

	this.LvUsers = (ListView) this.findViewById(R.id.users);
	this.LvUsers.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "item click");
		String user = (String) parent.getAdapter().getItem(position);

		if (user != null) {
		    Intent intent = new Intent(getApplicationContext(), UserChatActivity.class);
		    intent.putExtra("user", user);
		    startActivity(intent);
		    //finish();
		}
	    }
	});

	this.users = new ArrayList<String>();
	this.usersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.users);
	this.LvUsers.setAdapter(usersAdapter);

	Log.d(TAG, "onCreate");
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
	this.registerXmmpReceiver();
	Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
	super.onPause();
	this.disconnectXmpp();
	this.unregisterXmmpReceiver();
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

    private void registerXmmpReceiver() {
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
	this.registerReceiver(xmppContactsReceiver, intentFilter);
    }

    private void unregisterXmmpReceiver() {
	// Unregister since the activity is not visible
	this.unregisterReceiver(xmppContactsReceiver);
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

    private void doBindService() {
	bindService(new Intent(this, myHosterService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
	unbindService(serviceConnection);
    }

    private void addUsers(String[] add) {
	for (String user : add) {
	    if (this.users.contains(user)) {
		continue;
	    }
	    this.users.add(user);
	}

	refreshUsers();
    }

    private void deleteUsers(String[] remove) {
	for (String user : remove) {
	    if (this.users.contains(user)) {
		this.users.remove(user);
	    }
	}

	refreshUsers();
    }

    private void refreshUsers() {
	this.usersAdapter.notifyDataSetChanged();
    }

    private boolean hasService() {
	return service != null;
    }

    public class XmppContactsReceiver extends BroadcastReceiver {
	private static final String TAG = "XmppReceiver";

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
		}
	    }
	}

	public void ConnectionOpen(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionOpen);
	    BtConnect.setEnabled(false);
	    BtDisconnect.setEnabled(true);
	}

	public void ConnectionError(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionError);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	public void ConnectionClose(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.ConnectionClose);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	public void LogInSuccess(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.LogInSuccess);
	    BtConnect.setEnabled(false);
	    BtDisconnect.setEnabled(true);
	}

	public void LogInError(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.LogInError);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	public void RosterChanged(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.RosterChanged);

	    Bundle extras = intent.getExtras();
	    if (extras != null) {
		String action = extras.getString("action");
		String[] data = extras.getStringArray("data");

		if (action != null && data != null) {
		    Log.d(TAG, XmppReceiverEvents.RosterChanged + " action: " + action);

		    if (action.equals("add") || action.equals("update")) {
			addUsers(data);
		    } else if (action.equals("delete")) {
			deleteUsers(data);
		    }
		}
	    }
	}

	public void UserStatusChanged(Intent intent) {
	    Log.d(TAG, XmppReceiverEvents.UserStatusChanged);

	    Bundle extras = intent.getExtras();
	    if (extras != null) {
		String user = extras.getString("user");
		String status = extras.getString("status");
		if (user != null && status != null) {
		    Log.d(TAG, user + " is now: " + status);

		    String[] users = new String[] { user };
		    if (status.equals("available")) {
			addUsers(users);
		    } else if (status.equals("unavailable")) {
			deleteUsers(users);
		    } else {
			Log.e(TAG, "status unknown");
		    }

		}
	    }
	}
    }

    private XmppContactsReceiver xmppContactsReceiver = new XmppContactsReceiver();

    private ServiceConnection serviceConnection = new ServiceConnection() {
	public void onServiceConnected(ComponentName className, IBinder binder) {
	    service = ((myHosterService.ServiceBinder) binder).getService();
	    connectXmpp();
	}

	public void onServiceDisconnected(ComponentName className) {
	    service = null;
	}
    };
}
