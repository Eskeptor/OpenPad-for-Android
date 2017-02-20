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
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class FileObjectViewHolder
{
    public ImageView image;
    public TextView name;
    public TextView size;
}

public class FileObjectAdaptor extends BaseAdapter {
    private Context context;

    private ArrayList<FileObject> fileObjects;
    private Drawable drawableFolder;
    private Drawable drawableNormalFile;
    private Drawable drawableOver1File;
    private Drawable drawableOver2File;

    public FileObjectAdaptor(final Context context, final ArrayList<FileObject> fileObjects)
    {
        this.context = context;
        this.fileObjects = fileObjects;

        if(Build.VERSION.SDK_INT >= 21)
        {
            drawableFolder = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            drawableNormalFile = context.getResources().getDrawable(R.drawable.ic_note_normal, null);
            drawableOver1File = context.getResources().getDrawable(R.drawable.ic_note_over1, null);
            drawableOver2File = context.getResources().getDrawable(R.drawable.ic_note_over2, null);
        }
        else
        {
            drawableFolder = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            drawableNormalFile = context.getResources().getDrawable(R.drawable.ic_note_normal);
            drawableOver1File = context.getResources().getDrawable(R.drawable.ic_note_over1);
            drawableOver2File = context.getResources().getDrawable(R.drawable.ic_note_over2);
        }
    }

    @Override
    public int getCount() {
        return fileObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return fileObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileObjectViewHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fileobject_layout, null);
            holder = new FileObjectViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.item_fileobj_image);
            holder.name = (TextView)convertView.findViewById(R.id.item_fileobj_name);
            holder.size = (TextView)convertView.findViewById(R.id.item_fileobj_size);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FileObjectViewHolder)convertView.getTag();
        }

        if(fileObjects.get(position).type == Constant.BROWSER_IMAGE_TYPE_FOLDER)
        {
            holder.image.setImageDrawable(drawableFolder);
            holder.size.setText("");
        }
        else if(fileObjects.get(position).type == Constant.BROWSER_IMAGE_TYPE_OVER1)
        {
            holder.image.setImageDrawable(drawableOver1File);
            holder.size.setText(fileObjects.get(position).size + "KB");
        }
        else if(fileObjects.get(position).type == Constant.BROWSER_IMAGE_TYPE_OVER2)
        {
            holder.image.setImageDrawable(drawableOver2File);
            holder.size.setText(fileObjects.get(position).size + "KB");
        }
        else
        {
            holder.image.setImageDrawable(drawableNormalFile);
            holder.size.setText(fileObjects.get(position).size + "KB");
        }

        holder.name.setText(fileObjects.get(position).name);

        return convertView;
    }
}
