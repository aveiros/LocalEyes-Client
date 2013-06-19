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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.toString();

    private SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    private EditText EtUsername;
    private EditText EtPassword;

    private String username;
    private String password;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
		performAuthentication(username, password);
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

    private void performAuthentication(String username, String password) {
	UserLoginRequest request = new UserLoginRequest(username, password);
	contentManager.execute(request, new UserRequestListener());
    }

    private class UserRequestListener implements RequestListener<UserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    Toast.makeText(LoginActivity.this, "LOGIN ERROR!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRequestSuccess(UserResource user) {
	    if (user == null || user.getUsername() == null) {
		Toast.makeText(LoginActivity.this, "LOGIN ERROR!", Toast.LENGTH_LONG).show();
		return;
	    }

	    Toast.makeText(LoginActivity.this, "LOGIN SUCCESS!", Toast.LENGTH_LONG).show();

	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    SharedPreferences.Editor editor = preferences.edit();

	    editor.putString(PreferencesHelper.Username, username);
	    editor.commit();

	    editor.putString(PreferencesHelper.Password, password).commit();
	    editor.commit();
	    
	    App app = (App) getApplication();
	    app.setUser(user);

	    finish();
	    Intent intent = new Intent(getBaseContext(), MainActivity.class);
	    startActivity(intent);
	}
    }
}
