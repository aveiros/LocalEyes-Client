package com.lisbonbigapps.myhoster.client.adapter;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.model.MessageModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageModelListAdapter extends BaseAdapter {

    private class ViewHolder {
	public TextView textSender;
	public TextView textMessage;
    }

    private MessageModel[] list;
    private LayoutInflater inflater;

    public MessageModelListAdapter(Context context, MessageModel[] list) {
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
	    view = inflater.inflate(R.layout.item_message_list, parent, false);

	    viewHolder = new ViewHolder();
	    viewHolder.textSender = (TextView) view.findViewById(R.id.textViewMessageSender);
	    viewHolder.textMessage = (TextView) view.findViewById(R.id.textViewMessageText);

	    view.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) view.getTag();
	}

	MessageModel model = list[position];

	viewHolder.textSender.setText(model.sender);
	viewHolder.textMessage.setText(model.text);

	return view;
    }
}
