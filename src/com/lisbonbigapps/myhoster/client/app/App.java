package com.lisbonbigapps.myhoster.client.app;

import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.service.LocalService;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class App extends Application {
    private static final String TAG = App.class.toString();
    private UserResource user;

    @Override
    public void onCreate() {
	super.onCreate();
	Intent serviceIntent = new Intent(getApplicationContext(), LocalService.class);
	this.startService(serviceIntent);
	Log.d(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
	super.onTerminate();
	Intent serviceIntent = new Intent(getApplicationContext(), LocalService.class);
	this.stopService(serviceIntent);
	Log.d(TAG, "onTerminate");
    }

    public UserResource getUser() {
	return user;
    }

    public void setUser(UserResource user) {
	this.user = user;
    }

    public boolean hasUser() {
	return user != null;
    }
}
