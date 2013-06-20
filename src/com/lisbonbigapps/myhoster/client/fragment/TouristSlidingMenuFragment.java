package com.lisbonbigapps.myhoster.client.fragment;

import java.util.List;
import com.lisbonbigapps.myhoster.client.app.App;
import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.ui.LoginActivity;
import com.lisbonbigapps.myhoster.client.ui.MainActivity;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TouristSlidingMenuFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	setHasOptionsMenu(true);
	view = inflater.inflate(R.layout.sliding_menu_traveller, container, false);

	View v = view.findViewById(R.id.layoutMenuSearch);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onSearch();
	    }
	});

	v = view.findViewById(R.id.layoutMenuHostsAround);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onHostsAround();
	    }
	});

	v = view.findViewById(R.id.layoutMenuServices);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onServices();
	    }
	});

	v = view.findViewById(R.id.layoutMenuMap);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onMessages();
	    }
	});

	v = view.findViewById(R.id.bt_logout);
	v.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onLogOut();
	    }
	});

	fillView(view);
	return view;
    }

    private void fillView(View view) {
	getLocation();

	if (view == null) {
	    return;
	}

	App app = (App) getActivity().getApplication();
	if (app == null) {
	    return;
	}

	UserResource user = app.getUser();
	if (user == null) {
	    return;
	}

	TextView textName = (TextView) view.findViewById(R.id.profileLocalName);
	TextView textFeedback = (TextView) view.findViewById(R.id.textView4);
	TextView textStatus = (TextView) view.findViewById(R.id.profileLocalTextViewStatus);

	textName.setText(user.getName());
	textStatus.setText("Online");
	textFeedback.setText("(" + user.getService().getVotes() + ")");
    }

    protected void onHostsAround() {
	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment ft = new HostListFragment();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, ft);
	    transaction.commit();
	}
    }

    protected void onSearch() {
	// TODO Auto-generated method stub

    }

    protected void getLocation() {
	Geocoder gc = new Geocoder(this.getActivity());

	List<Address> list = null;
	try {
	    list = gc.getFromLocation(38.725174, -9.150099, 10);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	if (list == null) {
	    return;
	}

	String location = "";
	if (list.size() == 1) {
	    Address address = list.get(0);
	    String l1 = address.getLocality() == null ? "" : address.getLocality();
	    String l2 = address.getThoroughfare() == null ? "" : address.getThoroughfare();
	    location = String.format("%s - %s", l1, l2);
	}

	TextView textLocation = (TextView) view.findViewById(R.id.profileLocalTextViewLocation);
	textLocation.setText(location);
    }

    protected void onServices() {
	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment fg = new ServicesFragment();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, fg);
	    transaction.commit();
	}
    }

    protected void onMessages() {
	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment ft = new MessagesFragment();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, ft);
	    transaction.commit();
	}
    }

    protected void onLogOut() {
	MainActivity activity = (MainActivity) getActivity();
	Context context = activity.getBaseContext();

	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	SharedPreferences.Editor editor = preferences.edit();

	editor.remove(PreferencesHelper.Mode).commit();
	editor.remove(PreferencesHelper.Username).commit();
	editor.remove(PreferencesHelper.Password).commit();

	activity.getService().disconnect();
	activity.finish();
	Intent intent = new Intent(context, LoginActivity.class);
	startActivity(intent);
    }
}