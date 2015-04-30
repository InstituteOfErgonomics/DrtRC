package de.tum.mw.lfe.drtrc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.ArrayList;


public class MyConfigAdapter extends ArrayAdapter<DataItem> {
    private final MainActivity mParent;
    private final ArrayList<DataItem> mDataItems;

    public MyConfigAdapter(MainActivity context, ArrayList<DataItem> dataItems) {
        super((Context)context, R.layout.item_wcheckbox, dataItems);
        this.mParent = context;
        this.mDataItems = dataItems;
    }

    static class ViewHolder {
        protected TextView id;
        protected TextView name;
        protected TextView desc;
        protected CheckBox checkboxToast;
        protected CheckBox checkboxMyView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = mParent.getLayoutInflater();
            view = inflator.inflate(R.layout.item_wcheckbox, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView) view.findViewById(R.id.itemIdTextView);
            viewHolder.name = (TextView) view.findViewById(R.id.itemNameTextView);
            viewHolder.desc = (TextView) view.findViewById(R.id.itemDescTextView);
            viewHolder.checkboxToast = (CheckBox) view.findViewById(R.id.itemToastCheckBox);
            viewHolder.checkboxMyView = (CheckBox) view.findViewById(R.id.itemMyViewCheckBox);

            viewHolder.checkboxToast.setTag(mDataItems.get(position));
            viewHolder.checkboxToast
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            DataItem item = (DataItem) viewHolder.checkboxToast
                                    .getTag();
                            item.mToastEnabled = buttonView.isChecked();
                        }
                    });

            viewHolder.checkboxMyView.setTag(mDataItems.get(position));
            viewHolder.checkboxMyView
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            DataItem item = (DataItem) viewHolder.checkboxMyView
                                    .getTag();
                            item.mViewEnabled = buttonView.isChecked();
                        }
                    });

            view.setTag(viewHolder);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkboxToast.setTag(mDataItems.get(position));
            ((ViewHolder) view.getTag()).checkboxMyView.setTag(mDataItems.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.id.setText(mDataItems.get(position).mId);
        holder.name.setText(mDataItems.get(position).mName);
        holder.desc.setText(mDataItems.get(position).mDesc);
        holder.checkboxToast.setChecked(mDataItems.get(position).mToastEnabled);
        holder.checkboxMyView.setChecked(mDataItems.get(position).mViewEnabled);
        return view;
    }
}
