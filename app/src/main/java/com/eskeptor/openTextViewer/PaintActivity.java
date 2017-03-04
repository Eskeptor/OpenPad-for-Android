package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
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

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;

    private int eraserColor;

    private int curEraserSize;
    private int curBrushValue;
    private int curRedValue;
    private int curGreenValue;
    private int curBlueValue;

    private String openFolderURL;
    private String openFileName;
    private String openFileURL;
    private int memoType;
    private int memoIndex;
    private LogManager logManager;
    private File lastLog;

    private Runnable runnable;

    private MenuItem undo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        context_this = getApplicationContext();

        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        memoType = getIntent().getIntExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);

        logManager = new LogManager();

        curBrushValue = (int)Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        curEraserSize = (int)Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        curRedValue = 0;
        curGreenValue = 0;
        curBlueValue = 0;

        eraserColor = Color.WHITE;

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

        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar == brushSeekSize)
                {
                    brushTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), progress));
                }
                if(seekBar == brushSeekRed)
                {
                    brushTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                }
                if(seekBar == brushSeekGreen)
                {
                    brushTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                }
                if(seekBar == brushSeekBlue)
                {
                    brushTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                }
                if(seekBar == eraserSeekSize)
                {
                    eraserTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar == brushSeekSize)
                {
                    curBrushValue = seekBar.getProgress();
                    paintFunction.setLineWidth(curBrushValue);
                }
                if(seekBar == brushSeekRed)
                {
                    curRedValue = seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(seekBar == brushSeekGreen)
                {
                    curGreenValue = seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(seekBar == brushSeekBlue)
                {
                    curBlueValue = seekBar.getProgress();
                    paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
                }
                if(seekBar == eraserSeekSize)
                {
                    curEraserSize = seekBar.getProgress();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paint, menu);
        undo = menu.findItem(R.id.menu_paint_undo);
        undo.setVisible(false);

        //todo 버튼 활성화 비활성화 연동시키기
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_paint_pen)
        {
            paintFunction.setColor(Color.rgb(curRedValue, curGreenValue, curBlueValue));
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
        else if(item.getItemId() == R.id.menu_paint_eraser)
        {
            paintFunction.setColor(eraserColor);
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
        else if(item.getItemId() == R.id.menu_paint_reset)
        {
            paintFunction.resetPaint();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        if(paintFunction.isModified())
        {
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == AlertDialog.BUTTON_POSITIVE)
                    {
                        Log.e("Debug", "which : " + Integer.toString(which));
                        Log.e("Debug", "memoType : " + Integer.toString(memoType));
                        if(memoType == Constant.MEMO_TYPE_NEW)
                        {
                            alert = new AlertDialog.Builder(PaintActivity.this);
                            alert.setTitle(R.string.memo_alert_save_context);
                            alert.setItems(R.array.main_selectalert, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == Constant.MEMO_SAVE_SELECT_TYPE_EXTERNAL)
                                    {
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setClass(context_this, FileBrowserActivity.class);
                                        intent.setType("text/plain");
                                        intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE);
                                        startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE);
                                    }
                                    else if(which == Constant.MEMO_SAVE_SELECT_TYPE_INTERNAL)
                                    {
                                        if(paintFunction.isFileopen())
                                        {
                                            paintFunction.savePaint(openFolderURL);
                                        }
                                        else
                                        {
                                            openFileName = openFolderURL + File.separator + (memoIndex + Constant.FILE_IMAGE_EXTENSION);
                                            paintFunction.savePaint(openFileName);
                                            memoIndex++;
                                            writeLog();
                                        }
                                        finish();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            alert.show();
                        }
                        else if(memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL || memoType == Constant.MEMO_TYPE_OPEN_INTERNAL)
                        {
                            paintFunction.savePaint(openFileURL);
                            finish();
                        }
                    }
                    else if(which == AlertDialog.BUTTON_NEGATIVE)
                    {
                        finish();
                    }
                    dialog.dismiss();
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
        if(memoType == Constant.MEMO_TYPE_NEW)
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
            paintFunction.setBitmap(memoType, openFolderURL, Integer.toString(memoIndex));
            drawLayout.setBackgroundColor(eraserColor);
        }
        else if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
            openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
            setTitle(openFileName);
            paintFunction.setBitmap(memoType, openFileURL, null);
        }
    }
}
