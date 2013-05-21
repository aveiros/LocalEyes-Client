package com.listbonbigapps.myhoster.client.ui;

import java.util.HashMap;
import java.util.Map.Entry;

import com.listbonbigapps.myhoster.client.R;
import com.listbonbigapps.myhoster.client.request.UserLoginRequest;
import com.listbonbigapps.myhoster.client.resources.UserResource;
import com.listbonbigapps.myhoster.client.util.PreferencesHelper;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    private EditText EtUsername;
    private EditText EtPassword;

    private String username;
    private String password;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.login, menu);
	return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);

	EtUsername = (EditText) findViewById(R.id.username);
	EtPassword = (EditText) findViewById(R.id.password);

	Button BtLogin = (Button) findViewById(R.id.login);
	BtLogin.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		username = EtUsername.getText().toString();
		password = EtPassword.getText().toString();
		performUserLoginRequest(username, password);
	    }
	});

	Log.d(TAG, "onCreate");
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

    private void performUserLoginRequest(String username, String password) {
	UserLoginRequest request = new UserLoginRequest(username, password);
	contentManager.execute(request, new UserRequestListener());
    }

    private void storeSharedPreferences(HashMap<String, String> properties) {
	SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

	for (Entry<String, String> entry : properties.entrySet()) {
	    String key = entry.getKey();
	    String value = entry.getValue();
	    editor.putString(key, value);
	}

	editor.commit();
    }

    private void startContactListActivity() {
	Intent intent = new Intent(getBaseContext(), ContactsActivity.class);
	startActivity(intent);
	finish();
    }

    private class UserRequestListener implements RequestListener<UserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    Toast.makeText(LoginActivity.this, "Error during request: " + e.getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRequestSuccess(UserResource user) {
	    if (user == null || user.getUsername() == null) {
		Toast.makeText(LoginActivity.this, "LOGIN ERROR!", Toast.LENGTH_LONG).show();
		return;
	    }

	    Toast.makeText(LoginActivity.this, "LOGIN SUCCESS!", Toast.LENGTH_LONG).show();

	    HashMap<String, String> preferences = new HashMap<String, String>();
	    preferences.put(PreferencesHelper.Username, username);
	    preferences.put(PreferencesHelper.Password, password);
	    storeSharedPreferences(preferences);

	    startContactListActivity();
	}
    }
}
