package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.adapter.HostersModelListAdapter;
import com.lisbonbigapps.myhoster.client.adapter.ServiceModelListAdapter;
import com.lisbonbigapps.myhoster.client.model.HosterModel;
import com.lisbonbigapps.myhoster.client.request.ServicesRequest;
import com.lisbonbigapps.myhoster.client.resources.ListServiceResource;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.TravellerActivity;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class ServicesFragment extends SherlockListFragment {
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
	// inflater.inflate(R.menu.profile_menu, menu);
	// Add Search Button
	super.onCreateOptionsMenu(menu, inflater);
	menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	getSherlockActivity().getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_ab));

	// White ABS title
	// String abTitle = "<font color='#ffffff'>Locals around you..</font>";
	// getSherlockActivity().getSupportActionBar().setTitle(Html.fromHtml(abTitle));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	TravellerActivity activity = (TravellerActivity) this.getActivity();
	ServicesRequest request = new ServicesRequest();
	activity.getContentManager().execute(request, new ServicesRequestListener());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);

	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment serviceFragment = new ServiceFragment();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, serviceFragment);
	    transaction.commit();
	}

	// mCallback.onHostSelected(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return true;
    }

    // public interface OnHostSelectedListener {
    // public void onHostSelected(long hostId);
    // }

    private class ServicesRequestListener implements RequestListener<ListServiceResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    ServiceResource[] services = new ServiceResource[0];
	    ListAdapter listAdapter = new ServiceModelListAdapter(getActivity(), services);
	    setListAdapter(listAdapter);
	}

	@Override
	public void onRequestSuccess(ListServiceResource list) {
	    if (list == null) {
		return;
	    }

	    ServiceResource[] services = list.toArray(new ServiceResource[list.size()]);
	    ListAdapter listAdapter = new ServiceModelListAdapter(getActivity(), services);
	    setListAdapter(listAdapter);
	}
    }
}
