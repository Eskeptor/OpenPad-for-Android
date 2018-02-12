package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.eskeptor.openTextViewer.datatype.BrushObject;
import com.eskeptor.openTextViewer.datatype.CircleObject;
import com.eskeptor.openTextViewer.textManager.LogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import util.TestLog;

/**
 * Created by eskeptor on 17. 4. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class PaintActivity extends AppCompatActivity {
    private DrawerLayout mPaintDrawerLayout;
    private PaintFunction mPaintFunction;
    private LinearLayout mDrawLayout;
    private LinearLayout mBrushLayout;
    private LinearLayout mEraserLayout;
    private LinearLayout mShapeLayout;
    private SeekBar mBrushSeekSize;
    private SeekBar mBrushSeekRed;
    private SeekBar mBrushSeekGreen;
    private SeekBar mBrushSeekBlue;
    private SeekBar mEraserSeekSize;
    private TextView mBrushTxtSize;
    private TextView mBrushTxtRed;
    private TextView mBrushTxtGreen;
    private TextView mBrushTxtBlue;
    private TextView mEraserTxtSize;
    private ImageView mBrushColor;
    private Button mShapeCircle;
    private Button mShapeRectangle;
    private static Constant.ShapeType mShapeType;

    private Context mContextThis;

    private int mCurEraserSize;
    private int mCurBrushValue;
    private int mCurRedValue;
    private int mCurGreenValue;
    private int mCurBlueValue;

    private String mOpenFolderURL;
    private String mOpenFileName;
    private String mOpenFileURL;
    private int mMemoIndex;
    private File mLastLog;

    private Runnable mRunnable;

    private static MenuItem mMenuItemUndo;

    @Override
    protected void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_paint);

        mContextThis = getApplicationContext();

        mOpenFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        mOpenFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
        mOpenFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);

        mCurBrushValue = (int) Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        mCurEraserSize = (int) Constant.PAINT_ERASER_WIDTH_PIXEL;
        mCurRedValue = 0;
        mCurGreenValue = 0;
        mCurBlueValue = 0;

        mPaintDrawerLayout = (DrawerLayout)findViewById(R.id.activity_paint_drawer);
        mDrawLayout = (LinearLayout) findViewById(R.id.activity_paint);
        mEraserLayout = (LinearLayout) findViewById(R.id.paint_eraser_seekLayout);
        mEraserSeekSize = (SeekBar) findViewById(R.id.paint_eraser_seekSize);
        mEraserTxtSize = (TextView) findViewById(R.id.paint_eraser_txtSize);
        mBrushLayout = (LinearLayout) findViewById(R.id.paint_brush_seekLayout);
        mBrushSeekSize = (SeekBar) findViewById(R.id.paint_brush_seekSize);
        mBrushSeekRed = (SeekBar) findViewById(R.id.paint_brush_seekRed);
        mBrushSeekGreen = (SeekBar) findViewById(R.id.paint_brush_seekGreen);
        mBrushSeekBlue = (SeekBar) findViewById(R.id.paint_brush_seekBlue);
        mBrushTxtSize = (TextView) findViewById(R.id.paint_brush_txtSize);
        mBrushTxtRed = (TextView) findViewById(R.id.paint_brush_txtRed);
        mBrushTxtGreen = (TextView) findViewById(R.id.paint_brush_txtGreen);
        mBrushTxtBlue = (TextView) findViewById(R.id.paint_brush_txtBlue);
        mBrushColor = (ImageView) findViewById(R.id.paint_color);
        mShapeLayout = (LinearLayout) findViewById(R.id.paint_shapesLayout);
        mShapeCircle = (Button) findViewById(R.id.paint_shape_circle);
        mShapeRectangle = (Button) findViewById(R.id.paint_shape_rectangle);
        mShapeType = Constant.ShapeType.None;

        mShapeCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeType = Constant.ShapeType.Circle;
            }
        });
        mShapeRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeType = Constant.ShapeType.Rectangle;
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCurBrushValue, mCurBrushValue);
        params.gravity = Gravity.CENTER;
        params.topMargin = 5;
        params.rightMargin = 15;
        mBrushColor.setLayoutParams(params);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {
                int id = _seekBar.getId();
                switch (id) {
                    case R.id.paint_brush_seekSize:
                        mBrushTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), _progress));
                        break;
                    case R.id.paint_brush_seekRed:
                        mBrushTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), _progress));
                        break;
                    case R.id.paint_brush_seekGreen:
                        mBrushTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), _progress));
                        break;
                    case R.id.paint_brush_seekBlue:
                        mBrushTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), _progress));
                        break;
                    case R.id.paint_eraser_seekSize:
                        mEraserTxtSize.setText(String.format(getResources().getString(R.string.paint_txtBrushSize), _progress));
                        break;

                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar _seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar _seekBar) {
                int id = _seekBar.getId();
                switch (id) {
                    case R.id.paint_brush_seekSize:
                        mCurBrushValue = _seekBar.getProgress();
                        mPaintFunction.setLineWidth(mCurBrushValue);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCurBrushValue, mCurBrushValue);
                        params.gravity = Gravity.CENTER;
                        params.topMargin = 5;
                        params.rightMargin = 15;
                        mBrushColor.setLayoutParams(params);
                        break;
                    case R.id.paint_brush_seekRed:
                        mCurRedValue = _seekBar.getProgress();
                        mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                        mBrushColor.setColorFilter(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue), PorterDuff.Mode.SRC);
                        break;
                    case R.id.paint_brush_seekGreen:
                        mCurGreenValue = _seekBar.getProgress();
                        mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                        mBrushColor.setColorFilter(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue), PorterDuff.Mode.SRC);
                        break;
                    case R.id.paint_brush_seekBlue:
                        mCurBlueValue = _seekBar.getProgress();
                        mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                        mBrushColor.setColorFilter(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue), PorterDuff.Mode.SRC);
                        break;
                    case R.id.paint_eraser_seekSize:
                        mCurEraserSize = _seekBar.getProgress();
                        mPaintFunction.setLineWidth(mCurEraserSize);
                        break;
                }
            }
        };

        mBrushSeekSize.setOnSeekBarChangeListener(seekBarChangeListener);
        mBrushSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        mBrushSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        mBrushSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);
        mEraserSeekSize.setOnSeekBarChangeListener(seekBarChangeListener);

        mBrushSeekSize.setMax((int) Constant.PAINT_MAXIMUM_WIDTH);
        mBrushSeekSize.setProgress(mCurBrushValue);
        mBrushSeekRed.setMax(Constant.PAINT_COLOR_MAX);
        mBrushSeekRed.setProgress(mCurRedValue);
        mBrushSeekGreen.setMax(Constant.PAINT_COLOR_MAX);
        mBrushSeekGreen.setProgress(mCurGreenValue);
        mBrushSeekBlue.setMax(Constant.PAINT_COLOR_MAX);
        mBrushSeekBlue.setProgress(mCurBlueValue);
        mEraserSeekSize.setMax((int) Constant.PAINT_MAXIMUM_WIDTH);
        mEraserSeekSize.setProgress(mCurEraserSize);

        mBrushLayout.setVisibility(View.GONE);
        mEraserLayout.setVisibility(View.GONE);
        mShapeLayout.setVisibility(View.GONE);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mPaintFunction = new PaintFunction(mContextThis);
                initPaint();
                mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                mDrawLayout.addView(mPaintFunction);
            }
        };

        runOnUiThread(mRunnable);

        if (mOpenFileURL != null) {
            File imageSummary = new File(mOpenFileURL + Constant.FILE_IMAGE_SUMMARY);
            if (!imageSummary.exists()) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.paint_dialog_alert);
                dialog.setMessage(R.string.paint_dialog_isDescription);
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        if (_which == AlertDialog.BUTTON_POSITIVE) {
                            imageDescription();
                        }
                        _dialog.dismiss();
                    }
                };
                dialog.setPositiveButton(R.string.paint_dialog_yes, clickListener);
                dialog.setNegativeButton(R.string.paint_dialog_no, clickListener);
                dialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_paint, _menu);
        mMenuItemUndo = _menu.findItem(R.id.menu_paint_undo);
        mMenuItemUndo.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem _item) {
        int id = _item.getItemId();
        switch (id) {
            case R.id.menu_paint_reset:
                mPaintFunction.resetPaint();
                mShapeType = Constant.ShapeType.None;
                break;
            case R.id.menu_paint_undo:
                mPaintFunction.undoCanvas();
                mPaintFunction.invalidate();
                break;
            case R.id.menu_paint_etc:
                mPaintDrawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.menu_paint_info:
                imageDescription();
                break;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPaintFunction.destroyPaint();
        mPaintFunction = null;
        mDrawLayout = null;
        mBrushLayout = null;
        mEraserLayout = null;
        mShapeLayout = null;
        mBrushSeekSize = null;
        mBrushSeekRed = null;
        mBrushSeekGreen = null;
        mBrushSeekBlue = null;
        mEraserSeekSize = null;
        mEraserTxtSize = null;
        mBrushTxtSize = null;
        mBrushTxtRed = null;
        mBrushTxtGreen = null;
        mBrushTxtBlue = null;
        mContextThis = null;
        mMenuItemUndo = null;
        mOpenFolderURL = null;
        mOpenFileName = null;
        mOpenFileURL = null;
        mLastLog = null;
        mRunnable = null;
        mBrushColor = null;
        mShapeCircle = null;
        mShapeRectangle = null;
        System.gc();
    }

    @Override
    public void onBackPressed() {
        if (mPaintFunction.isModified()) {
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    switch (_which) {
                        case AlertDialog.BUTTON_POSITIVE: {
                            if (mOpenFileURL == null) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(PaintActivity.this);
                                alert.setTitle(R.string.memo_alert_save_context);
                                alert.setItems(R.array.main_selectalert, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface _dialog, int _which) {
                                        switch (_which) {
                                            case Constant.MEMO_SAVE_SELECT_TYPE_EXTERNAL:
                                                Intent intent = new Intent();
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.setClass(mContextThis, FileBrowserActivity.class);
                                                intent.setType("text/plain");
                                                intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BrowserType.SaveExternalOpenedFile.getValue());
                                                startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE);
                                                break;
                                            case Constant.MEMO_SAVE_SELECT_TYPE_INTERNAL:
                                                if (mPaintFunction.isFileopen()) {
                                                    mPaintFunction.savePaint(mOpenFolderURL);
                                                } else {
                                                    mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_IMAGE_EXTENSION);
                                                    File tmpFile = new File(mOpenFileURL);
                                                    while (tmpFile.exists()) {
                                                        mMemoIndex++;
                                                        mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_IMAGE_EXTENSION);
                                                        tmpFile = new File(mOpenFileURL);
                                                    }
                                                    mPaintFunction.savePaint(mOpenFileURL);
                                                    writeLog();
                                                }
                                                finish();
                                                break;
                                        }
                                        _dialog.dismiss();
                                    }
                                });
                                alert.show();
                            } else {
                                mPaintFunction.savePaint(mOpenFileURL);
                                finish();
                            }
                            break;
                        }
                        case AlertDialog.BUTTON_NEGATIVE: {
                            File imageSummary = new File(mOpenFileURL + Constant.FILE_IMAGE_SUMMARY);
                            if (imageSummary.exists()) {
                                if (imageSummary.delete()) {
                                    TestLog.Tag("PaintActivity").Logging(TestLog.LogType.DEBUG, "이미지 저장안함으로 요약제거");
                                } else {
                                    TestLog.Tag("PaintActivity").Logging(TestLog.LogType.ERROR, "요약제거 실패");
                                }
                            } else {
                                TestLog.Tag("PaintActivity").Logging(TestLog.LogType.DEBUG, "이미지 요약 존재 안함");
                            }
                            finish();
                            break;
                        }
                    }
                    _dialog.dismiss();
                }
            };
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.memo_alert_modified_title);
            alert.setMessage(R.string.memo_alert_modified_context);
            alert.setPositiveButton(R.string.memo_alert_modified_btnSave, clickListener);
            alert.setNegativeButton(R.string.memo_alert_modified_btnDiscard, clickListener);
            alert.show();
        } else {
            writeLog();
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    /**
     * 로그를 쓰는 메소드
     */
    private void writeLog() {
        try {
            if (!mPaintFunction.isFileopen()) {
                if (LogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath())) {
                    TestLog.Tag("PaintActivity(writeLog)").Logging(TestLog.LogType.DEBUG, "로그 저장 완료");
                } else {
                    TestLog.Tag("PaintActivity(writeLog)").Logging(TestLog.LogType.ERROR, "로그 저장 불가");
                }
            }
        } catch (Exception e) {
            TestLog.Tag("PaintActivity(writeLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
        }
    }

    /**
     * 초기화
     */
    private void initPaint() {
        if (mOpenFileURL == null) {
            mLastLog = new File(mOpenFolderURL + File.separator + Constant.FILE_LOG_COUNT);
            if (!mLastLog.exists()) {
                try {
                    if (mLastLog.createNewFile()) {
                        mMemoIndex = 1;
                        if (LogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath())) {
                            TestLog.Tag("PaintActivity(initPaint)").Logging(TestLog.LogType.DEBUG, "로그 저장 완료");
                        } else {
                            TestLog.Tag("PaintActivity(initPaint)").Logging(TestLog.LogType.DEBUG, "로그 저장 불가");
                        }
                    }
                } catch (IOException ioe) {
                    TestLog.Tag("PaintActivity(initPaint)").Logging(TestLog.LogType.ERROR, ioe.getMessage());
                }
            } else {
                try {
                    mMemoIndex = Integer.parseInt(LogManager.openLog(mLastLog.getPath()));
                } catch (Exception e) {
                    TestLog.Tag("PaintActivity(initPaint)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
            setTitle(R.string.memo_title_newFile);
            mPaintFunction.setBitmap((mOpenFileURL == null), mOpenFolderURL);
            mDrawLayout.setBackgroundColor(Color.WHITE);
        } else {
            mOpenFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
            mOpenFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
            setTitle(mOpenFileName);
            mPaintFunction.setBitmap((mOpenFileURL == null), mOpenFileURL);
        }
    }

    private void imageDescription() {
        if (mOpenFileURL == null) {
            mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_IMAGE_EXTENSION);
            File tmpFile = new File(mOpenFileURL);
            while (tmpFile.exists()) {
                mMemoIndex++;
                mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_IMAGE_EXTENSION);
                tmpFile = new File(mOpenFileURL);
            }
        }
        final File imageSummary = new File(mOpenFileURL + Constant.FILE_IMAGE_SUMMARY);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paint_dialog_description_title);
        View layout = LayoutInflater.from(mContextThis).inflate(R.layout.dialog_image_description, null);
        final EditText description = (EditText)layout.findViewById(R.id.dialog_image_description_input);
        TestLog.Tag("Description").Logging(TestLog.LogType.DEBUG, "imageSummary: " + imageSummary);
        if (mOpenFileURL != null) {
            if (imageSummary.exists()) {
                FileReader fr = null;
                BufferedReader br = null;
                String line;
                try {
                    fr = new FileReader(imageSummary);
                    br = new BufferedReader(fr);
                    if ((line = br.readLine()) != null) {
                        TestLog.Tag("Description").Logging(TestLog.LogType.DEBUG, "내용: " + line);
                        description.setText(line);
                    } else {
                        description.setText("");
                    }
                } catch (Exception e) {
                    TestLog.Tag("imageDescription").Logging(TestLog.LogType.ERROR, e.getMessage());
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e) {
                            TestLog.Tag("imageDescription").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                    if (fr != null) {
                        try {
                            fr.close();
                        } catch (Exception e) {
                            TestLog.Tag("imageDescription").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                }
            }
        }
        builder.setView(layout);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                if (_which == AlertDialog.BUTTON_POSITIVE) {
                    FileWriter fw = null;
                    BufferedWriter bw = null;
                    try {
                        fw = new FileWriter(imageSummary);
                        bw = new BufferedWriter(fw);
                        bw.write(description.getText().toString());
                    } catch (Exception e) {
                        TestLog.Tag("PaintActivity(imageDescription)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    } finally {
                        if (bw != null) {
                            try { bw.close(); }
                            catch (Exception e) { TestLog.Tag("PaintActivity(imageDescription)").Logging(TestLog.LogType.ERROR, e.getMessage()); }
                        }
                        if (fw != null) {
                            try { fw.close(); }
                            catch (Exception e) { TestLog.Tag("PaintActivity(imageDescription)").Logging(TestLog.LogType.ERROR, e.getMessage()); }
                        }
                    }
                }
                _dialog.dismiss();
            }
        };
        builder.setPositiveButton(R.string.memo_alert_modified_btnSave, clickListener);
        builder.setNegativeButton(R.string.memo_alert_modified_btnCancel, clickListener);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void onClick(View _v) {
        int idx = _v.getId();
        switch (idx) {
            case R.id.paint_brush_btnClose:
                mBrushLayout.setVisibility(View.GONE);
                break;
            case R.id.paint_eraser_btnClose:
                mEraserLayout.setVisibility(View.GONE);
                break;
            case R.id.paint_shapes_btnClose:
                mShapeLayout.setVisibility(View.GONE);
                break;
            case R.id.paint_brush:
                mPaintFunction.changePaint(Constant.PaintType.Brush);
                mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                mPaintFunction.setLineWidth(mCurBrushValue);
                if (mBrushLayout.getVisibility() == View.VISIBLE) {
                    mBrushLayout.setVisibility(View.GONE);
                } else {
                    if (mEraserLayout.getVisibility() == View.VISIBLE) {
                        mEraserLayout.setVisibility(View.GONE);
                    }
                    if (mShapeLayout.getVisibility() == View.VISIBLE) {
                        mShapeLayout.setVisibility(View.GONE);
                    }
                    mBrushLayout.setVisibility(View.VISIBLE);
                }
                mShapeType = Constant.ShapeType.None;
                mPaintDrawerLayout.closeDrawer(Gravity.END);
                break;
            case R.id.paint_eraser:
                mPaintFunction.changePaint(Constant.PaintType.Eraser);
                mPaintFunction.setLineWidth(mCurEraserSize);
                if (mEraserLayout.getVisibility() == View.VISIBLE) {
                    mEraserLayout.setVisibility(View.GONE);
                } else {
                    if (mBrushLayout.getVisibility() == View.VISIBLE) {
                        mBrushLayout.setVisibility(View.GONE);
                    }
                    if (mShapeLayout.getVisibility() == View.VISIBLE) {
                        mShapeLayout.setVisibility(View.GONE);
                    }
                    mEraserLayout.setVisibility(View.VISIBLE);
                }
                mShapeType = Constant.ShapeType.None;
                mPaintDrawerLayout.closeDrawer(Gravity.END);
                break;
            case R.id.paint_shapes:
                mPaintFunction.changePaint(Constant.PaintType.Shape);
                mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                mPaintFunction.setLineWidth(mCurBrushValue);
                if (mShapeLayout.getVisibility() == View.VISIBLE) {
                    mShapeLayout.setVisibility(View.GONE);
                } else {
                    if (mEraserLayout.getVisibility() == View.VISIBLE) {
                        mEraserLayout.setVisibility(View.GONE);
                    }
                    if (mBrushLayout.getVisibility() == View.VISIBLE) {
                        mBrushLayout.setVisibility(View.GONE);
                    }
                    mShapeLayout.setVisibility(View.VISIBLE);
                }
                mPaintDrawerLayout.closeDrawer(Gravity.END);
                break;
        }
    }

    /**
     * 그림을 그리는 뷰용 Class
     */
    static class PaintFunction extends View {
        private float mCurX;
        private float mCurY;
        private float mPrevX;
        private float mPrevY;
        private float mRadius = 0.0f;

        private Paint mCanvasPaint;
        private Paint mBrushPaint;
        private Canvas mCanvas;
        private Path mPath;
        private Bitmap mBitmap;
        private BrushObject mBrushObject;

        private int mScreenWidth;
        private int mScreenHeight;

        private int mCurColor;
        private float mCurSize;

        private boolean mFileopen;
        private String mFilename;
        private boolean mIsModified;

        public PaintFunction(final Context _context) {
            super(_context);
            DisplayMetrics displayMetrics = _context.getApplicationContext().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        public PaintFunction(final Context _context, final AttributeSet _attributeSet) {
            super(_context, _attributeSet);
            DisplayMetrics displayMetrics = _context.getApplicationContext().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        public void destroyPaint() {
            mCanvasPaint = null;
            mBrushPaint = null;
            mCanvas = null;
            mPath = null;
            if (mBitmap != null)
                mBitmap.recycle();
            mBitmap = null;
            mBrushObject = null;
        }

        /**
         * 초기화
         */
        private void init() {
            if (mFileopen) {
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mBitmap = BitmapFactory.decodeFile(mFilename).copy(Bitmap.Config.ARGB_8888, true);
            } else {
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
            }

            if (mCanvas != null) {
                mCanvas = null;
            }
            mCanvas = new Canvas(mBitmap);
            if (!mFileopen) {
                mCanvas.drawARGB(Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX);
            }

            if (mBrushObject != null) {
                mBrushObject.init();
            } else {
                mBrushObject = new BrushObject();
            }

            if (mPath != null) {
                mPath = null;
            }
            mPath = new Path();

            if (mCanvasPaint != null) {
                mCanvasPaint.reset();
                mCanvasPaint = null;
            }
            mCanvasPaint = new Paint(Paint.DITHER_FLAG);
            mCanvasPaint.setAntiAlias(true);

            if (mBrushPaint != null) {
                mBrushPaint.reset();
                mBrushPaint = null;
            }
            mBrushPaint = new Paint();
            mBrushPaint.setAlpha(Constant.PAINT_COLOR_MAX);
            mBrushPaint.setDither(true);
            mBrushPaint.setStrokeWidth(Constant.PAINT_DEFAULT_WIDTH_PIXEL);
            mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
            mBrushPaint.setStyle(Paint.Style.STROKE);
            mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
            mBrushPaint.setAntiAlias(true);

            mRadius = 0.0f;
            setLineWidth(Constant.PAINT_DEFAULT_WIDTH_PIXEL);
        }

        @Override
        protected void onDraw(final Canvas _canvas) {
            if (mBitmap != null) {
                _canvas.drawBitmap(mBitmap, 0, 0, mCanvasPaint);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mBitmap != null)
                mBitmap.recycle();
            mBitmap = null;
            mPath = null;
            mCanvas = null;
            mCanvasPaint = null;
            mBrushPaint = null;
            mBrushObject.init();
            mFilename = null;
        }

        @Override
        public boolean onTouchEvent(final MotionEvent _event) {
            mCurX = _event.getX();
            mCurY = _event.getY();

            int action = _event.getAction();

            switch (mShapeType) {
                case None: {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN: {
                            mPath.reset();
                            mPath.moveTo(mCurX, mCurY);
                            mPrevX = mCurX;
                            mPrevY = mCurY;
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (Math.abs(mCurX - mPrevX) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL || Math.abs(mCurY - mPrevY) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL) {
                                mPath.quadTo(mPrevX, mPrevY, mCurX, mCurY);
                                mPrevX = mCurX;
                                mPrevY = mCurY;
                            }
                            mCanvas.drawPath(mPath, mBrushPaint);
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            mBrushObject.mBrushType.add(Constant.ShapeType.None);
                            mBrushObject.mBrushPaths.add(new Path(mPath));
                            mBrushObject.mBrushSizes.add(mBrushPaint.getStrokeWidth());
                            mBrushObject.mBrushColor.add(mCurColor);
                            mMenuItemUndo.setVisible(true);
                            drawLine();
                            invalidate();
                            performClick();
                            break;
                        }
                    }
                    break;
                }
                case Circle: {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN: {
                            mPrevX = mCurX;
                            mPrevY = mCurY;
                            mRadius = 1f;
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            mRadius = (float)Math.sqrt(Math.pow(mCurX - mPrevX, 2) + Math.pow(mCurY - mPrevY, 2)) / 2;
                            mCanvas.drawCircle(mPrevX + (mCurX - mPrevX) / 2, mPrevY + (mCurY - mPrevY) / 2, mRadius, mBrushPaint);
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            mBrushObject.mBrushType.add(Constant.ShapeType.Circle);
                            mBrushObject.mBrushPaths.add(new CircleObject(mPrevX + (mCurX - mPrevX) / 2, mPrevY + (mCurY - mPrevY) / 2, mRadius));
                            mBrushObject.mBrushSizes.add(mBrushPaint.getStrokeWidth());
                            mBrushObject.mBrushColor.add(mCurColor);
                            mMenuItemUndo.setVisible(true);
                            drawLine();
                            invalidate();
                            performClick();
                            break;
                        }
                    }
                    break;
                }
                case Rectangle: {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN: {
                            mPrevX = mCurX;
                            mPrevY = mCurY;
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            mCanvas.drawRect(new Rect((int)mPrevX, (int)mPrevY, (int)mCurX, (int)mCurY), mBrushPaint);
                            invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            mBrushObject.mBrushType.add(Constant.ShapeType.Rectangle);
                            mBrushObject.mBrushPaths.add(new Rect((int)mPrevX, (int)mPrevY, (int)mCurX, (int)mCurY));
                            mBrushObject.mBrushSizes.add(mBrushPaint.getStrokeWidth());
                            mBrushObject.mBrushColor.add(mCurColor);
                            mMenuItemUndo.setVisible(true);
                            drawLine();
                            invalidate();
                            performClick();
                            break;
                        }
                    }
                    break;
                }
            }
            mIsModified = true;
            return false;
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        /**
         * 페인트 색상 설정
         * @param _color 색
         */
        public void setColor(final int _color) {
            mCurColor = _color;
            mBrushPaint.setColor(mCurColor);
            invalidate();
        }

        /**
         * 페인트 굵기 설정
         * @param _lineWidth 굵기
         */
        public void setLineWidth(final float _lineWidth) {
            mCurSize = _lineWidth;
            mBrushPaint.setStrokeWidth(mCurSize);
            invalidate();
        }

        /**
         * 초기화 수행 명령
         */
        public void resetPaint() {
            init();
            setColor(mCurColor);
            invalidate();
        }

        /**
         * 파일이 열려있는가(이미 생성된 이미지를 열었는가) 여부 반환
         * @return 이미생성 혹은 아님
         */
        public boolean isFileopen() {
            return mFileopen;
        }

        /**
         * 비트맵을 설정한다.
         * @param _memoType 이미 생성되어진 파일인가
         * @param _folderUrl 폴더 경로
         */
        public void setBitmap(final boolean _memoType, final String _folderUrl) {
            if (_memoType) {
                mFileopen = false;
            } else {
                this.mFilename = _folderUrl;
                mFileopen = true;
            }
            init();
        }

        /**
         * 만든 이미지 메모를 저장
         * @param _dir 저장 경로
         */
        public void savePaint(final String _dir) {
            FileOutputStream fos = null;
            this.draw(mCanvas);
            try {
                fos = new FileOutputStream(new File(_dir));
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                TestLog.Tag("PaintFunction(save-)").Logging(TestLog.LogType.ERROR, e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 메모가 변경되었는가 여부 확인
         * @return 변경 혹은 미변경
         */
        public boolean isModified() {
            return mIsModified;
        }

        /**
         * 되돌리기 기능
         */
        public void undoCanvas() {
            /*if (mBrushObject.mBrushPathsIdx != 0) {
                mBrushObject.mBrushType.remove(--mBrushObject.mBrushPathsIdx);
                mBrushObject.mBrushPaths.remove(mBrushObject.mBrushPathsIdx);
                mBrushObject.mBrushSizes.remove(mBrushObject.mBrushPathsIdx);
                mBrushObject.mBrushColor.remove(mBrushObject.mBrushPathsIdx);
                mPath.reset();
                drawLine();
            }

            if (mBrushObject.mBrushPathsIdx == 0) {
                mMenuItemUndo.setVisible(false);
            }*/
            if (!mBrushObject.mBrushPaths.isEmpty()) {
                mBrushObject.mBrushType.removeLast();
                mBrushObject.mBrushPaths.removeLast();
                mBrushObject.mBrushSizes.removeLast();
                mBrushObject.mBrushColor.removeLast();
                mPath.reset();
                drawLine();
            }

            if (mBrushObject.mBrushPaths.isEmpty()) {
                mMenuItemUndo.setVisible(false);
            }
            invalidate();
        }

        /**
         * 브러쉬, 지우개 변경
         * @param _type 지우개, 도형, 브러쉬
         */
        public void changePaint(final Constant.PaintType _type) {
            switch (_type) {
                case Brush: case Shape:
                    mBrushPaint.setColor(mCurColor);
                    break;
                case Eraser:
                    mCurColor = Color.WHITE;
                    mBrushPaint.setColor(mCurColor);
                    break;
            }
            invalidate();
        }

        /**
         * 선을 그린다.(지우개 포함)
         */
        private void drawLine() {
            if (mFileopen) {
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mBitmap = BitmapFactory.decodeFile(mFilename).copy(Bitmap.Config.ARGB_8888, true);
            } else {
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
            }

            if (mCanvas != null) {
                mCanvas = null;
            }
            mCanvas = new Canvas(mBitmap);
            if (!mFileopen) {
                mCanvas.drawARGB(Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX, Constant.PAINT_COLOR_MAX);
            }

            Iterator<Float> iterBrushSize = mBrushObject.mBrushSizes.iterator();
            Iterator<Object> iterBrushPath = mBrushObject.mBrushPaths.iterator();
            Iterator<Integer> iterBrushColor = mBrushObject.mBrushColor.iterator();
            Iterator<Constant.ShapeType> iterBrushType = mBrushObject.mBrushType.iterator();
            while (iterBrushSize.hasNext()) {
                mBrushPaint.setStrokeWidth(iterBrushSize.next());
                mBrushPaint.setColor(iterBrushColor.next());
                switch (iterBrushType.next()) {
                    case None: {
                        mCanvas.drawPath((Path)iterBrushPath.next(), mBrushPaint);
                        break;
                    }
                    case Circle: {
                        CircleObject circleObject = (CircleObject)iterBrushPath.next();
                        mCanvas.drawCircle(circleObject.mX, circleObject.mY, circleObject.mRadius, mBrushPaint);
                        break;
                    }
                    case Rectangle: {
                        Rect rect = (Rect)iterBrushPath.next();
                        mCanvas.drawRect(rect, mBrushPaint);
                        break;
                    }
                }
            }
            mBrushPaint.setStrokeWidth(mCurSize);
            mBrushPaint.setColor(mCurColor);
        }
    }
}
