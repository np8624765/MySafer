package com.example.mysafer.adapter;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Along on 2015/9/13.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter{

    public List<T> lists;

    public MyBaseAdapter(){}

    public MyBaseAdapter(List<T> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
