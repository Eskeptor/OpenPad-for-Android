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
import java.util.Locale;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class FolderViewHolder
{
    public ImageView mFolderIcon;
    public TextView mFolderName;
    public TextView mFileCountInFolder;
}

public class FolderAdaptor extends BaseAdapter {
    private Context mContext;
    private ArrayList<FolderObject> mFolders;
    private Drawable mDrawableFolderNormal;
    private Drawable mDrawableFolderRoot;
    private Drawable mDrawableFolderExternal;

    public FolderAdaptor(final Context _context, final ArrayList<FolderObject> _folders) {
        this.mContext = _context;
        this.mFolders = _folders;

        if (Build.VERSION.SDK_INT >= 21) {
            mDrawableFolderNormal = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp, null);
            mDrawableFolderRoot = _context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp, null);
            mDrawableFolderExternal = _context.getResources().getDrawable(R.drawable.ic_folder_open_black, null);
        } else {
            mDrawableFolderNormal = _context.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
            mDrawableFolderRoot = _context.getResources().getDrawable(R.drawable.ic_folder_shared_black_24dp);
            mDrawableFolderExternal = _context.getResources().getDrawable(R.drawable.ic_folder_open_black);
        }
    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public Object getItem(int _position) {
        return mFolders.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        return 0;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        FolderViewHolder holder;

        if (_convertView == null) {
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.item_folder_layout, null);
            holder = new FolderViewHolder();
            holder.mFolderIcon = (ImageView) _convertView.findViewById(R.id.item_folder_image);
            holder.mFolderName = (TextView) _convertView.findViewById(R.id.item_folder_name);
            holder.mFileCountInFolder = (TextView) _convertView.findViewById(R.id.item_folder_count);

            _convertView.setTag(holder);
        } else {
            holder = (FolderViewHolder) _convertView.getTag();
        }

        int type = mFolders.get(_position).mFolderType;
        switch (type) {
            case Constant.FOLDER_TYPE_DEFAULT:
                holder.mFolderIcon.setImageDrawable(mDrawableFolderRoot);
                break;
            case Constant.FOLDER_TYPE_EXTERNAL:
                holder.mFolderIcon.setImageDrawable(mDrawableFolderExternal);
                break;
            case Constant.FOLDER_TYPE_CUSTOM:
                holder.mFolderIcon.setImageDrawable(mDrawableFolderNormal);
                break;
        }

        holder.mFolderName.setText(mFolders.get(_position).mFolderName);

        if (mFolders.get(_position).mFileCountInFolder == Constant.FOLDER_TYPE_EXTERNAL) {
            holder.mFileCountInFolder.setText(null);
        } else {
            holder.mFileCountInFolder.setText(String.format(Locale.getDefault(), "%d", mFolders.get(_position).mFileCountInFolder));
        }

        return _convertView;
    }
}
