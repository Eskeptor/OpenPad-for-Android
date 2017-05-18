package com.eskeptor.openTextViewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.eskeptor.openTextViewer.datatype.MainFileObject;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

interface ClickAction
{
    public void onClick(final View _view, final int _position);
    public void onLongClick(final View _view, final int _position);
}

class MainFileViewHolder extends RecyclerView.ViewHolder
{
    public ImageView image;
    public TextView title;
    public TextView contents;
    public TextView date;
    public View view;
    public MainFileViewHolder(final View _view)
    {
        super(_view);
        this.view = _view;
        image = (ImageView)itemView.findViewById(R.id.item_mainfile_image);
        title = (TextView)itemView.findViewById(R.id.item_mainfile_title);
        contents = (TextView)itemView.findViewById(R.id.item_mainfile_context);
        date = (TextView)itemView.findViewById(R.id.item_mainfile_date);
    }
}

class RecyclerViewPadding extends RecyclerView.ItemDecoration
{
    private int bottom;
    private int left;
    private int right;
    public RecyclerViewPadding(final int _bottom, final int _right, final int _left)
    {
        this.bottom = _bottom;
        this.right = _right;
        this.left = _left;
    }

    @Override
    public void getItemOffsets(Rect _outRect, View _view, RecyclerView _parent, RecyclerView.State _state) {
        super.getItemOffsets(_outRect, _view, _parent, _state);
        _outRect.bottom = bottom;
        _outRect.right = right;
        _outRect.left = left;
    }
}

public class MainFileAdaptor extends RecyclerView.Adapter<MainFileViewHolder>
{
    private ArrayList<MainFileObject> mainFiles;
    private ClickAction action;

    public MainFileAdaptor(final ArrayList<MainFileObject> _mainFiles) {
        this.mainFiles = _mainFiles;
    }

    public void setClickAction(final ClickAction _action)
    {
        this.action = _action;
    }

    @Override
    public MainFileViewHolder onCreateViewHolder(final ViewGroup _parent, final int _viewType) {
        View view = LayoutInflater.from(_parent.getContext()).inflate(R.layout.item_mainfile_layout, null);
        return new MainFileViewHolder(view);
    }

    @Override
    public int getItemViewType(final int _position) {
        return mainFiles.get(_position).type;
    }

    @Override
    public void onBindViewHolder(final MainFileViewHolder _holder, final int _position) {
        if(getItemViewType(_position) == Constant.LISTVIEW_FILE_TYPE_IMAGE)
        {
            Bitmap bitmap = decodeBitmapFromResource(mainFiles.get(_position).url, 100, 100);
            _holder.image.setImageBitmap(bitmap);
            _holder.title.setText(mainFiles.get(_position).title);
            _holder.date.setText(mainFiles.get(_position).date);
            _holder.contents.setVisibility(View.GONE);
        }
        else
        {
            _holder.title.setText(mainFiles.get(_position).title);
            _holder.contents.setText(mainFiles.get(_position).context_oneline);
            _holder.date.setText(mainFiles.get(_position).date);
            _holder.image.setVisibility(View.GONE);
        }

        _holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                if(action != null)
                {
                    action.onClick(_v, _holder.getAdapterPosition());
                }
            }
        });
        _holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View _v) {
                if(action != null)
                {
                    action.onLongClick(_v, _holder.getAdapterPosition());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainFiles.size();
    }

    private static int calculateInBitmapSize(BitmapFactory.Options _options, int _reqWidth, int _reqHeight) {
        // Raw bottom and width of image
        final int height = _options.outHeight;
        final int width = _options.outWidth;
        int inSampleSize = 1;

        if (height > _reqHeight || width > _reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // bottom and width larger than the requested bottom and width.
            while ((halfHeight / inSampleSize) >= _reqHeight
                    && (halfWidth / inSampleSize) >= _reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeBitmapFromResource(final String _bitmap, int _reqWidth, int _reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(_bitmap, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInBitmapSize(options, _reqWidth, _reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(_bitmap, options);
    }
}