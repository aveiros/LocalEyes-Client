package com.lisbonbigapps.myhoster.client.fragment;

import com.lisbonbigapps.myhoster.client.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainViewFragment extends Fragment {
	


	// -----
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.selection_screen, container, false);
        
//        Button buttonGrid = (Button) view.findViewById(R.id.gridView);
//        Button buttonList = (Button) view.findViewById(R.id.listView);
//                
//        buttonList.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//              Activity activity = getActivity();
//              
//              if (activity != null) {
//                  gotoListView();
//              }            	
//               
//            }            
//        });
////        
//        buttonGrid.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//            	 gotoGridView();
//            }            
//        });
//        
        return view;
    }


}