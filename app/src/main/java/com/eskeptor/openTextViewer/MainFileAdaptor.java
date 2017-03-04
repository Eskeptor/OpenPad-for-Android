package com.eskeptor.openTextViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

class ImageFileViewHolder
{
    public ImageView image;
    public TextView title;
    public TextView date;
}

public class MainFileAdaptor extends BaseAdapter
{
    private Context context;
    private ArrayList<MainFile> mainFiles;

    public MainFileAdaptor(final Context context, final ArrayList<MainFile> mainFiles)
    {
        this.context = context;
        this.mainFiles = mainFiles;
    }

    @Override
    public int getCount()
    {
        return mainFiles.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mainFiles.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mainFiles.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(getItemViewType(position) == Constant.LISTVIEW_FILE_TYPE_IMAGE)
        {
            ImageFileViewHolder holder;

            if(convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_imagefile_layout, null);
                holder = new ImageFileViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.item_imagefile_image);
                holder.title = (TextView)convertView.findViewById(R.id.item_imagefile_title);
                holder.date = (TextView)convertView.findViewById(R.id.item_imagefile_date);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ImageFileViewHolder)convertView.getTag();
            }
            Bitmap bitmap = decodeBitmapFromResource(mainFiles.get(position).url, 100, 100);
            holder.image.setImageBitmap(bitmap);
            holder.title.setText(mainFiles.get(position).title);
            holder.date.setText(mainFiles.get(position).date);

            return convertView;
        }
        else
        {
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
            holder.title.setText(mainFiles.get(position).title);
            holder.context.setText(mainFiles.get(position).context_oneline);
            holder.date.setText(mainFiles.get(position).date);
            return convertView;
        }
    }

    private static int calculateInBitmapSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
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