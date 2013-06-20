package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
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
import com.lisbonbigapps.myhoster.client.adapter.LocalServiceListAdapter;
import com.lisbonbigapps.myhoster.client.request.ServiceReplyRequest;
import com.lisbonbigapps.myhoster.client.request.ServicesHostRequest;
import com.lisbonbigapps.myhoster.client.resources.ListServiceResource;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class LocalServiceListFragment extends SherlockListFragment {
    static final String TAG = LocalServiceListFragment.class.toString();
    static final int AB_REFRESH = 1;

    protected List<ServiceResource> services;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.local_services, container, false);
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

	// FragmentManager manager = getActivity().getSupportFragmentManager();
	// Fragment fragment = manager.findFragmentById(R.id.fragment_content);
	//
	// if (fragment != null) {
	// Bundle args = new Bundle();
	// args.putLong("id", id);
	//
	// Fragment fg = new ServiceFragment();
	// fg.setArguments(args);
	//
	// FragmentTransaction transaction = manager.beginTransaction();
	// transaction.replace(R.id.fragment_content, fg);
	// transaction.commit();
	// }
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
	ServicesHostRequest request = new ServicesHostRequest();
	activity.getContentManager().execute(request, new ServicesRequestListener());
    }

    private class ServicesRequestListener implements RequestListener<ListServiceResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    Context context = getActivity();
	    if (context == null) {
		return;
	    }

	    services = new ArrayList<ServiceResource>(0);
	    ServiceResource[] servicesArray = services.toArray(new ServiceResource[services.size()]);
	    ListAdapter listAdapter = new LocalServiceListAdapter1(context, servicesArray);
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

	    services = sort(list);
	    ServiceResource[] servicesArray = services.toArray(new ServiceResource[services.size()]);
	    ListAdapter listAdapter = new LocalServiceListAdapter1(context, servicesArray);
	    setListAdapter(listAdapter);
	}

	private ListServiceResource sort(ListServiceResource list) {
	    Collections.sort(list, new Comparator<ServiceResource>() {
		public int compare(ServiceResource s1, ServiceResource s2) {
		    if (s1.getStatus().equals("PENDING") && !s2.getStatus().equals("PENDING")) {
			return -1;
		    } else if (!s1.getStatus().equals("PENDING") && s2.getStatus().equals("PENDING")) {
			return 1;
		    } else {
			return 0;
		    }
		}
	    });

	    return list;
	}
    }

    private class LocalServiceListAdapter1 extends LocalServiceListAdapter {
	public LocalServiceListAdapter1(Context context, ServiceResource[] list) {
	    super(context, list);
	}

	@Override
	public void onAccept(int position) {
	    ServiceResource service = getService(position);
	    if (service == null) {
		return;
	    }

	    MainActivity activity = (MainActivity) getActivity();
	    ServiceReplyRequest request = new ServiceReplyRequest(service.getId(), "accept");
	    activity.getContentManager().execute(request, new ServiceReplyRequestListener());
	}

	@Override
	public void onReject(int position) {
	    ServiceResource service = getService(position);
	    if (service == null) {
		return;
	    }

	    MainActivity activity = (MainActivity) getActivity();
	    ServiceReplyRequest request = new ServiceReplyRequest(service.getId(), "reject");
	    activity.getContentManager().execute(request, new ServiceReplyRequestListener());
	}

	private ServiceResource getService(int position) {
	    ServiceResource service = null;
	    if (position < services.size()) {
		service = services.get(position);
	    }

	    return service;
	}
    }

    private class ServiceReplyRequestListener implements RequestListener<ServiceResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    getServices();
	}

	@Override
	public void onRequestSuccess(ServiceResource service) {
	    getServices();
	}
    }
}
