package com.abelsky.scrumtimer.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/**
 * Shows two lines of text in a wheel.
 *
 * @author andy
 */
public abstract class CommentedWheelTextAdapter extends AbstractWheelTextAdapter {
    private final int itemTextResourceId;
    private final int itemDescResourceId;

    /**
     * @param itemResource       resource for the whole item
     * @param itemTextResourceId TextView of label
     * @param itemDescResourceId TextView of description
     */
    public CommentedWheelTextAdapter(Context context,
                                     int itemResource,
                                     int itemTextResourceId, int itemDescResourceId) {
        super(context, itemResource);

        this.itemTextResourceId = itemTextResourceId;
        this.itemDescResourceId = itemDescResourceId;
    }

    protected abstract CharSequence getTitle(int index);

    protected abstract CharSequence getDesc(int index);


    @Override
    public CharSequence getItemText(int index) {
        assert false;   // replaced with getTitle() and getDesc()
        return null;
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        assert (index >= 0);

        if (convertView == null) {
            convertView = inflater.inflate(itemResourceId, parent, false);
        }

        final TextView titleView = (TextView) convertView.findViewById(itemTextResourceId);
        titleView.setText(getTitle(index));

        final TextView descView = (TextView) convertView.findViewById(itemDescResourceId);
        descView.setText(getDesc(index));

        return convertView;
    }
}
