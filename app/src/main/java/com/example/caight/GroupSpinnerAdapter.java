package com.example.caight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupSpinnerAdapter extends ArrayAdapter<List<CatGroup>>
{
    Activity context;
    ArrayList<CatGroup> list;
    LayoutInflater inflater;

    public GroupSpinnerAdapter(Activity context, int id, ArrayList<CatGroup> list)
    {
        super(context, id);

        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CatGroup item = list.get(position);
        View view = inflater.inflate(R.layout.item_group_spinner, parent, false);

        ((TextView)view.findViewById(R.id.groupTextView)).setText(item.getName());
        ((TextView)view.findViewById(R.id.idTextView)).setText('(' + Methods.toHexId(item.getId()) + ')');

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getView(position, convertView, parent);
    }
}
