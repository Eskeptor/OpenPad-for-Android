package com.eskeptor.openTextViewer;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by narut on 2017-03-01.
 */
public class PaintFunction extends View{
    private float maximumPixel = 4.0f;
    private float curX;
    private float curY;
    private float prevX;
    private float prevY;
    private Paint paint;
    private Canvas canvas;
    private Path path;
    private Bitmap bitmap;

    public PaintFunction(final Context context)
    {
        super(context);
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        init(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public PaintFunction(final Context context, final AttributeSet attributeSet)
    {
        super(context, attributeSet);
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        init(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    private void init(final int width, final int height)
    {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        path = new Path();

        paint = new Paint();
        paint.setAlpha(255);
        paint.setDither(true);
        paint.setStrokeWidth(10);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
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
            return true;
        }
        else if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
        {
            if(Math.abs(curX - prevY) >= maximumPixel || Math.abs(curY - prevY) >= maximumPixel)
            {
                path.quadTo(prevX, prevY, curX, curY);
                prevX = curX;
                prevY = curY;
                canvas.drawPath(path, paint);
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setColor(final int color)
    {
        paint.setColor(color);
    }

    public void setMaximumPixel(final float maximumPixel)
    {
        this.maximumPixel = maximumPixel;
    }

    public float getMaximumPixel()
    {
        return maximumPixel;
    }
}
