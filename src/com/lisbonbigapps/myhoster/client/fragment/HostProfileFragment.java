package com.lisbonbigapps.myhoster.client.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.request.UserRequest;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.TravellerActivity;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class HostProfileFragment extends SherlockFragment {
    private UserResource user;
    private long userId;
    private View view;

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
	this.view = inflater.inflate(R.layout.profile_hoster, container, false);

	View v = view.findViewById(R.id.buttonBook);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onBookNow();
	    }
	});

	v = view.findViewById(R.id.buttonContact);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onContact();
	    }
	});

	return view;
    }

    private void onContact() {
	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    String username = getBaseActivity(TravellerActivity.class).getService().getUsername();

	    String user1 = (username == null ? "" : username);
	    // TODO: HARD CODED
	    String user2 = user.getUsername() + "@localhost";

	    Bundle args = new Bundle();
	    args.putString("user1", user1);
	    args.putString("user2", user2);

	    Fragment fg = new MessagingFragment();
	    fg.setArguments(args);

	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, fg);
	    transaction.commit();
	}
    }

    private void onBookNow() {
	// start booking activity
    }

    private void fillView(UserResource user) {
	this.user = user;

	TextView textName = (TextView) view.findViewById(R.id.profileLocalName);
	TextView textFeedback = (TextView) view.findViewById(R.id.textView4);
	TextView textStatus = (TextView) view.findViewById(R.id.profileLocalTextViewStatus);
	TextView textLocation = (TextView) view.findViewById(R.id.profileLocalTextViewLocation);
	TextView textDistance = (TextView) view.findViewById(R.id.profileLocalTextViewDistance);
	TextView textDescription = (TextView) view.findViewById(R.id.textView2);
	TextView textPOI = (TextView) view.findViewById(R.id.closebyPoiName);
	TextView textPOIdistance = (TextView) view.findViewById(R.id.textView5);

	textName.setText(user.getName());
	textFeedback.setText("(" + user.getService().getVotes() + ")");
	textStatus.setText("Online");
	textLocation.setText("");
	textDistance.setText("");
	textDescription.setText(user.getService().getDescription() + "I can show you the GARDENS around the Island. During the visit to the gardens I can take you also to the best tea houses");
	textPOI.setText("" + "LISBON (OLD CITY CENTER)");
	textPOIdistance.setText("" + "Its around 200M from you.");
    }

    @SuppressWarnings("unchecked")
    public <T extends Activity> T getBaseActivity(Class<T> clazz) {
	return (T) this.getActivity();
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

	    fillView(user);
	}
    }
}