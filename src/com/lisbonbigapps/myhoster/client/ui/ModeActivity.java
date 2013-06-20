package com.lisbonbigapps.myhoster.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.util.Mode;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;

public class ModeActivity extends Activity {
    protected static final String TAG = ModeActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.selection_screen);

	View view = findViewById(R.id.imageViewLocal);
	view.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		updateMode(Mode.LOCAL);
		startMainActivity();
	    }
	});

	view = findViewById(R.id.imageViewTourist);
	view.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		updateMode(Mode.TOURIST);
		startMainActivity();
	    }
	});
    }

    protected void updateMode(String mode) {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	SharedPreferences.Editor editor = preferences.edit();

	editor.putString(PreferencesHelper.Mode, mode);
	editor.commit();
    }

    protected void startMainActivity() {
	finish();

	Intent intent = new Intent(getBaseContext(), MainActivity.class);
	startActivity(intent);
    }
}