package com.lisbonbigapps.myhoster.client.ui;

import com.lisbonbigapps.myhoster.client.app.App;
import com.lisbonbigapps.myhoster.client.request.UserLoginRequest;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class SplashActivity extends Activity {
    private final String TAG = SplashActivity.class.toString();

    private final SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    private boolean authenticating = true;
    private boolean authenticationSuccess = false;
    private int splashDuration = 0;
    private int splashTime = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.splash_screen);

	this.splashInit();
    }

    @Override
    protected void onStart() {
	super.onStart();
	getContentManager().start(this);
	this.performAuthentication();
    }

    @Override
    protected void onStop() {
	getContentManager().shouldStop();
	super.onStop();
    }

    protected void splashInit() {
	Thread thread = new Thread() {
	    @Override
	    public void run() {
		try {
		    while (authenticating || (splashDuration < splashTime)) {
			android.os.SystemClock.sleep(200);
			splashDuration = splashDuration + 200;
		    }
		} catch (Exception e) {
		    Log.e(TAG, e.getMessage());
		} finally {
		    if (authenticationSuccess) {
			authenticationSuccess();
		    } else {
			authenticationError();
		    }
		}
	    }
	};

	thread.start();
    }

    protected void performAuthentication() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	String username = preferences.getString(PreferencesHelper.Username, "");
	String password = preferences.getString(PreferencesHelper.Password, "");

	if (username.equals("") || password.equals("")) {
	    authenticationSuccess = false;
	    authenticating = false;
	    return;
	}

	UserLoginRequest request = new UserLoginRequest(username, password);
	getContentManager().execute(request, new UserLoginRequestListener());
    }

    protected void authenticationSuccess() {
	Intent intent = new Intent(getBaseContext(), TravellerActivity.class);
	startActivity(intent);
	finish();
    }

    protected void authenticationError() {
	Intent intent = new Intent(getBaseContext(), LoginActivity.class);
	startActivity(intent);
	finish();
    }

    private class UserLoginRequestListener implements RequestListener<UserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    authenticationSuccess = false;
	    authenticating = false;
	}

	@Override
	public void onRequestSuccess(UserResource user) {
	    if (user == null || user.getUsername() == null) {
		return;
	    }
	    
	    App app = (App) getApplication();
	    app.setUser(user);

	    authenticationSuccess = true;
	    authenticating = false;
	}
    }

    public SpiceManager getContentManager() {
	return contentManager;
    }
}
