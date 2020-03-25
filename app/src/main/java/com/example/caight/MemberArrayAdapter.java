package com.example.caight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MemberArrayAdapter extends ArrayAdapter<List<MemberArrayAdapter.Item>>
{
    public static class Item
    {
        public String name;
        public String email;

        public Item(String name, String email)
        {
            this.name = name;
            this.email = email;
        }
    }

    private LayoutInflater inflater = null;
    private List<MemberArrayAdapter.Item> list = null;

    public MemberArrayAdapter(@NonNull Context context, int resource, List<MemberArrayAdapter.Item> list)
    {
        super(context, resource);
        this.list = list;

        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        Item item = list.get(position);
        View view = inflater.inflate(R.layout.item_member_spinner, parent, false);

        ((TextView)view.findViewById(R.id.nameTextView)).setText(item.name);
        ((TextView)view.findViewById(R.id.emailTextView)).setText(item.email);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getView(position, convertView, parent);
    }
}
