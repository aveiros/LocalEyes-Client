package com.lisbonbigapps.myhoster.client.adapter;

import com.lisbonbigapps.myhoster.client.R;
import com.lisbonbigapps.myhoster.client.model.HosterModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class HostersModelListAdapter extends BaseAdapter {

    private class ViewHolder {
        public TextView textAddress;
        public TextView textName;
        public TextView textDistance;
        public ImageView photoThumbnail;
    }
    
    private HosterModel[] mHosters;
    private LayoutInflater  mInflater;
    
    public HostersModelListAdapter(Context context, HosterModel[] locations) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHosters = locations;
    }
    
    @Override
    public int getCount() {
        if (mHosters != null) {
            return mHosters.length;
        }
        
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mHosters != null && position >= 0 && position < getCount()) {
            return mHosters[position];
        }
        
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mHosters != null && position >= 0 && position < getCount()) {
            return mHosters[position].id;
        }
        
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View       view = convertView;
        ViewHolder viewHolder;
        
        if (view == null) {
            view = mInflater.inflate(R.layout.item_hosters_list, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.textName = (TextView) view.findViewById(R.id.grid_label_name);
            viewHolder.textAddress = (TextView) view.findViewById(R.id.grid_label_address);
            viewHolder.textDistance = (TextView) view.findViewById(R.id.grid_label_distance);
            viewHolder.photoThumbnail = (ImageView) view.findViewById(R.id.grid_image);
            
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        
        HosterModel hosterModel = mHosters[position];
        
        viewHolder.photoThumbnail.setImageResource(hosterModel.picture);
        viewHolder.textAddress.setText(hosterModel.address);
        viewHolder.textDistance.setText(String.valueOf(hosterModel.distance)+"m");
        viewHolder.textName.setText(hosterModel.name);
        
        return view;
    }

}