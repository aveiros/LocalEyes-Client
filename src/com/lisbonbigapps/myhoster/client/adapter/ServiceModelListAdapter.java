package com.lisbonbigapps.myhoster.client.adapter;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.resources.ServiceResource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceModelListAdapter extends BaseAdapter {

    private class ViewHolder {
	public TextView textTimestamp;
	public TextView textStatus;
	public TextView textLocal;
	public TextView textFeeback;
    }

    private ServiceResource[] list;
    private LayoutInflater inflater;

    public ServiceModelListAdapter(Context context, ServiceResource[] list) {
	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	this.list = list;
    }

    @Override
    public int getCount() {
	if (list != null) {
	    return list.length;
	}

	return 0;
    }

    @Override
    public Object getItem(int position) {
	if (list != null && position >= 0 && position < getCount()) {
	    return list[position];
	}

	return null;
    }

    @Override
    public long getItemId(int position) {
	if (list != null && position >= 0 && position < getCount()) {
	    return list[position].getId();
	}

	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

	View view = convertView;
	ViewHolder viewHolder;

	if (view == null) {
	    view = inflater.inflate(R.layout.item_service_list, parent, false);

	    viewHolder = new ViewHolder();
	    viewHolder.textStatus = (TextView) view.findViewById(R.id.textViewServiceStatus);
	    viewHolder.textTimestamp = (TextView) view.findViewById(R.id.textViewServiceDate);
	    viewHolder.textLocal = (TextView) view.findViewById(R.id.textViewServiceLocal);
	    viewHolder.textFeeback = (TextView) view.findViewById(R.id.textViewServiceFeedback);

	    view.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) view.getTag();
	}

	ServiceResource model = list[position];

	viewHolder.textStatus.setText(model.getStatus());
	viewHolder.textTimestamp.setText("");
	viewHolder.textLocal.setText(model.getHoster().getName());
	viewHolder.textFeeback.setText("");

	return view;
    }
}
