package com.example.lqrecycleview.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lqrecycleview.R;

import java.util.List;

public class MyAdapter {
    private List<String> items;

    private Context context;

    public View onCreateViewHolder(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(R.layout.textview_layout, null);
        TextView text = (TextView) convertView.findViewById(R.id.textview);
        text.setText(items.get(position));
        return convertView;
    }

    public View onBindViewHolder(int position, View convertView, ViewGroup viewGroup) {
        TextView text = (TextView) convertView.findViewById(R.id.textview);
        text.setText(items.get(position));
        return convertView;
    }

    public int getItemViewType(int row) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public int getItemCount() {
        return items.size();
    }

    public int getHeight(int index) {
        return 150;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public MyAdapter(Context context) {
        this.context = context;
    }

}
