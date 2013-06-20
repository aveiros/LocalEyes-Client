package com.lisbonbigapps.myhoster.client.adapter;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public abstract class LocalServiceListAdapter extends BaseAdapter {

    private class ViewHolder {
	public TextView textDistance;
	public TextView textName;
	public TextView textAddress;
	public TextView textStatus;
	public Button buttonAccept;
	public Button buttonReject;
    }

    private ServiceResource[] services;
    private LayoutInflater inflater;

    public LocalServiceListAdapter(Context context, ServiceResource[] list) {
	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	this.services = list;
    }

    @Override
    public int getCount() {
	if (services != null) {
	    return services.length;
	}

	return 0;
    }

    @Override
    public Object getItem(int position) {
	if (services != null && position >= 0 && position < getCount()) {
	    return services[position];
	}

	return null;
    }

    @Override
    public long getItemId(int position) {
	if (services != null && position >= 0 && position < getCount()) {
	    return services[position].getId();
	}

	return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	View view = convertView;
	ViewHolder viewHolder;

	if (view == null) {
	    view = inflater.inflate(R.layout.item_local_service_list, parent, false);

	    viewHolder = new ViewHolder();
	    viewHolder.textName = (TextView) view.findViewById(R.id.grid_label_name);
	    viewHolder.textDistance = (TextView) view.findViewById(R.id.grid_label_distance);
	    viewHolder.textAddress = (TextView) view.findViewById(R.id.grid_label_address);
	    viewHolder.textStatus = (TextView) view.findViewById(R.id.grid_label_status);
	    viewHolder.buttonAccept = (Button) view.findViewById(R.id.button_accept);
	    viewHolder.buttonReject = (Button) view.findViewById(R.id.button_reject);

	    viewHolder.buttonAccept.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    onAccept(position);
		}
	    });

	    viewHolder.buttonReject.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    onReject(position);
		}
	    });

	    view.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) view.getTag();
	}

	ServiceResource service = services[position];

	viewHolder.textName.setText(service.getTraveller().getName());
	viewHolder.textDistance.setText("300m");
	viewHolder.textAddress.setText("");
	viewHolder.textStatus.setText(service.getStatus());

	if (service.getStatus().equals("PENDING")) {
	    viewHolder.textStatus.setVisibility(View.INVISIBLE);
	    viewHolder.buttonAccept.setVisibility(View.VISIBLE);
	    viewHolder.buttonReject.setVisibility(View.VISIBLE);
	} else {
	    viewHolder.textStatus.setVisibility(View.VISIBLE);
	    viewHolder.buttonAccept.setVisibility(View.INVISIBLE);
	    viewHolder.buttonReject.setVisibility(View.INVISIBLE);
	}

	return view;
    }

    public abstract void onAccept(int position);

    public abstract void onReject(int position);
}
