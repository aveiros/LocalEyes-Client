package com.listbonbigapps.myhoster.client.ui;

import com.listbonbigapps.myhoster.client.R;
import com.listbonbigapps.myhoster.client.request.UserLoginRequest;
import com.listbonbigapps.myhoster.client.resources.UserResource;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
		performUserLoginRequest(EtUsername.getText().toString(), EtPassword.getText().toString());
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
    
    private void startChatActivity() {
	Intent intent = new Intent(getBaseContext(), ContactsActivity.class);
	
	/* fake data */
	intent.putExtra("username", "contact.myhoster@gmail.com");
	intent.putExtra("password", "contact12345");

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

	    startChatActivity();
	}
    }

    // private class UserLogoutRequestListener implements
    // RequestListener<MessageResource> {
    // @Override
    // public void onRequestFailure(SpiceException e) {
    // Toast.makeText(LoginActivity.this,
    // "Error during request: " + e.getMessage(),
    // Toast.LENGTH_LONG).show();
    // }
    //
    // @Override
    // public void onRequestSuccess(MessageResource message) {
    // if (message == null) {
    // Toast.makeText(LoginActivity.this, "LOGOUT ERROR!",
    // Toast.LENGTH_LONG).show();
    // return;
    // }
    //
    // Toast.makeText(LoginActivity.this, "LOGOUT SUCCESS!",
    // Toast.LENGTH_LONG).show();
    // }
    // }
}
