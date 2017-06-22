package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.eskeptor.openTextViewer.datatype.BrushObject;
import com.eskeptor.openTextViewer.textManager.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PaintActivity extends AppCompatActivity {
    private PaintFunction paintFunction;
    private LinearLayout drawLayout;
    private LinearLayout brushLayout;
    private LinearLayout eraserLayout;
    private SeekBar brushSeekSize;
    private SeekBar brushSeekRed;
    private SeekBar brushSeekGreen;
    private SeekBar brushSeekBlue;
    private SeekBar eraserSeekSize;
    private TextView brushTxtSize;
    private TextView brushTxtRed;
    private TextView brushTxtGreen;
    private TextView brushTxtBlue;
    private TextView eraserTxtSize;

    private Context context_this;

    private AlertDialog.Builder alert;

    private int curEraserSize;
    private int curBrushValue;
    private int curRedValue;
    private int curGreenValue;
    private int curBlueValue;

    private String openFolderURL;
    private String openFileName;
    private String openFileURL;
    //private int memoType;
    private int memoIndex;
    private LogManager logManager;
    private File lastLog;

    private Runnable runnable;

    private static MenuItem undo;

    @Override
    protected void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_paint);

        context_this = getApplicationContext();

        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
        openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);

        logManager = new LogManager();

        curBrushValue = (int)Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        curEraserSize = (int)Constant.PAINT_ERASER_WIDTH_PIXEL;
        curRedValue = 0;
        curGreenValue = 0;
        curBlueValue = 0;

        drawLayout = (LinearLayout)findViewById(R.id.activity_paint);
        eraserLayout = (LinearLayout)findViewById(R.id.paint_eraser_seekLayout);
        eraserSeekSize = (SeekBar)findViewById(R.id.paint_eraser_seekSize);
        eraserTxtSize = (TextView)findViewById(R.id.paint_eraser_txtSize);
        brushLayout = (LinearLayout)findViewById(R.id.paint_brush_seekLayout);
        brushSeekSize = (SeekBar)findViewById(R.id.paint_brush_seekSize);
        brushSeekRed = (SeekBar)findViewById(R.id.paint_brush_seekRed);
        brushSeekGreen = (SeekBar)findViewById(R.id.paint_brush_seekGreen);
        brushSeekBlue = (SeekBar)findViewById(R.id.paint_brush_seekBlue);
        brushTxtSize = (TextView)findViewById(R.id.paint_brush_txtSize);
        brushTxtRed = (TextView)findViewById(R.id.paint_brush_txtRed);
        brushTxtGreen = (TextView)findViewById(R.id.paint_brush_txtGreen);
        brushTxtBlue = (TextView)findViewById(R.id.paint_brush_txtBlue);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {
                if(_seekBar == brushSeekSize)
                {
                    brushTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), _progress));
                }
                if(_seekBar == brushSeekRed)
                {
                    brushTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), _progress));
                }
                if(_seekBar == brushSeekGreen)
                {
                    brushTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), _progress));
                }
                if(_seekBar == brushSeekBlue)
                {
                    brushTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), _progress));
                }
                if(_seekBar == eraserSeekSize)
                {
                    eraserTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), _progress));
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar _seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar _seekBar) {
                if(_seekBar == brushSeekSize)
                {
                    curBrushValue = _seekBar.getProgress();
                    paintFunction.setLineWidth(curBrushValue);
                }
                if(_seekBar == brushSeekRed)
                {
                    curRedValue = _seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(_seekBar == brushSeekGreen)
                {
                    curGreenValue = _seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(_seekBar == brushSeekBlue)
                {
                    curBlueValue = _seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(_seekBar == eraserSeekSize)
                {
                    curEraserSize = _seekBar.getProgress();
                    paintFunction.setLineWidth(curEraserSize);
                }
            }
        };

        brushSeekSize.setOnSeekBarChangeListener(seekBarChangeListener);
        brushSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        brushSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        brushSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);
        eraserSeekSize.setOnSeekBarChangeListener(seekBarChangeListener);

        brushSeekSize.setMax((int)Constant.PAINT_MAXIMUM_WIDTH);
        brushSeekSize.setProgress(curBrushValue);
        brushSeekRed.setMax(Constant.PAINT_COLOR_MAX);
        brushSeekRed.setProgress(curRedValue);
        brushSeekGreen.setMax(Constant.PAINT_COLOR_MAX);
        brushSeekGreen.setProgress(curGreenValue);
        brushSeekBlue.setMax(Constant.PAINT_COLOR_MAX);
        brushSeekBlue.setProgress(curBlueValue);
        eraserSeekSize.setMax((int)Constant.PAINT_MAXIMUM_WIDTH);
        eraserSeekSize.setProgress(curEraserSize);

        brushLayout.setVisibility(View.GONE);
        eraserLayout.setVisibility(View.GONE);

        runnable = new Runnable() {
            @Override
            public void run() {
                paintFunction = new PaintFunction(context_this);
                initPaint();
                paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                drawLayout.addView(paintFunction);
            }
        };

        runOnUiThread(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_paint, _menu);
        undo = _menu.findItem(R.id.menu_paint_undo);
        undo.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem _item) {
        int id = _item.getItemId();
        if(id == R.id.menu_paint_pen)
        {
            paintFunction.changePaint(Constant.PAINT_TYPE_BRUSH);
            paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
            paintFunction.setLineWidth(curBrushValue);
            if(brushLayout.getVisibility() == View.VISIBLE)
            {
                brushLayout.setVisibility(View.GONE);
            }
            else
            {
                if(eraserLayout.getVisibility() == View.VISIBLE)
                {
                    eraserLayout.setVisibility(View.GONE);
                }
                brushLayout.setVisibility(View.VISIBLE);
            }
        }
        else if(id == R.id.menu_paint_eraser)
        {
            paintFunction.changePaint(Constant.PAINT_TYPE_ERASER);
            paintFunction.setLineWidth(curEraserSize);
            if(eraserLayout.getVisibility() == View.VISIBLE)
            {
                eraserLayout.setVisibility(View.GONE);
            }
            else
            {
                if(brushLayout.getVisibility() == View.VISIBLE)
                {
                    brushLayout.setVisibility(View.GONE);
                }
                eraserLayout.setVisibility(View.VISIBLE);
            }
        }
        else if(id == R.id.menu_paint_reset)
        {
            paintFunction.resetPaint();
        }
        else if(id == R.id.menu_paint_undo)
        {
//            int color;
            paintFunction.undoCanvas();
            /*color = paintFunction.curColor;
            curRedValue = Color.red(color);
            curGreenValue = Color.green(color);
            curBlueValue = Color.blue(color);
            brushSeekRed.setProgress(curRedValue);
            brushSeekGreen.setProgress(curGreenValue);
            brushSeekBlue.setProgress(curBlueValue);*/
            paintFunction.invalidate();
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        paintFunction = null;
        drawLayout = null;
        brushLayout = null;
        eraserLayout = null;
        brushSeekSize = null;   brushSeekRed = null;    brushSeekGreen = null;  brushSeekBlue = null;
        eraserSeekSize = null;  eraserTxtSize = null;
        brushTxtSize = null;    brushTxtRed = null;     brushTxtGreen = null;   brushTxtBlue = null;
        context_this = null;
        if(alert != null)
            alert = null;
        logManager = null;
        undo = null;
        openFolderURL = null;
        openFileName = null;
        openFileURL = null;
        lastLog = null;
        runnable = null;
        System.gc();
    }

    @Override
    public void onBackPressed() {
        if(paintFunction.isModified())
        {
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    if(_which == AlertDialog.BUTTON_POSITIVE)
                    {
                        if(openFileURL == null)
                        {
                            alert = new AlertDialog.Builder(PaintActivity.this);
                            alert.setTitle(R.string.memo_alert_save_context);
                            alert.setItems(R.array.main_selectalert, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface _dialog, int _which) {
                                    if(_which == Constant.MEMO_SAVE_SELECT_TYPE_EXTERNAL)
                                    {
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setClass(context_this, FileBrowserActivity.class);
                                        intent.setType("text/plain");
                                        intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE);
                                        startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE);
                                    }
                                    else if(_which == Constant.MEMO_SAVE_SELECT_TYPE_INTERNAL)
                                    {
                                        if(paintFunction.isFileopen())
                                        {
                                            paintFunction.savePaint(openFolderURL);
                                        }
                                        else
                                        {
                                            openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_IMAGE_EXTENSION);
                                            File tmpFile = new File(openFileURL);
                                            while(tmpFile.exists())
                                            {
                                                memoIndex++;
                                                openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_IMAGE_EXTENSION);
                                                tmpFile = new File(openFileURL);
                                            }
                                            paintFunction.savePaint(openFileURL);
                                            writeLog();
                                        }
                                        finish();
                                    }
                                    _dialog.dismiss();
                                }
                            });
                            alert.show();
                        }
                        else
                        {
                            paintFunction.savePaint(openFileURL);
                            finish();
                        }
                    }
                    else if(_which == AlertDialog.BUTTON_NEGATIVE)
                    {
                        finish();
                    }
                    _dialog.dismiss();
                }
            };
            alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.memo_alert_modified_title);
            alert.setMessage(R.string.memo_alert_modified_context);
            alert.setPositiveButton(R.string.memo_alert_modified_btnSave, clickListener);
            alert.setNegativeButton(R.string.memo_alert_modified_btnDiscard, clickListener);
            alert.show();
        }
        else
        {
            writeLog();
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private void writeLog()
    {
        try {
            if(!paintFunction.isFileopen())
            {
                logManager.saveLog(Integer.toString(memoIndex), lastLog.getPath());
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    private void initPaint()
    {
        if(openFileURL == null)
        {
            lastLog = new File(openFolderURL + File.separator + Constant.FILE_LOG_COUNT);
            if(!lastLog.exists())
            {
                try
                {
                    lastLog.createNewFile();
                    memoIndex = 1;
                    logManager.saveLog(Integer.toString(memoIndex), lastLog.getPath());
                }
                catch (IOException ioe){ioe.printStackTrace();}
            }
            else
            {
                try
                {
                    memoIndex = Integer.parseInt(logManager.openLog(lastLog.getPath()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setTitle(R.string.memo_title_newFile);
            paintFunction.setBitmap((openFileURL == null), openFolderURL, Integer.toString(memoIndex));
            drawLayout.setBackgroundColor(Color.WHITE);
        }
        else
        {
            openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
            openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
            setTitle(openFileName);
            paintFunction.setBitmap((openFileURL == null), openFileURL, null);
        }
    }







    static class PaintFunction extends View{
        private float curX;
        private float curY;
        private float prevX;
        private float prevY;

        private Paint canvasPaint;
        private Paint brushPaint;
        private Canvas canvas;
        private Path path;
        private Bitmap bitmap;
        private BrushObject brushObject;

        private int screenWidth;
        private int screenHeight;

        private int curColor;
        private float curSize;

        private boolean fileopen;
        private String folderUrl;
        private String filename;
        private boolean modified;

        public PaintFunction(final Context _context)
        {
            super(_context);
            DisplayMetrics displayMetrics = _context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;
        }

        public PaintFunction(final Context _context, final AttributeSet _attributeSet)
        {
            super(_context, _attributeSet);
            DisplayMetrics displayMetrics = _context.getApplicationContext().getResources().getDisplayMetrics();
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

            if(brushObject != null)
            {
                brushObject.init();
            }
            else
            {
                brushObject = new BrushObject();
            }

            if(path != null)
            {
                path = null;
            }
            path = new Path();

            if(canvasPaint != null)
            {
                canvasPaint.reset();
                canvasPaint = null;
            }
            canvasPaint = new Paint(Paint.DITHER_FLAG);

            if(brushPaint != null)
            {
                brushPaint.reset();
                brushPaint = null;
            }
            brushPaint = new Paint();
            brushPaint.setAlpha(Constant.PAINT_COLOR_MAX);
            brushPaint.setDither(true);
            brushPaint.setStrokeWidth(Constant.PAINT_DEFAULT_WIDTH_PIXEL);
            brushPaint.setStrokeJoin(Paint.Join.ROUND);
            brushPaint.setStyle(Paint.Style.STROKE);
            brushPaint.setStrokeCap(Paint.Cap.ROUND);
            brushPaint.setAntiAlias(true);

            setLineWidth(Constant.PAINT_DEFAULT_WIDTH_PIXEL);
        }

        @Override
        protected void onDraw(final Canvas _canvas) {
            if(bitmap != null)
            {
                _canvas.drawBitmap(bitmap, 0, 0, canvasPaint);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if(bitmap != null)
                bitmap.recycle();
            bitmap = null;
            path = null;
            canvas = null;
            canvasPaint = null;
            brushPaint = null;
            brushObject.init();
            folderUrl = null;
            filename = null;
        }

        @Override
        public boolean onTouchEvent(final MotionEvent _event) {
            curX = _event.getX();
            curY = _event.getY();

            if((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN)
            {
                path.reset();
                path.moveTo(curX, curY);
                prevX = curX;
                prevY = curY;
                invalidate();
                return true;
            }
            else if((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
            {
                if(Math.abs(curX - prevX) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL || Math.abs(curY - prevY) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL)
                {
                    path.quadTo(prevX, prevY, curX, curY);
                    prevX = curX;
                    prevY = curY;
                }
                canvas.drawPath(path, brushPaint);
                invalidate();
                return true;
            }
            else if((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
            {
                brushObject.brushPaths.add(new Path(path));
                brushObject.brushSizes.add(brushPaint.getStrokeWidth());
                brushObject.brushColor.add(curColor);
                brushObject.brushPathsIdx++;
                undo.setVisible(true);
                invalidate();
            }

            modified = true;
            return false;
        }

        public void setColor(final int _color)
        {
            curColor = _color;
            brushPaint.setColor(curColor);
            invalidate();
        }

        public void setLineWidth(final float _lineWidth)
        {
            curSize = _lineWidth;
            brushPaint.setStrokeWidth(curSize);
            invalidate();
        }

        public void resetPaint()
        {
            reset();
            setColor(curColor);
            invalidate();
        }

        public boolean isFileopen()
        {
            return fileopen;
        }

        public void setBitmap(final boolean _memoType, final String _folderUrl, @Nullable final String _fileName)
        {
            if(_memoType)
            {
                this.folderUrl = _folderUrl;
                fileopen = false;
            }
            else
            {
                this.filename = _folderUrl;
                fileopen = true;
            }
            reset();
        }

        public void savePaint(final String _dir)
        {
            FileOutputStream fos = null;
            this.draw(canvas);
            try
            {
                fos = new FileOutputStream(new File(_dir));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (Exception e){e.printStackTrace();}
            finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean isModified()
        {
            return modified;
        }

        public void undoCanvas()
        {
            if(brushObject.brushPathsIdx != 0)
            {
                brushObject.brushPaths.remove(--brushObject.brushPathsIdx);
                brushObject.brushSizes.remove(brushObject.brushPathsIdx);
                brushObject.brushColor.remove(brushObject.brushPathsIdx);
                path.reset();
                drawLine();
            }

            if(brushObject.brushPathsIdx == 0)
            {
                undo.setVisible(false);
            }
            invalidate();
        }

        public void changePaint(final int _type)
        {
            if(_type == Constant.PAINT_TYPE_BRUSH)
            {
                brushPaint.setColor(curColor);
            }
            else
            {
                curColor = Color.WHITE;
                brushPaint.setColor(curColor);
            }
            invalidate();
        }

        private void drawLine()
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

            int idx = brushObject.brushPathsIdx;
            for(int i = 0; i < idx; i++)
            {
                brushPaint.setStrokeWidth(brushObject.brushSizes.get(i));
                brushPaint.setColor(brushObject.brushColor.get(i));
                canvas.drawPath(brushObject.brushPaths.get(i), brushPaint);
            }
            brushPaint.setStrokeWidth(curSize);
            brushPaint.setColor(curColor);
        }
    }
}
