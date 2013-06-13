package com.lisbonbigapps.myhoster.client.ui;

import com.lisbonbigapps.myhoster.client.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity {
    private static final String TAG = "PreferencesActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);
	
	Log.d(TAG, "onCreate");
    }

}
