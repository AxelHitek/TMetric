package com.tm.ah.tmetric.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tm.ah.tmetric.R;

import java.util.ArrayList;

/**
 * Created by AH on 2/5/2018.
 */

public class CalendarLstViewAdapter extends ArrayAdapter<String> {
    int groupid;
    ArrayList<String> elements;
    Context context;
    public CalendarLstViewAdapter(Context context, int vg, int id, ArrayList elem){
        super(context,vg, id, elem);
        this.context=context;
        groupid=vg;
        this.elements =elem;

    }
    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textview;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the list_item.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            CalendarLstViewAdapter.ViewHolder viewHolder = new CalendarLstViewAdapter.ViewHolder();
            viewHolder.textview= (TextView) rowView.findViewById(R.id.projectNameCalendar);
            rowView.setTag(viewHolder);
        }
        // Set text to each TextView of ListView item
        CalendarLstViewAdapter.ViewHolder holder = (CalendarLstViewAdapter.ViewHolder) rowView.getTag();
        holder.textview.setText(elements.get(position));
        return rowView;
    }

}
