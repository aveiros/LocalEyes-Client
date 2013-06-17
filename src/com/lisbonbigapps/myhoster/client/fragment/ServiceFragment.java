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

public class ServiceFragment extends SherlockFragment {
    private long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
	this.userId = getArguments() == null ? 0 : getArguments().getLong("id", 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	// inflater.inflate(R.menu.profile_menu, menu);
	// getSherlockActivity().getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	TravellerActivity activity = (TravellerActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.service, container, false);

	return v;
    }
}