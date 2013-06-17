package com.lisbonbigapps.myhoster.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lisbonbigapps.myhoster.client.R;

public class MessagesFragment extends SherlockListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View v = inflater.inflate(R.layout.messages_list, container, false);
	return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// inflater.inflate(R.menu.profile_menu, menu);
	// Add Search Button
	super.onCreateOptionsMenu(menu, inflater);
	menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	getSherlockActivity().getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_ab));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

//	TravellerActivity activity = (TravellerActivity) this.getActivity();
//	ServicesRequest request = new ServicesRequest();
//	activity.getContentManager().execute(request, new ServicesRequestListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return true;
    }
}
