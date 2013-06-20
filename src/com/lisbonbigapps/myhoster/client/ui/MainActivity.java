package com.lisbonbigapps.myhoster.client.ui;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lisbonbigapps.myhoster.client.adapter.SuggestionsAdapter;
import com.lisbonbigapps.myhoster.client.app.App;
import com.lisbonbigapps.myhoster.client.data.MessageItem;
import com.lisbonbigapps.myhoster.client.data.RosterContact;
import com.lisbonbigapps.myhoster.client.database.MessagesDataSource;
import com.lisbonbigapps.myhoster.client.fragment.HostListFragment;
import com.lisbonbigapps.myhoster.client.fragment.HostProfileFragment;
import com.lisbonbigapps.myhoster.client.fragment.LocalServiceListFragment;
import com.lisbonbigapps.myhoster.client.fragment.LocalSlidingMenuFragment;
import com.lisbonbigapps.myhoster.client.fragment.TouristSlidingMenuFragment;
import com.lisbonbigapps.myhoster.client.service.LocalService;
import com.lisbonbigapps.myhoster.client.service.LocalTracker;
import com.lisbonbigapps.myhoster.client.util.MessengerEvents;
import com.lisbonbigapps.myhoster.client.util.Mode;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

public class MainActivity extends SlidingFragmentActivity implements HostListFragment.OnHostSelectedListener {
    private final String TAG = MainActivity.class.toString();

    private final SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    private LocalService service;
    private LocalTracker tracker;

    private MessagesDataSource messagesDataSource;

    private static final String[] COLUMNS = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1 };

    private int mTitleRes;
    protected Fragment mFrag;
    public static Context appContext;
    private MenuItem menuItem;
    private SuggestionsAdapter mSuggestionsAdapter;

    // Progress Bar -------------------------------
    private int mProgress = 100;
    final Handler mHandler = new Handler();
    final Runnable mProgressRunner = new Runnable() {
	@Override
	public void run() {
	    mProgress += 2;

	    // Normalize our progress along the progress bar's scale
	    int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * mProgress;
	    setSupportProgress(progress);

	    if (mProgress < 100) {
		mHandler.postDelayed(mProgressRunner, 50);
	    }
	}
    };

    // Progress Bar -------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setMessagesDataSource(new MessagesDataSource(this));

	App app = (App) getApplication();
	String mode = app.getMode();

	// ACTION BAR
	// BitmapDrawable bg = (BitmapDrawable)
	// getResources().getDrawable(R.drawable.ab_bg);
	// bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	// getSupportActionBar().setBackgroundDrawable(bg);

	ActionBar bar = getSupportActionBar();
	bar.setDisplayHomeAsUpEnabled(true);
	bar.setTitle("");
	// bar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
	// this.getMessagesDataSource().drop();

	// set the Behind View
	setBehindContentView(R.layout.menu_frame);
	if (savedInstanceState == null) {
	    FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();

	    if (mode.equals(Mode.TOURIST)) {
		mFrag = new TouristSlidingMenuFragment();
	    } else {
		mFrag = new LocalSlidingMenuFragment();
	    }

	    t.replace(R.id.menu_frame, mFrag);
	    t.commit();
	} else {
	    mFrag = (Fragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
	}

	// customize the SlidingMenu
	SlidingMenu sm = getSlidingMenu();
	sm.setShadowWidthRes(R.dimen.shadow_width);
	sm.setShadowDrawable(R.drawable.shadow);
	sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	sm.setFadeDegree(0.35f);
	sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

	// We default to building our Fragment at runtime
	setContentView(R.layout.activity_fragment_runtime);
	FragmentManager fm = getSupportFragmentManager();
	Fragment fragment = fm.findFragmentById(R.id.fragment_content);

	if (fragment == null) {
	    FragmentTransaction ft = fm.beginTransaction();
	    Fragment fg;

	    if (mode.equals(Mode.TOURIST)) {
		fg = new HostListFragment();
	    } else {
		fg = new LocalServiceListFragment();
	    }

	    ft.add(R.id.fragment_content, fg);
	    ft.commit();
	}
    }

    @Override
    protected void onStart() {
	super.onStart();
	contentManager.start(this);
	doBindService();
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
	doUnbindService();
	contentManager.shouldStop();
	super.onStop();
    }

    // Show Sliding Menu Trick
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// toggle();
	// }
	// }, 10);
    }

    protected void updateHostsList(String newText) {
	// Progress Bar
	if (mProgress == 100) {
	    mProgress = 0;
	    mProgressRunner.run();
	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// Intent intent = new Intent();
	// intent.setClass(this, SelectionModeActivity.class);
	// startActivity(intent);
	// return (true);
	// }
	// return false;
	// // more code here for other cases
	return false;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public class TestTask extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... params) {
	    // Simulate something long running
	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    return null;
	}

	@Override
	protected void onPostExecute(String result) {
	    menuItem.collapseActionView();
	    menuItem.setActionView(null);
	}
    }

    public SpiceManager getContentManager() {
	return contentManager;
    }

    @Override
    public void onHostSelected(long hostId) {
	FragmentManager manager = getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment profile = new HostProfileFragment();
	    Bundle args = new Bundle();
	    args.putLong("id", hostId);
	    profile.setArguments(args);

	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, profile);
	    transaction.commit();
	}
    }

    public MessagesDataSource getMessagesDataSource() {
	return messagesDataSource;
    }

    public void setMessagesDataSource(MessagesDataSource messagesDataSource) {
	this.messagesDataSource = messagesDataSource;
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

    public void sendMessage(String to, String text) {
	if (hasBindService()) {
	    MessagesDataSource dataSource = this.getMessagesDataSource();
	    String username = this.service.getUsername();
	    dataSource.addMessage(username == null ? "" : username, to, text);

	    this.service.sendMessage(to, text);
	}
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

	MessagesDataSource dataSource = this.getMessagesDataSource();
	String username = this.service.getUsername();
	dataSource.addMessage(message.getUsername(), username == null ? "" : username, message.getText());

	String sender = message.getName() == null ? message.getUsername() : message.getName();
	// if (message.getUsername().equals(remoteUser)) {
	// addMessage(sender, message.getText());
	// } else {
	// // sent it to toolbar
	// }
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
		// String remote = remoteUser;
		//
		// if (entry.getUsername().equals(local)) {
		// Log.d(TAG, "local user changed status");
		// } else if (entry.getUsername().equals(remote)) {
		// Log.d(TAG, "remote user changed status");
		// }
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

    public LocalService getService() {
	return service;
    }

    public void setService(LocalService service) {
	this.service = service;
    }

    public LocalTracker getTracker() {
	return tracker;
    }

    public void setTracker(LocalTracker tracker) {
	this.tracker = tracker;
    }
}