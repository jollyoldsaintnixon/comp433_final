package com.example.finalproject;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

    MainActivity mainActivity;

    public MyListAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return this.mainActivity.salty.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            if (this.mainActivity == null) {
                Log.v("mytag", "is mainactivity null?");

            }
            convertView = this.mainActivity.getLayoutInflater().inflate(R.layout.my_row, null, false);
        }

        TextView textView = convertView.findViewById(R.id.my_row_text);
        textView.setText(this.mainActivity.salty[position]);

        return convertView;
    }
}
