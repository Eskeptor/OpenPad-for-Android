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
    public ImageView mFileImage;
    public TextView mFileName;
    public TextView mFileSize;
}

public class FileObjectAdaptor extends BaseAdapter
{
    private Context mContext;

    private ArrayList<FileObject> mFileObjects;
    private Drawable mDrawableFolder;
    private Drawable mDrawableNormalFile;
    private Drawable mDrawableOver1File;
    private Drawable mDrawableOver2File;

    public FileObjectAdaptor(final Context _context, final ArrayList<FileObject> _fileObjects) {
        this.mContext = _context;
        this.mFileObjects = _fileObjects;

        if (Build.VERSION.SDK_INT >= 21) {
            mDrawableFolder = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            mDrawableNormalFile = _context.getResources().getDrawable(R.drawable.ic_note_normal, null);
            mDrawableOver1File = _context.getResources().getDrawable(R.drawable.ic_note_over1, null);
            mDrawableOver2File = _context.getResources().getDrawable(R.drawable.ic_note_over2, null);
        } else {
            mDrawableFolder = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            mDrawableNormalFile = _context.getResources().getDrawable(R.drawable.ic_note_normal);
            mDrawableOver1File = _context.getResources().getDrawable(R.drawable.ic_note_over1);
            mDrawableOver2File = _context.getResources().getDrawable(R.drawable.ic_note_over2);
        }
    }

    @Override
    public int getCount() {
        return mFileObjects.size();
    }

    @Override
    public Object getItem(int _position) {
        return mFileObjects.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        return 0;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        FileObjectViewHolder holder;

        if (_convertView == null) {
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fileobject_layout, null);
            holder = new FileObjectViewHolder();
            holder.mFileImage = (ImageView) _convertView.findViewById(R.id.item_fileobj_image);
            holder.mFileName = (TextView) _convertView.findViewById(R.id.item_fileobj_name);
            holder.mFileSize = (TextView) _convertView.findViewById(R.id.item_fileobj_size);

            _convertView.setTag(holder);
        } else {
            holder = (FileObjectViewHolder) _convertView.getTag();
        }

        Constant.BrowserIconType type = mFileObjects.get(_position).mIconType;
        String size = Long.toString(mFileObjects.get(_position).mFileSize) + Constant.BASIC_FILE_UNIT;

        switch (type) {
            case Folder:
                holder.mFileImage.setImageDrawable(mDrawableFolder);
                holder.mFileSize.setText("");
                break;
            case Over1:
                holder.mFileImage.setImageDrawable(mDrawableOver1File);
                holder.mFileSize.setText(size);
                break;
            case Over2:
                holder.mFileImage.setImageDrawable(mDrawableOver2File);
                holder.mFileSize.setText(size);
                break;
            case Normal:
                holder.mFileImage.setImageDrawable(mDrawableNormalFile);
                holder.mFileSize.setText(size);
                break;
        }

        holder.mFileName.setText(mFileObjects.get(_position).mFileName);

        return _convertView;
    }
}
