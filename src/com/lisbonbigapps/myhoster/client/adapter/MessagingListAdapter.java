package com.lisbonbigapps.myhoster.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lisbonbigapps.myhoster.client.R;

public class MessagingListAdapter extends BaseAdapter {

    private class ViewHolder {
	public TextView textMessage;
    }

    private String[] messages;
    private LayoutInflater inflater;

    public MessagingListAdapter(Context context, String[] messages) {
	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	this.messages = messages;
    }

    @Override
    public int getCount() {
	if (messages != null) {
	    return messages.length;
	}

	return 0;
    }

    @Override
    public Object getItem(int position) {
	if (messages != null && position >= 0 && position < getCount()) {
	    return messages[position];
	}

	return null;
    }

    @Override
    public long getItemId(int position) {
	if (messages != null && position >= 0 && position < getCount()) {
	    // doesnt have an id
	    // return list[position].getId();
	    return position;
	}

	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

	View view = convertView;
	ViewHolder viewHolder;

	if (view == null) {
	    view = inflater.inflate(R.layout.item_messaging_list, parent, false);

	    viewHolder = new ViewHolder();
	    viewHolder.textMessage = (TextView) view.findViewById(R.id.textViewMessage);

	    view.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) view.getTag();
	}

	String model = messages[position];
	viewHolder.textMessage.setText(model);

	return view;
    }
}