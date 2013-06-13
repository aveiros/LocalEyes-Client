package com.lisbonbigapps.myhoster.client.ui;

import java.util.ArrayList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lisbonbigapps.myhoster.client.data.MessageItem;
import com.lisbonbigapps.myhoster.client.data.RosterContact;
import com.lisbonbigapps.myhoster.client.service.LocalService;
import com.lisbonbigapps.myhoster.client.util.MessengerEvents;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;

public class ContactsActivity extends Activity {
    private static final String TAG = ContactsActivity.class.toString();

    private LocalService service;

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
    public boolean onOptionsItemSelected(MenuItem item) {
	int menuItemId = item.getItemId();

	switch (menuItemId) {
	case R.id.action_settings:
	    Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
	    startActivity(intent);
	    break;
	}

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
		connect();
	    }
	});

	BtDisconnect.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		disconnect();
	    }
	});

	this.LvUsers = (ListView) this.findViewById(R.id.users);
	this.LvUsers.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String remoteUser = (String) parent.getAdapter().getItem(position);
		if (remoteUser != null) {
		    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
		    intent.putExtra("remoteUser", remoteUser);
		    startActivity(intent);
		}
	    }
	});

	Button available = (Button) this.findViewById(R.id.available);
	available.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		service.available();
	    }
	});

	Button busy = (Button) this.findViewById(R.id.busy);
	busy.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		service.busy();
	    }
	});

	Button away = (Button) this.findViewById(R.id.away);
	away.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		service.away();
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

    private void unregisterReceiver() {
	// Unregister since the activity is not visible
	this.unregisterReceiver(serviceBroadcastReceiver);
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
	    cleanRosterContacts();
	}
    }

    private void doBindService() {
	this.bindService(new Intent(this, LocalService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
	this.unbindService(serviceConnection);
    }

    private boolean hasBindService() {
	return this.service == null ? false : true;
    }

    private void processRosterContacts(ArrayList<RosterContact> entries) {
	ArrayList<RosterContact> contacts = new ArrayList<RosterContact>();

	/* process here according to user preferences */
	for (RosterContact entry : entries) {
	    String presence = entry.getPresence();
	    if (presence != null) {
		if (presence.equals("available")) {
		    contacts.add(entry);
		}
	    }
	}

	this.addRosterContacts(contacts);
    }

    private void processRosterContact(RosterContact entry) {
	String presence = entry.getPresence();
	if (presence != null) {
	    if (presence.equals("available")) {
		this.addRosterContact(entry);
	    } else if (presence.equals("unavailable")) {
		this.removeRosterContact(entry);
	    } else {
		Log.d(TAG, "unhandled presence type");
	    }
	}
    }

    private void addRosterContacts(ArrayList<RosterContact> entries) {
	for (RosterContact entry : entries) {
	    String username = entry.getUsername();
	    if (this.users.contains(username)) {
		continue;
	    }

	    this.users.add(username);
	}

	this.refreshUsersAdapter();
    }

    private void addRosterContact(RosterContact entry) {
	String username = entry.getUsername();
	if (this.users.contains(username)) {
	    return;
	}

	this.users.add(username);
	this.refreshUsersAdapter();
    }

    private void removeRosterContacts(ArrayList<RosterContact> entries) {
	for (RosterContact entry : entries) {
	    String username = entry.getUsername();
	    this.users.remove(username);
	}

	this.refreshUsersAdapter();
    }

    private void removeRosterContact(RosterContact entry) {
	String username = entry.getUsername();
	this.users.remove(username);
	this.refreshUsersAdapter();
    }

    private void cleanRosterContacts() {
	this.users.clear();
	this.refreshUsersAdapter();
    }

    private void processMessage(MessageItem message) {
	if (message == null) {
	    return;
	}

	// sent it to toolbar
    }

    private void refreshUsersAdapter() {
	this.usersAdapter.notifyDataSetChanged();
    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "ServiceBroadcastReceiver";

	public void onConnected(Intent intent) {
	    Log.d(TAG, MessengerEvents.Connected);
	    BtConnect.setEnabled(false);
	    BtDisconnect.setEnabled(true);
	}

	public void onConnectionError(Intent intent) {
	    Log.d(TAG, MessengerEvents.ConnectionError);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	public void onConnectionClose(Intent intent) {
	    Log.d(TAG, MessengerEvents.ConnectionClose);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	public void onAuthenticated(Intent intent) {
	    Log.d(TAG, MessengerEvents.Authenticated);
	    BtConnect.setEnabled(false);
	    BtDisconnect.setEnabled(true);
	}

	public void onAuthenticationError(Intent intent) {
	    Log.d(TAG, MessengerEvents.AuthenticationError);
	    BtConnect.setEnabled(true);
	    BtDisconnect.setEnabled(false);
	}

	private void onRosterAdd(Intent intent) {
	    ArrayList<RosterContact> contacts = intent.getParcelableArrayListExtra("data");
	    processRosterContacts(contacts);
	    Log.d(TAG, "onContactsAdded");
	}

	private void onRosterUpdate(Intent intent) {
	    // ArrayList<ContactEntry> contacts =
	    // intent.getParcelableArrayListExtra("data");
	    // addContacts(contacts);
	    // Log.d(TAG, "onContactsUpdated");
	}

	private void onRosterRemove(Intent intent) {
	    // ArrayList<ContactEntry> contacts =
	    // intent.getParcelableArrayListExtra("data");
	    // removeContacts(contacts);
	    // Log.d(TAG, "onContactsRemoved");
	}

	private void onRosterContactPresenceChanged(Intent intent) {
	    RosterContact contact = intent.getParcelableExtra("data");
	    processRosterContact(contact);
	    Log.d(TAG, "onContactPresenceChanged");
	}

	private void onMessageReceived(Intent intent) {
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
		} else if (action.equals(MessengerEvents.RosterAdd)) {
		    this.onRosterAdd(intent);
		} else if (action.equals(MessengerEvents.RosterRemove)) {
		    this.onRosterRemove(intent);
		} else if (action.equals(MessengerEvents.RosterUpdate)) {
		    this.onRosterUpdate(intent);
		} else if (action.equals(MessengerEvents.RosterContactPresenceChanged)) {
		    this.onRosterContactPresenceChanged(intent);
		} else if (action.equals(MessengerEvents.MessageReceived)) {
		    this.onMessageReceived(intent);
		}
	    }
	}
    }

    private ServiceBroadcastReceiver serviceBroadcastReceiver = new ServiceBroadcastReceiver();

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