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

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

interface ClickAction
{
    public void onClick(final View view, final int position);
    public void onLongClick(final View view, final int position);
}

class MainFileViewHolder extends RecyclerView.ViewHolder
{
    public ImageView image;
    public TextView title;
    public TextView contents;
    public TextView date;
    public View view;
    public MainFileViewHolder(final View view)
    {
        super(view);
        this.view = view;
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
    public RecyclerViewPadding(final int bottom, final int right, final int left)
    {
        this.bottom = bottom;
        this.right = right;
        this.left = left;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = bottom;
        outRect.right = right;
        outRect.left = left;
    }
}

public class MainFileAdaptor extends RecyclerView.Adapter<MainFileViewHolder>
{
    private ArrayList<MainFile> mainFiles;
    private ClickAction action;

    public MainFileAdaptor(final ArrayList<MainFile> mainFiles) {
        this.mainFiles = mainFiles;
    }

    public void setClickAction(final ClickAction action)
    {
        this.action = action;
    }

    @Override
    public MainFileViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainfile_layout, null);
        return new MainFileViewHolder(view);
    }

    @Override
    public int getItemViewType(final int position) {
        return mainFiles.get(position).type;
    }

    @Override
    public void onBindViewHolder(final MainFileViewHolder holder, final int position) {
        if(getItemViewType(position) == Constant.LISTVIEW_FILE_TYPE_IMAGE)
        {
            Bitmap bitmap = decodeBitmapFromResource(mainFiles.get(position).url, 100, 100);
            holder.image.setImageBitmap(bitmap);
            holder.title.setText(mainFiles.get(position).title);
            holder.date.setText(mainFiles.get(position).date);
            holder.contents.setVisibility(View.GONE);
        }
        else
        {
            holder.title.setText(mainFiles.get(position).title);
            holder.contents.setText(mainFiles.get(position).context_oneline);
            holder.date.setText(mainFiles.get(position).date);
            holder.image.setVisibility(View.GONE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action != null)
                {
                    action.onClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(action != null)
                {
                    action.onLongClick(v, holder.getAdapterPosition());
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

    private static int calculateInBitmapSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw bottom and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // bottom and width larger than the requested bottom and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeBitmapFromResource(final String bitmap, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmap, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInBitmapSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(bitmap, options);
    }
}