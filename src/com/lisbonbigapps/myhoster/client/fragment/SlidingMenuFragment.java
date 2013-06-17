package com.lisbonbigapps.myhoster.client.fragment;

import com.lisbonbigapps.myhoster.client.ui.LoginActivity;
import com.lisbonbigapps.myhoster.client.util.PreferencesHelper;
import com.lisbonbigapps.myhoster.client.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SlidingMenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	setHasOptionsMenu(true);
	View view = inflater.inflate(R.layout.sliding_menu_traveller, container, false);

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

	return view;
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

    protected void onServices() {
	FragmentManager manager = getActivity().getSupportFragmentManager();
	Fragment fragment = manager.findFragmentById(R.id.fragment_content);

	if (fragment != null) {
	    Fragment ft = new ServicesFragment();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.replace(R.id.fragment_content, ft);
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
	Activity activity = getActivity();
	Context context = activity.getBaseContext();

	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	SharedPreferences.Editor editor = preferences.edit();

	editor.remove(PreferencesHelper.Username).commit();
	editor.remove(PreferencesHelper.Password).commit();

	activity.finish();
	Intent intent = new Intent(context, LoginActivity.class);
	startActivity(intent);
    }
}