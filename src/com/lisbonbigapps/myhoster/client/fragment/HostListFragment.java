package com.lisbonbigapps.myhoster.client.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.adapter.HostersModelListAdapter;
import com.lisbonbigapps.myhoster.client.model.HosterModel;
import com.lisbonbigapps.myhoster.client.request.HostsAroundRequest;
import com.lisbonbigapps.myhoster.client.resources.ListUserResource;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.TravellerActivity;
import com.lisbonbigapps.myhoster.client.R;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class HostListFragment extends SherlockListFragment {
    private OnHostSelectedListener mCallback;
    boolean isDualPane;
    int mCurCheckPosition = 0;
    public static final String ARG_SECTION_NUMBER = "BrowseHostersListFragment";
    public ListView mainList;

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.fragment_hosters_list, container, false);
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
	String abTitle = "<font color='#ffffff'>Locals around you..</font>";
	getSherlockActivity().getSupportActionBar().setTitle(Html.fromHtml(abTitle));
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

	TravellerActivity activity = (TravellerActivity) this.getActivity();
	HostsAroundRequest request = new HostsAroundRequest(500, 0.0, 0.0);
	activity.getContentManager().execute(request, new HostsAroundRequestListener());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	mCallback.onHostSelected(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// Intent intent = new Intent();
	// intent.setClass(getSherlockActivity(), SelectionModeActivity.class);
	// startActivity(intent);
	// return (true);
	// }
	// return false;
	return true;
    }

    public interface OnHostSelectedListener {
	public void onHostSelected(long hostId);
    }

    private class HostsAroundRequestListener implements RequestListener<ListUserResource> {
	@Override
	public void onRequestFailure(SpiceException e) {
	    HosterModel[] closeByhosts = new HosterModel[0];
	    ListAdapter listAdapter = new HostersModelListAdapter(getActivity(), closeByhosts);
	    setListAdapter(listAdapter);
	}

	@Override
	public void onRequestSuccess(ListUserResource listUsers) {
	    if (listUsers == null) {
		return;
	    }

	    ArrayList<HosterModel> hosts = new ArrayList<HosterModel>();
	    for (UserResource user : listUsers) {
		HosterModel instance = new HosterModel(user.getId(), user.getName(), "", R.drawable.thumb_1, 300, 1001, 3);
		hosts.add(instance);
	    }

	    HosterModel[] closeByhosts = hosts.toArray(new HosterModel[hosts.size()]);
	    ListAdapter listAdapter = new HostersModelListAdapter(getActivity(), closeByhosts);
	    setListAdapter(listAdapter);
	}
    }
}
