package com.adragon.newcrosstable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;


public class CustomAdapter extends BaseAdapter {
    String[] arr;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(Context context, String[] arr) {
        this.arr = arr;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arr.length-1;
    }

    @Override
    public Object getItem(int i) {
        return arr[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_item_style,null);
        TextView tv = view.findViewById(R.id.textView);
        tv.setText(arr[i]);
        return view;
    }
}
