package com.eskeptor.openTextViewer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class FolderViewHolder
{
    public ImageView image;
    public TextView name;
    public TextView count;
}

public class FolderAdaptor extends BaseAdapter
{
    private Context context;

    private ArrayList<Folder> folders;
    private Drawable drawableFolderNormal;
    private Drawable drawableFolderRoot;
    private Drawable drawableFolderExternal;

    public FolderAdaptor(final Context context, final ArrayList<Folder> folders)
    {
        this.context = context;
        this.folders = folders;

        if(Build.VERSION.SDK_INT >= 21)
        {
            drawableFolderNormal = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            drawableFolderRoot = context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp, null);
            drawableFolderExternal = context.getResources().getDrawable(R.drawable.ic_folder_open_black, null);
        }
        else
        {
            drawableFolderNormal = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            drawableFolderRoot = context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp);
            drawableFolderExternal = context.getResources().getDrawable(R.drawable.ic_folder_open_black);
        }
    }

    @Override
    public int getCount()
    {
        return folders.size();
    }

    @Override
    public Object getItem(int position)
    {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        FolderViewHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_folder_layout, null);
            holder = new FolderViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.item_folder_image);
            holder.name = (TextView)convertView.findViewById(R.id.item_folder_name);
            holder.count = (TextView)convertView.findViewById(R.id.item_folder_count);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FolderViewHolder)convertView.getTag();
        }

        if(folders.get(position).type == Constant.FOLDER_TYPE_DEFAULT)
        {
            holder.image.setImageDrawable(drawableFolderRoot);
        }
        else if(folders.get(position).type == Constant.FOLDER_TYPE_EXTERNAL)
        {
            holder.image.setImageDrawable(drawableFolderExternal);
        }
        else
        {
            holder.image.setImageDrawable(drawableFolderNormal);
        }

        holder.name.setText(folders.get(position).name);

        if(folders.get(position).count == Constant.FOLDER_TYPE_EXTERNAL)
        {
            holder.count.setText(null);
        }
        else
        {
            holder.count.setText(Integer.toString(folders.get(position).count));
        }

        return convertView;
    }
}
