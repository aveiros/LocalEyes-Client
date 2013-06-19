package com.lisbonbigapps.myhoster.client.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.request.ServiceRequest;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class ServiceFragment extends SherlockFragment {
    View view;

    long serviceId;
    ServiceResource serviceResource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);

	Bundle args = this.getArguments();
	if (args != null) {
	    this.serviceId = args.getLong("id", -1);
	}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);

	ActionBar actionBar = getSherlockActivity().getSupportActionBar();
	actionBar.setTitle("Service Information");
	actionBar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	MainActivity activity = (MainActivity) this.getActivity();
	ServiceRequest request = new ServiceRequest(this.serviceId);
	activity.getContentManager().execute(request, new ServiceRequestListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);

	view = inflater.inflate(R.layout.service, container, false);

	View iv = view.findViewById(R.id.buttonServiceCall);
	iv.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		OnCallHost();
	    }
	});

	iv = view.findViewById(R.id.buttonServiceSendMessage);
	iv.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		OnSendMessage();
	    }
	});

	return view;
    }

    protected void OnSendMessage() {
	if (this.serviceResource == null) {
	    return;
	}

	UserResource user = this.serviceResource.getHoster();

	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    String username = ((MainActivity) getActivity()).getService().getUsername();

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

    protected void OnCallHost() {
	if (this.serviceResource == null) {
	    return;
	}

	UserResource host = this.serviceResource.getHoster();
	if (host == null) {
	    return;
	}

	String number = String.format("tel:%s", host.getPhoneNumber());
	Intent callIntent = new Intent(Intent.ACTION_DIAL);
	callIntent.setData(Uri.parse(number));
	startActivity(callIntent);
    }

    protected void fillView(ServiceResource service) {
	this.serviceResource = service;

	UserResource host = service.getHoster();

	TextView textName = (TextView) view.findViewById(R.id.profileLocalName);
	TextView textFeedback = (TextView) view.findViewById(R.id.textView4);
	TextView textStatus = (TextView) view.findViewById(R.id.profileLocalTextViewStatus);
	TextView textLocation = (TextView) view.findViewById(R.id.profileLocalTextViewLocation);
	TextView textDistance = (TextView) view.findViewById(R.id.profileLocalTextViewDistance);
	TextView textDescription = (TextView) view.findViewById(R.id.textView2);
	TextView textPOI = (TextView) view.findViewById(R.id.closebyPoiName);
	TextView textPOIdistance = (TextView) view.findViewById(R.id.textView5);

	textName.setText(host.getName());
	textFeedback.setText("(" + host.getService().getVotes() + ")");
	textStatus.setText("Online");
	textLocation.setText("");
	textDistance.setText("");
	textDescription.setText(host.getService().getDescription() + "I can show you the GARDENS around the Island. During the visit to the gardens I can take you also to the best tea houses");
	textPOI.setText("" + "LISBON (OLD CITY CENTER)");
	textPOIdistance.setText("" + "Its around 200M from you.");
    }

    private class ServiceRequestListener implements RequestListener<ServiceResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	}

	@Override
	public void onRequestSuccess(ServiceResource service) {
	    if (service == null) {
		return;
	    }

	    fillView(service);
	}
    }
}