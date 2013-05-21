package com.listbonbigapps.myhoster.client.ui;

import com.listbonbigapps.myhoster.client.R;
import com.listbonbigapps.myhoster.client.request.UserLoginRequest;
import com.listbonbigapps.myhoster.client.resources.UserResource;
import com.listbonbigapps.myhoster.client.util.PreferencesHelper;
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

public class WelcomeActivity extends Activity {
    private static final String TAG = "WelcomeActivity";

    private SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_welcome);
	Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
	super.onStart();
	contentManager.start(this);

	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	String username = preferences.getString(PreferencesHelper.Username, "");
	String password = preferences.getString(PreferencesHelper.Password, "");

	if (username == "" && password == "") {
	    Log.d(TAG, "login preferences not available");
	    finishAndStartActivity(LoginActivity.class);
	} else {
	    Log.d(TAG, "performing login with existing preferences");
	    UserLoginRequest request = new UserLoginRequest(username, password);
	    contentManager.execute(request, new UserRequestListener());
	}
    }

    @Override
    protected void onStop() {
	contentManager.shouldStop();
	super.onStop();
    }

    private void finishAndStartActivity(Class<?> classType) {
	Intent intent = new Intent(getBaseContext(), classType);
	startActivity(intent);
	finish();
    }

    private class UserRequestListener implements RequestListener<UserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    finishAndStartActivity(LoginActivity.class);
	}

	@Override
	public void onRequestSuccess(UserResource user) {
	    if (user == null || user.getUsername() == null) {
		return;
	    }
	    
	    finishAndStartActivity(ContactsActivity.class);
	}
    }
}
