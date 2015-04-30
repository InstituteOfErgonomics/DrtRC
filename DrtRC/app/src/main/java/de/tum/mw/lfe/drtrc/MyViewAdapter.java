package de.tum.mw.lfe.drtrc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyViewAdapter extends ArrayAdapter<DataItem> {
    private final MainActivity mParent;
    private final ArrayList<DataItem> mDataItems;

    public MyViewAdapter(MainActivity context, ArrayList<DataItem> dataItems) {
        super((Context)context, R.layout.item, dataItems);
        this.mParent = context;
        this.mDataItems = dataItems;
    }

    static class ViewHolder {
        protected TextView result;
        protected TextView id;
        protected TextView name;
        //protected TextView desc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = mParent.getLayoutInflater();
            view = inflator.inflate(R.layout.item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.result = (TextView) view.findViewById(R.id.itemResultTextView);
            viewHolder.id = (TextView) view.findViewById(R.id.itemIdTextView);
            viewHolder.name = (TextView) view.findViewById(R.id.itemNameTextView);
            //viewHolder.desc = (TextView) view.findViewById(R.id.itemDescTextView);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.result.setText(mDataItems.get(calculateAdjustedPosition(position)).mResult);
        holder.id.setText(mDataItems.get(calculateAdjustedPosition(position)).mId);
        holder.name.setText(mDataItems.get(calculateAdjustedPosition(position)).mName);
        //holder.desc.setText(mDataItems.get(calculateAdjustedPosition(position)).mDesc);


        return view;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (DataItem d: mDataItems){
            if (d.mViewEnabled) count++;
        }

        return count;
    }

    public int calculateAdjustedPosition(int position){
        int adjPosition = -1;
        int count = -1;
        for (DataItem d: mDataItems){
            if (d.mViewEnabled) adjPosition++;
            count++;
            if (adjPosition == position) break;
        }
        return count;
    }

}
