package com.eskeptor.openTextViewer;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by narut on 2017-03-01.
 */
/*
public class PaintFunction extends View{
    private float curX;
    private float curY;
    private float prevX;
    private float prevY;

    private Paint paint;
    private Canvas canvas;
    private Path path;
    private Bitmap bitmap;

    private int screenWidth;
    private int screenHeight;

    private float curLineWidth;
    private int curColor;

    private boolean fileopen;
    private String folderUrl;
    private String filename;
    private boolean modified;

    public PaintFunction(final Context context)
    {
        super(context);
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    public PaintFunction(final Context context, final AttributeSet attributeSet)
    {
        super(context, attributeSet);
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }


    private void reset()
    {
        if(fileopen)
        {
            if(bitmap != null)
            {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = BitmapFactory.decodeFile(filename).copy(Bitmap.Config.ARGB_8888, true);
        }
        else
        {
            if(bitmap != null)
            {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        }

        if(canvas != null)
        {
            canvas = null;
        }
        canvas = new Canvas(bitmap);
        if(!fileopen)
        {
            canvas.drawARGB(Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX);
        }

        Log.e("Debug", "save count : " + Integer.toString(canvas.getSaveCount()));

        if(path != null)
        {
            path = null;
        }
        path = new Path();

        paint = new Paint();
        paint.setAlpha(255);
        paint.setDither(true);
        paint.setStrokeWidth(Constant.PAINT_DEFAULT_WIDTH_PIXEL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        if(curLineWidth == 0.0f)
        {
            curLineWidth = Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        }
        setLineWidth(curLineWidth);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if(bitmap != null)
        {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(bitmap != null)
            bitmap.recycle();
        bitmap = null;
        path = null;
        canvas = null;
        paint = null;
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        curX = event.getX();
        curY = event.getY();

        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN)
        {
            path.reset();
            path.moveTo(curX, curY);
            prevX = curX;
            prevY = curY;
            canvas.drawPoint(curX, curY, paint);
            invalidate();
            return true;
        }
        else if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
        {
            if(Math.abs(curX - prevX) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL || Math.abs(curY - prevY) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL)
            {
                path.quadTo(prevX, prevY, curX, curY);
                prevX = curX;
                prevY = curY;
                canvas.drawPath(path, paint);
            }
            invalidate();
            return true;
        }
        modified = true;
        return false;
    }

    public void setColor(final int color)
    {
        curColor = color;
        paint.setColor(curColor);
    }

    public void setLineWidth(final float lineWidth)
    {
        curLineWidth = lineWidth;
        paint.setStrokeWidth(curLineWidth);
    }

    public void resetPaint()
    {
        reset();
        setColor(curColor);
        invalidate();
        Log.e("Debug", "save count : " + Integer.toString(canvas.getSaveCount()));
    }

    public boolean isFileopen()
    {
        return fileopen;
    }

    public float getCurLineWidth()
    {
        return curLineWidth;
    }

    public void setBitmap(final int memoType, final String folderUrl, @Nullable final String fileName)
    {
        if(memoType == Constant.MEMO_TYPE_NEW)
        {
            this.folderUrl = folderUrl;
            fileopen = false;
        }
        else
        {
            this.filename = folderUrl;
            fileopen = true;
        }
        reset();
    }

    public void savePaint(final String dir)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(new File(dir));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (Exception e){e.printStackTrace();}
        finally {
            try{fos.close();}
            catch (Exception e){e.printStackTrace();}
        }
    }

    public boolean isModified()
    {
        return modified;
    }
}
*/
