package com.lisbonbigapps.myhoster.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.adapter.ServiceModelListAdapter;
import com.lisbonbigapps.myhoster.client.request.ServicesRequest;
import com.lisbonbigapps.myhoster.client.resources.ListServiceResource;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class ServicesFragment extends SherlockListFragment {
    static final String TAG = ServicesFragment.class.toString();
    static final int AB_REFRESH = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.services_list, container, false);
	return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	menu.add(0, AB_REFRESH, 0, "Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	ActionBar actionBar = getSherlockActivity().getSupportActionBar();
	actionBar.setTitle("Services");
	actionBar.setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	this.getServices();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);

	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Bundle args = new Bundle();
	    args.putLong("id", id);

	    Fragment fg = new ServiceFragment();
	    fg.setArguments(args);

	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, fg);
	    transaction.commit();
	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case AB_REFRESH:
	    this.getServices();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    private void getServices() {
	MainActivity activity = (MainActivity) this.getActivity();
	ServicesRequest request = new ServicesRequest();
	activity.getContentManager().execute(request, new ServicesRequestListener());
    }

    private class ServicesRequestListener implements RequestListener<ListServiceResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    Context context = getActivity();
	    if (context == null) {
		return;
	    }

	    ServiceResource[] services = new ServiceResource[0];
	    ListAdapter listAdapter = new ServiceModelListAdapter(context, services);
	    setListAdapter(listAdapter);
	}

	@Override
	public void onRequestSuccess(ListServiceResource list) {
	    if (list == null) {
		return;
	    }

	    Context context = getActivity();
	    if (context == null) {
		return;
	    }

	    ServiceResource[] services = list.toArray(new ServiceResource[list.size()]);
	    ListAdapter listAdapter = new ServiceModelListAdapter(context, services);
	    setListAdapter(listAdapter);
	}
    }
}
