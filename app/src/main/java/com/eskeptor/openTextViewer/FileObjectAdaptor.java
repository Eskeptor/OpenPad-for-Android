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
import com.eskeptor.openTextViewer.datatype.FileObject;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class FileObjectViewHolder
{
    public ImageView image;
    public TextView name;
    public TextView size;
}

public class FileObjectAdaptor extends BaseAdapter
{
    private Context context;

    private ArrayList<FileObject> fileObjects;
    private Drawable drawableFolder;
    private Drawable drawableNormalFile;
    private Drawable drawableOver1File;
    private Drawable drawableOver2File;

    public FileObjectAdaptor(final Context _context, final ArrayList<FileObject> _fileObjects)
    {
        this.context = _context;
        this.fileObjects = _fileObjects;

        if(Build.VERSION.SDK_INT >= 21)
        {
            drawableFolder = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            drawableNormalFile = _context.getResources().getDrawable(R.drawable.ic_note_normal, null);
            drawableOver1File = _context.getResources().getDrawable(R.drawable.ic_note_over1, null);
            drawableOver2File = _context.getResources().getDrawable(R.drawable.ic_note_over2, null);
        }
        else
        {
            drawableFolder = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            drawableNormalFile = _context.getResources().getDrawable(R.drawable.ic_note_normal);
            drawableOver1File = _context.getResources().getDrawable(R.drawable.ic_note_over1);
            drawableOver2File = _context.getResources().getDrawable(R.drawable.ic_note_over2);
        }
    }

    @Override
    public int getCount()
    {
        return fileObjects.size();
    }

    @Override
    public Object getItem(int _position)
    {
        return fileObjects.get(_position);
    }

    @Override
    public long getItemId(int _position)
    {
        return 0;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent)
    {
        FileObjectViewHolder holder;

        if(_convertView == null)
        {
            _convertView = LayoutInflater.from(context).inflate(R.layout.item_fileobject_layout, null);
            holder = new FileObjectViewHolder();
            holder.image = (ImageView)_convertView.findViewById(R.id.item_fileobj_image);
            holder.name = (TextView)_convertView.findViewById(R.id.item_fileobj_name);
            holder.size = (TextView)_convertView.findViewById(R.id.item_fileobj_size);

            _convertView.setTag(holder);
        }
        else
        {
            holder = (FileObjectViewHolder)_convertView.getTag();
        }

        int type = fileObjects.get(_position).type;
        String size = Long.toString(fileObjects.get(_position).size) + Constant.BASIC_FILE_UNIT;

        if(type == Constant.BROWSER_IMAGE_TYPE_FOLDER)
        {
            holder.image.setImageDrawable(drawableFolder);
            holder.size.setText("");
        }
        else if(type == Constant.BROWSER_IMAGE_TYPE_OVER1)
        {
            holder.image.setImageDrawable(drawableOver1File);
            holder.size.setText(size);
        }
        else if(type == Constant.BROWSER_IMAGE_TYPE_OVER2)
        {
            holder.image.setImageDrawable(drawableOver2File);
            holder.size.setText(size);
        }
        else
        {
            holder.image.setImageDrawable(drawableNormalFile);
            holder.size.setText(size);
        }

        holder.name.setText(fileObjects.get(_position).name);

        return _convertView;
    }
}
