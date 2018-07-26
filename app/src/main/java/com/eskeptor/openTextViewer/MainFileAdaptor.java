package com.eskeptor.openTextViewer;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eskeptor.openTextViewer.datatype.MainFileObject;


import java.util.ArrayList;


/*
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */


/**
 * Interface that defined the click action of the main page
 */
interface ClickAction {
    void onClick(final View view, final int position);
    void onLongClick(final View view, final int position);
}

/**
 * View holders for main page files
 */
class MainFileViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImage;
    public TextView mTitle;
    public TextView mContents;
    public TextView mDate;
    public View mView;
    public RelativeLayout mViewForeground;
    public RelativeLayout mViewBackground;

    MainFileViewHolder(final View view) {
        super(view);
        mView = view;
        mImage = itemView.findViewById(R.id.item_mainfile_image);
        mTitle = itemView.findViewById(R.id.item_mainfile_title);
        mContents = itemView.findViewById(R.id.item_mainfile_context);
        mDate = itemView.findViewById(R.id.item_mainfile_date);
        mViewForeground = itemView.findViewById(R.id.item_mainfile_foreground);
        mViewBackground = itemView.findViewById(R.id.item_mainfile_background);
    }
}

/**
 * For Padding in the RecyclerView
 */
class RecyclerViewPadding extends RecyclerView.ItemDecoration {
    private int mBottom;
    private int mLeft;
    private int mRight;
    private int mTop;

    RecyclerViewPadding(final int bottom, final int right, final int left, final int top) {
        mBottom = bottom;
        mRight = right;
        mLeft = left;
        mTop = top;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mBottom;
        outRect.right = mRight;
        outRect.left = mLeft;
        outRect.top = mTop;
    }
}

/**
 * Adapter for main file
 */
public class MainFileAdaptor extends RecyclerView.Adapter<MainFileViewHolder> {
    private ArrayList<MainFileObject> mMainFiles;
    private ClickAction mAction;
    private SharedPreferences mSharedPref;

    MainFileAdaptor(final ArrayList<MainFileObject> mainFiles, SharedPreferences sharedPref) {
        this.mMainFiles = mainFiles;
        mSharedPref = sharedPref;
    }

    public void setClickAction(final ClickAction action) {
        this.mAction = action;
    }

    @NonNull
    @Override
    public MainFileViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainfile_layout, null);
        return new MainFileViewHolder(view);
    }

    @Override
    public int getItemViewType(final int position) {
        return mMainFiles.get(position).mFileType.getValue();
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFileViewHolder holder, final int position) {
        boolean isViewImage = mSharedPref.getBoolean(Constant.APP_VIEW_IMAGE, true);
        if (getItemViewType(position) == MainFileObject.FileType.Image.getValue()) {
            if(isViewImage) {
                Bitmap bitmap = decodeBitmapFromResource(mMainFiles.get(position).mFilePath, 100, 100);
                holder.mImage.setImageBitmap(bitmap);
                holder.mImage.setVisibility(View.VISIBLE);
                holder.mContents.setVisibility(View.GONE);
            } else {
                holder.mImage.setVisibility(View.GONE);
                holder.mContents.setVisibility(View.VISIBLE);
                holder.mContents.setText(mMainFiles.get(position).mOneLinePreview);
            }
            holder.mTitle.setText(mMainFiles.get(position).mFileTitle);
            holder.mDate.setText(mMainFiles.get(position).mModifyDate);
        } else {
            holder.mTitle.setText(mMainFiles.get(position).mFileTitle);
            holder.mContents.setText(mMainFiles.get(position).mOneLinePreview);
            holder.mDate.setText(mMainFiles.get(position).mModifyDate);
            holder.mImage.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    mAction.onClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mAction != null) {
                    mAction.onLongClick(v, holder.getAdapterPosition());
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMainFiles.size();
    }

    /**
     * How to calculate the size of a bitmap
     * @param options Option
     * @param reqWidth Width
     * @param reqHeight Height
     * @return Size
     */
    private static int calculateInBitmapSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw mBottom and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // mBottom and width larger than the requested mBottom and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * How to Convert Resources to Bitmap
     * @param bitmap Path to Bitmap
     * @param reqWidth Width
     * @param reqHeight Height
     * @return Bitmap
     */
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