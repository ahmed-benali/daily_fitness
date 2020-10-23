package com.benaliahmed.dailyfitness.run;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.benaliahmed.dailyfitness.R;

import java.util.ArrayList;

public class listViewAdapter extends BaseAdapter {

    private ArrayList<listViewItem> listViewItemList = new ArrayList<listViewItem>();

    public listViewAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listViewItemList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, viewGroup, false);
        }
        TextView tv_date= view.findViewById(R.id.date_tv);
        TextView tv_distance= view.findViewById(R.id.distance_tv);
        TextView tv_duration= view.findViewById(R.id.duration_tv);
        TextView tv_speed= view.findViewById(R.id.speed_tv);
        TextView tv_calories= view.findViewById(R.id.calories_tv);

        listViewItem item = listViewItemList.get(i);
        tv_date.setText(item.getDate());
        tv_duration.setText(item.getDuration());
        tv_distance.setText(item.getDistance());
        tv_speed.setText(item.getSpeed());
        tv_calories.setText(item.getCalories());

        return view;
    }

    public void addItem(int id, String date,String distance,String duration,String speed,String calories){
        listViewItem item = new listViewItem();
        item.setId(id);
        item.setDate(date);
        item.setDistance(distance);
        item.setDuration(duration);
        item.setSpeed(speed);
        item.setCalories(calories);
        listViewItemList.add(item);
    }
}
