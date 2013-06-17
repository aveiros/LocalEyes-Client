package com.lisbonbigapps.myhoster.client.ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lisbonbigapps.myhoster.client.adapter.SuggestionsAdapter;
import com.lisbonbigapps.myhoster.client.fragment.HostListFragment;
import com.lisbonbigapps.myhoster.client.fragment.HostProfileFragment;
import com.lisbonbigapps.myhoster.client.fragment.SlidingMenuFragment;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

public class TravellerActivity extends SlidingFragmentActivity implements HostListFragment.OnHostSelectedListener {

    private final SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

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

	// set the Behind View
	setBehindContentView(R.layout.menu_frame);
	if (savedInstanceState == null) {
	    FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
	    mFrag = new SlidingMenuFragment();
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
	    ft.add(R.id.fragment_content, new HostListFragment());
	    ft.commit();
	}

	// ACTION BAR
	BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_bg);
	bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	getSupportActionBar().setBackgroundDrawable(bg);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setTitle("");
	getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    protected void onStart() {
	super.onStart();
	contentManager.start(this);
    }

    @Override
    protected void onStop() {
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
}