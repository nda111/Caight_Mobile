package com.example.caight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.TreeMap;

public class WeightListAdapter extends RecyclerView.Adapter<WeightListAdapter.ViewHolder>
{
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView weightTextView;
        TextView dateTextView;
        ImageView deleteButton;

        Date date;
        Float weight;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            weightTextView = itemView.findViewById(R.id.weightTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    static abstract class ItemEventListener
    {
        public abstract void onClick(ViewHolder viewHolder);
        public abstract void onDelete(ViewHolder viewHolder);
    }

    private Context context = null;
    private Map.Entry<Date, Float>[] weights = null;
    private ItemEventListener listener = null;

    public WeightListAdapter(Context context, TreeMap<Date, Float> weights, ItemEventListener listener)
    {
        this.context = context;
        this.weights = weights.entrySet().toArray(new Map.Entry[weights.size()]);

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_weight_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onClick(viewHolder);
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onDelete(viewHolder);
            }
        });

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Map.Entry<Date, Float> entry = weights[getItemCount() - position - 1];

        holder.date = entry.getKey();
        holder.weight = entry.getValue();

        holder.weightTextView.setText(holder.weight + " " + context.getResources().getString(R.string.unit_kg_no_parenthesis));
        holder.dateTextView.setText(Methods.DateFormatter.format(holder.date));
    }

    @Override
    public int getItemCount()
    {
        return weights.length;
    }
}
