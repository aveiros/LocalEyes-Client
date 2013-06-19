package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.adapter.MessagingListAdapter;
import com.lisbonbigapps.myhoster.client.data.MessageItem;
import com.lisbonbigapps.myhoster.client.database.Message;
import com.lisbonbigapps.myhoster.client.database.MessagesDataSource;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.lisbonbigapps.myhoster.client.util.MessengerEvents;

public class MessagingFragment extends SherlockListFragment {
    private ServiceBroadcastReceiver serviceBroadcastReceiver = new ServiceBroadcastReceiver();

    private List<String> messages = new ArrayList<String>();

    private EditText EtMessage;
    private Button BtSend;

    private String user1;
    private String user2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);

	Bundle args = this.getArguments();
	if (args != null) {
	    this.user1 = args.getString("user1", "");
	    this.user2 = args.getString("user2", "");
	} else {
	    this.user1 = "";
	    this.user2 = "";
	}

	if (!this.user2.equals("")) {
	    this.getBaseActivity(MainActivity.class).getService().addContact(user2);
	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View view = inflater.inflate(R.layout.messaging_list, container, false);

	BtSend = (Button) view.findViewById(R.id.chat_send_message);
	BtSend.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		String msg = EtMessage.getText().toString().trim();
		if (msg != null && !msg.equals("")) {
		    clearTextInput();
		    sendMessage(msg);
		}
	    }
	});

	EtMessage = (EditText) view.findViewById(R.id.chat_input);

	return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);

	ActionBar actionBar = getSherlockActivity().getSupportActionBar();
	actionBar.setTitle("Messaging");
	actionBar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	MessagesDataSource dataSource = this.getBaseActivity(MainActivity.class).getMessagesDataSource();
	List<Message> messages = dataSource.getMessagesWith(this.user1, this.user2, 10);
	fillView(messages);
    }

    private void fillView(List<Message> messages) {
	List<String> msgs = new ArrayList<String>();

	for (Message message : messages) {
	    String username = message.getSender().split("@")[0];
	    msgs.add(username + ": " + message);
	}

	this.messages = msgs;
	refreshMessages();
    }

    @Override
    public void onResume() {
	super.onResume();
	this.registerReceiver();
    }

    @Override
    public void onPause() {
	super.onPause();
	this.unregisterReceiver();
    }

    private void unregisterReceiver() {
	this.getActivity().unregisterReceiver(this.serviceBroadcastReceiver);
    }

    private void registerReceiver() {
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

	this.getActivity().registerReceiver(this.serviceBroadcastReceiver, intentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return true;
    }

    private void processMessage(MessageItem message) {
	if (message == null) {
	    return;
	}

	if (message.getUsername().equals(this.user2)) {
	    addMessage(message.getUsername(), message.getText());
	}
    }

    private void sendMessage(String message) {
	this.addMessage(user1, message);
	this.getBaseActivity(MainActivity.class).sendMessage(this.user2, message);
    }

    private void addMessage(String user, String message) {
	String username = user.split("@")[0];
	this.messages.add(username + ": " + message);
	this.refreshMessages();
    }

    private void clearTextInput() {
	this.EtMessage.setText("");
    }

    private void refreshMessages() {
	String[] messages = this.messages.toArray(new String[this.messages.size()]);
	ListAdapter listAdapter = new MessagingListAdapter(getActivity(), messages);
	setListAdapter(listAdapter);
    }

    @SuppressWarnings("unchecked")
    public <T extends Activity> T getBaseActivity(Class<T> clazz) {
	return (T) this.getActivity();
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = ServiceBroadcastReceiver.class.toString();

	private void onConnected(Intent intent) {
	}

	private void onConnectionClose(Intent intent) {
	}

	private void onConnectionError(Intent intent) {
	}

	private void onAuthenticated(Intent intent) {
	}

	private void onAuthenticationError(Intent intent) {
	}

	private void onRosterContactPresenceChanged(Intent intent) {
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
}