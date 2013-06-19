package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.adapter.MessagesListAdapter;
import com.lisbonbigapps.myhoster.client.database.Message;
import com.lisbonbigapps.myhoster.client.database.MessagesDataSource;
import com.lisbonbigapps.myhoster.client.model.ContactMessageModel;
import com.lisbonbigapps.myhoster.client.service.LocalService;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;

public class MessagesFragment extends SherlockListFragment {
    static final String TAG = MessagesFragment.class.toString();
    static final int AB_REFRESH = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View view = inflater.inflate(R.layout.messages_list, container, false);
	return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	menu.add(0, AB_REFRESH, 0, "Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	ActionBar actionBar = getSherlockActivity().getSupportActionBar();
	actionBar.setTitle("Messages");
	actionBar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	this.getMessages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case AB_REFRESH:
	    this.getMessages();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);

	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    ContactMessageModel contact = (ContactMessageModel) getListAdapter().getItem(position);
	    String username = getFragmentActivity().getService().getUsername();

	    String user1 = (username == null ? "" : username);
	    String user2 = contact.getUsername();

	    Bundle args = new Bundle();
	    args.putString("user1", user1);
	    args.putString("user2", user2);

	    Fragment fg = new MessagingFragment();
	    fg.setArguments(args);

	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, fg);
	    transaction.commit();
	}
    }

    private void getMessages() {
	MainActivity activity = this.getFragmentActivity();
	if (activity == null) {
	    return;
	}

	MessagesDataSource dataSource = activity.getMessagesDataSource();
	if (dataSource == null) {
	    return;
	}

	List<Message> messages = dataSource.getAllMessages();
	fillView(messages);
    }

    private void fillView(List<Message> messages) {
	MainActivity activity = this.getFragmentActivity();
	if (activity == null) {
	    return;
	}

	LocalService service = activity.getService();
	if (service == null) {
	    return;
	}

	String username = service.getUsername();
	if (username == null) {
	    return;
	}

	List<ContactMessageModel> contacts = new ArrayList<ContactMessageModel>();
	for (Message message : messages) {
	    String receiver = message.getReceiver();
	    String sender = message.getSender();

	    ContactMessageModel contact = null;
	    if (sender.equals(username)) {
		contact = new ContactMessageModel(receiver);
	    }

	    if (receiver.equals(username)) {
		contact = new ContactMessageModel(sender);
	    }

	    if (contact == null) {
		continue;
	    }

	    if (!contacts.contains(contact)) {
		contacts.add(contact);
	    }
	}

	ContactMessageModel[] storedContactMessages = contacts.toArray(new ContactMessageModel[contacts.size()]);
	ListAdapter listAdapter = new MessagesListAdapter(activity, storedContactMessages);
	setListAdapter(listAdapter);
    }

    public MainActivity getFragmentActivity() {
	return (MainActivity) this.getActivity();
    }
}
