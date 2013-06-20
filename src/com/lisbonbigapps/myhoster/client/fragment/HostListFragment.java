package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.adapter.HostsModelListAdapter;
import com.lisbonbigapps.myhoster.client.model.HosterModel;
import com.lisbonbigapps.myhoster.client.request.HostsAroundRequest;
import com.lisbonbigapps.myhoster.client.resources.ListUserResource;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.service.LocalTracker;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class HostListFragment extends SherlockListFragment {
    static final String TAG = HostListFragment.class.toString();
    static final int AB_REFRESH = 1;

    int host_distance = 3000;

    private OnHostSelectedListener mCallback;
    boolean isDualPane;
    int mCurCheckPosition = 0;
    public static final String ARG_SECTION_NUMBER = "BrowseHostersListFragment";
    public ListView mainList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View view = inflater.inflate(R.layout.fragment_hosters_list, container, false);
	return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	menu.add(0, AB_REFRESH, 0, "Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	ActionBar actionBar = getSherlockActivity().getSupportActionBar();
	actionBar.setTitle("Around You");
	actionBar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);

	try {
	    mCallback = (OnHostSelectedListener) activity;
	} catch (ClassCastException e) {
	    throw new ClassCastException(activity.toString() + " must implement " + OnHostSelectedListener.class.getSimpleName());
	}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	this.getHostsAround();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case AB_REFRESH:
	    this.getHostsAround();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	mCallback.onHostSelected(id);
    }

    public void getHostsAround() {
	MainActivity activity = (MainActivity) this.getActivity();
	activity.setTracker(new LocalTracker(activity));
	LocalTracker tracker = activity.getTracker();

	if (tracker.canGetLocation()) {
	    double latitude = tracker.getLatitude();
	    double longitude = tracker.getLongitude();

	    Toast.makeText(getActivity(), latitude + " | " + longitude, Toast.LENGTH_LONG).show();
	    Log.d(TAG, latitude + " | " + longitude);
	    HostsAroundRequest request = new HostsAroundRequest(host_distance, 0d, 0d /*, true*/);
	    activity.getContentManager().execute(request, new HostsAroundRequestListener());
	} else {
	    Log.d(TAG, "location n/a");
	    Toast.makeText(getActivity(), "Please enable Network/GPS!", Toast.LENGTH_LONG).show();
	}
    }

    public interface OnHostSelectedListener {
	public void onHostSelected(long hostId);
    }

    private class HostsAroundRequestListener implements RequestListener<ListUserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    Context context = getActivity();
	    if (context == null) {
		return;
	    }

	    HosterModel[] closeByhosts = new HosterModel[0];
	    ListAdapter listAdapter = new HostsModelListAdapter(context, closeByhosts);
	    setListAdapter(listAdapter);
	}

	@Override
	public void onRequestSuccess(ListUserResource listUsers) {
	    if (listUsers == null) {
		return;
	    }

	    Context context = getActivity();
	    if (context == null) {
		return;
	    }

	    ArrayList<HosterModel> hosts = new ArrayList<HosterModel>();
	    for (UserResource user : listUsers) {
		HosterModel instance = new HosterModel(user.getId(), user.getName(), user.getService().getFee(), "", R.drawable.thumb_1, 300, 1001, 3);
		hosts.add(instance);
	    }

	    HosterModel[] closeByhosts = hosts.toArray(new HosterModel[hosts.size()]);
	    ListAdapter listAdapter = new HostsModelListAdapter(context, closeByhosts);
	    setListAdapter(listAdapter);
	}
    }
}
