package com.eskeptor.openTextViewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class TextFileViewHolder
{
    public TextView title;
    public TextView context;
    public TextView date;
}

public class TextFileAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<TextFile> textFiles;

    public TextFileAdaptor(final Context context, final ArrayList<TextFile> textFiles)
    {
        this.context = context;
        this.textFiles = textFiles;
    }

    @Override
    public int getCount() {
        return textFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return textFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextFileViewHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_textfile_layout, null);
            holder = new TextFileViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.item_textfile_title);
            holder.context = (TextView)convertView.findViewById(R.id.item_textfile_context);
            holder.date = (TextView)convertView.findViewById(R.id.item_textfile_date);

            convertView.setTag(holder);
        }
        else
        {
            holder = (TextFileViewHolder)convertView.getTag();
        }
        holder.title.setText(textFiles.get(position).title);
        holder.context.setText(textFiles.get(position).context_oneline);
        holder.date.setText(textFiles.get(position).date);
        return convertView;
    }
}