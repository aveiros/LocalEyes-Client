package com.lisbonbigapps.myhoster.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.model.ContactMessageModel;

public class MessagesListAdapter extends BaseAdapter {

    private class ViewHolder {
	public TextView textContact;
    }

    private ContactMessageModel[] contacts;
    private LayoutInflater inflater;

    public MessagesListAdapter(Context context, ContactMessageModel[] contacts) {
	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	this.contacts = contacts;
    }

    @Override
    public int getCount() {
	if (contacts != null) {
	    return contacts.length;
	}

	return 0;
    }

    @Override
    public Object getItem(int position) {
	if (contacts != null && position >= 0 && position < getCount()) {
	    return contacts[position];
	}

	return null;
    }

    @Override
    public long getItemId(int position) {
	if (contacts != null && position >= 0 && position < getCount()) {
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
	    view = inflater.inflate(R.layout.item_messages_list, parent, false);

	    viewHolder = new ViewHolder();
	    viewHolder.textContact = (TextView) view.findViewById(R.id.textViewContact);

	    view.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) view.getTag();
	}

	ContactMessageModel model = contacts[position];
	viewHolder.textContact.setText(model.getName());

	return view;
    }
}