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
import com.eskeptor.openTextViewer.datatype.FolderObject;

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

    private ArrayList<FolderObject> folders;
    private Drawable drawableFolderNormal;
    private Drawable drawableFolderRoot;
    private Drawable drawableFolderExternal;

    public FolderAdaptor(final Context _context, final ArrayList<FolderObject> _folders)
    {
        this.context = _context;
        this.folders = _folders;

        if(Build.VERSION.SDK_INT >= 21)
        {
            drawableFolderNormal = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            drawableFolderRoot = _context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp, null);
            drawableFolderExternal = _context.getResources().getDrawable(R.drawable.ic_folder_open_black, null);
        }
        else
        {
            drawableFolderNormal = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            drawableFolderRoot = _context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp);
            drawableFolderExternal = _context.getResources().getDrawable(R.drawable.ic_folder_open_black);
        }
    }

    @Override
    public int getCount()
    {
        return folders.size();
    }

    @Override
    public Object getItem(int _position)
    {
        return folders.get(_position);
    }

    @Override
    public long getItemId(int _position)
    {
        return 0;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent)
    {
        FolderViewHolder holder;

        if(_convertView == null)
        {
            _convertView = LayoutInflater.from(context).inflate(R.layout.item_folder_layout, null);
            holder = new FolderViewHolder();
            holder.image = (ImageView)_convertView.findViewById(R.id.item_folder_image);
            holder.name = (TextView)_convertView.findViewById(R.id.item_folder_name);
            holder.count = (TextView)_convertView.findViewById(R.id.item_folder_count);

            _convertView.setTag(holder);
        }
        else
        {
            holder = (FolderViewHolder)_convertView.getTag();
        }

        int type = folders.get(_position).type;
        if(type == Constant.FOLDER_TYPE_DEFAULT)
        {
            holder.image.setImageDrawable(drawableFolderRoot);
        }
        else if(type == Constant.FOLDER_TYPE_EXTERNAL)
        {
            holder.image.setImageDrawable(drawableFolderExternal);
        }
        else
        {
            holder.image.setImageDrawable(drawableFolderNormal);
        }

        holder.name.setText(folders.get(_position).name);

        if(folders.get(_position).count == Constant.FOLDER_TYPE_EXTERNAL)
        {
            holder.count.setText(null);
        }
        else
        {
            holder.count.setText(Integer.toString(folders.get(_position).count));
        }

        return _convertView;
    }
}
