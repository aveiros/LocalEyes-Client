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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SlidingMenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	setHasOptionsMenu(true);
	View view = inflater.inflate(R.layout.sliding_menu, container, false);

	Button BtLogOut = (Button) view.findViewById(R.id.bt_logout);
	BtLogOut.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onLogOut();
	    }
	});

	return view;
    }

    private void onLogOut() {
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