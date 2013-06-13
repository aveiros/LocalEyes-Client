package com.lisbonbigapps.myhoster.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.request.UserRequest;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.ContactsActivity;
import com.lisbonbigapps.myhoster.client.ui.TravellerActivity;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class HostProfileFragment extends SherlockFragment {
    private long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
	this.userId = getArguments() == null ? -1 : getArguments().getLong("id", -1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	inflater.inflate(R.menu.profile_menu, menu);
	getSherlockActivity().getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
	    Fragment fragment = fm.findFragmentById(R.id.fragment_content);
	    if (fragment != null) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_content, new HostListFragment());
		ft.commit();
	    }
	}

	return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	
	TravellerActivity activity = (TravellerActivity) this.getActivity();
	UserRequest request = new UserRequest(this.userId);
	activity.getContentManager().execute(request, new UserRequestListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.profile_hoster, container, false);

	Button sendMessage = (Button) v.findViewById(R.id.buttonMsg);
	sendMessage.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onSendMessage();
	    }
	});

	Button makeCall = (Button) v.findViewById(R.id.buttonCall);
	makeCall.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onMakePhoneCall();
	    }
	});

	return v;
    }

    private void onSendMessage() {
	Intent intent = new Intent(getSherlockActivity().getBaseContext(), ContactsActivity.class);
	startActivity(intent);
    }

    private void onMakePhoneCall() {
	// make a phone call
    }

    private void drawProfile(UserResource user) {
	String title = String.format("<font color='#ffffff'>%s</font>", user.getName());
	getSherlockActivity().getSupportActionBar().setTitle(Html.fromHtml(title));
    }

    private class UserRequestListener implements RequestListener<UserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	}

	@Override
	public void onRequestSuccess(UserResource user) {
	    if (user == null) {
		return;
	    }

	    drawProfile(user);
	}
    }
}