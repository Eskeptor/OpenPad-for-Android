package com.eskeptor.openTextViewer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eskeptor.openTextViewer.datatype.MainFileObject;


import java.util.ArrayList;

import util.TestLog;

import static android.support.v7.widget.helper.ItemTouchHelper.*;


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

    MainFileViewHolder(final View view) {
        super(view);
        mView = view;
        mImage = itemView.findViewById(R.id.item_mainfile_image);
        mTitle = itemView.findViewById(R.id.item_mainfile_title);
        mContents = itemView.findViewById(R.id.item_mainfile_context);
        mDate = itemView.findViewById(R.id.item_mainfile_date);
    }
}

/**
 * For Padding in the RecyclerView
 */
class RecyclerViewPadding extends RecyclerView.ItemDecoration {
    private int mBottom;
    private int mLeft;
    private int mRight;

    RecyclerViewPadding(final int bottom, final int right, final int left) {
        mBottom = bottom;
        mRight = right;
        mLeft = left;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mBottom;
        outRect.right = mRight;
        outRect.left = mLeft;
    }
}

abstract class SwipeControllerActions {
    public void onRightClicked(int position) {}
}

/**
 * For Swipe Action in the RecyclerView
 */
class SwipeController extends ItemTouchHelper.Callback {
    public enum SwipeButtonsState {
        GONE, RIGHT, LEFT
    }

    private static final float BUTTON_WIDTH = 100f;

    private boolean mSwipeBack = false;
    private SwipeButtonsState mButtonsState = SwipeButtonsState.GONE;
    private RectF mButton;
    private RecyclerView.ViewHolder mCurViewHolder;
    private SwipeControllerActions mActions;

    public SwipeController(SwipeControllerActions actions) {
        mActions = actions;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (mSwipeBack) {
            mSwipeBack = mButtonsState != SwipeButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (mButtonsState != SwipeButtonsState.GONE) {
                if (mButtonsState == SwipeButtonsState.RIGHT) {
                    dX = Math.min(dX, -BUTTON_WIDTH);
                }
                if (mButtonsState == SwipeButtonsState.LEFT) {
                    dX = 0.0f;
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
        if (mButtonsState == SwipeButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        mCurViewHolder = viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSwipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (mSwipeBack) {
                    if (dX < -BUTTON_WIDTH) {
                        mButtonsState = SwipeButtonsState.RIGHT;
                    } else if (dX > BUTTON_WIDTH) {
                        mButtonsState = SwipeButtonsState.LEFT;
                    }

                    if (mButtonsState != SwipeButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TestLog.Tag("Test").Logging(TestLog.LogType.ERROR, "dX: " + dX);
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    TestLog.Tag("Test").Logging(TestLog.LogType.ERROR, "dX: " + dX);
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0.0f, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    mSwipeBack = false;

                    if (mActions != null && mButton != null && mButton.contains(event.getX(), event.getY())) {
                        if (mButtonsState == SwipeButtonsState.RIGHT) {
                            mActions.onRightClicked(viewHolder.getAdapterPosition());
                        }
                    }

                    mButtonsState = SwipeButtonsState.GONE;
                    mCurViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = BUTTON_WIDTH - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("X", c, rightButton, p);

        mButton = null;
        if (mButtonsState == SwipeButtonsState.RIGHT) {
            mButton = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 50;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    public void onDraw(Canvas c) {
        if (mCurViewHolder != null) {
            drawButtons(c, mCurViewHolder);
        }
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