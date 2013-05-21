package com.listbonbigapps.myhoster.client;

import com.listbonbigapps.myhoster.client.service.myHosterService;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
	super.onCreate();
	Intent serviceIntent = new Intent(getApplicationContext(), myHosterService.class);
	this.startService(serviceIntent);
	Log.d(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
	super.onTerminate();
	Intent serviceIntent = new Intent(getApplicationContext(), myHosterService.class);
	this.stopService(serviceIntent);
	Log.d(TAG, "onTerminate");
    }
}
