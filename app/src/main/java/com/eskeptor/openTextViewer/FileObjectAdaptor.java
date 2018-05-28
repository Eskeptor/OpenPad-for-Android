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

/*
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * View Holder for FileObject
 */
class FileObjectViewHolder
{
    public ImageView fileImage;
    public TextView fileName;
    public TextView fileSize;
}

public class FileObjectAdaptor extends BaseAdapter
{
    private Context mContext;                       // Context to use for LayoutInflater
    private ArrayList<FileObject> mFileObjects;     // ArrayList holding a FileObject
    private Drawable mDrawableFolder;               // Folder Icon
    private Drawable mDrawableNormalFile;           // Normal File Icon
    private Drawable mDrawableOver1File;            // Medium File Icon
    private Drawable mDrawableOver2File;            // Large File Icon

    /**
     * Create a FileObject Adapter
     * @param context Context
     * @param fileObjects FileObject용 ArrrayList
     */
    FileObjectAdaptor(final Context context, final ArrayList<FileObject> fileObjects) {
        this.mContext = context;
        this.mFileObjects = fileObjects;

        if (Build.VERSION.SDK_INT >= 21) {
            mDrawableFolder = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            mDrawableNormalFile = context.getResources().getDrawable(R.drawable.ic_note_normal, null);
            mDrawableOver1File = context.getResources().getDrawable(R.drawable.ic_note_over1, null);
            mDrawableOver2File = context.getResources().getDrawable(R.drawable.ic_note_over2, null);
        } else {
            mDrawableFolder = context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            mDrawableNormalFile = context.getResources().getDrawable(R.drawable.ic_note_normal);
            mDrawableOver1File = context.getResources().getDrawable(R.drawable.ic_note_over1);
            mDrawableOver2File = context.getResources().getDrawable(R.drawable.ic_note_over2);
        }
    }

    @Override
    public int getCount() {
        return mFileObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileObjectViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fileobject_layout, null);
            holder = new FileObjectViewHolder();
            holder.fileImage = convertView.findViewById(R.id.item_fileobj_image);
            holder.fileName = convertView.findViewById(R.id.item_fileobj_name);
            holder.fileSize = convertView.findViewById(R.id.item_fileobj_size);
            convertView.setTag(holder);
        } else {
            holder = (FileObjectViewHolder) convertView.getTag();
        }

        FileObject.BrowserIconType type = mFileObjects.get(position).mIconType;
        String size = Long.toString(mFileObjects.get(position).mFileSize) + Constant.BASIC_FILE_UNIT;

        switch (type) {
            case Folder:
                holder.fileImage.setImageDrawable(mDrawableFolder);
                holder.fileSize.setText("");
                break;
            case Over1:
                holder.fileImage.setImageDrawable(mDrawableOver1File);
                holder.fileSize.setText(size);
                break;
            case Over2:
                holder.fileImage.setImageDrawable(mDrawableOver2File);
                holder.fileSize.setText(size);
                break;
            case Normal:
                holder.fileImage.setImageDrawable(mDrawableNormalFile);
                holder.fileSize.setText(size);
                break;
        }

        holder.fileName.setText(mFileObjects.get(position).mFileName);

        return convertView;
    }
}
