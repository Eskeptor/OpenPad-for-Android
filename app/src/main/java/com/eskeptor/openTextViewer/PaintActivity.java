package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.eskeptor.openTextViewer.datatype.BrushObject;
import com.eskeptor.openTextViewer.textManager.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PaintActivity extends AppCompatActivity {
    private PaintFunction mPaintFunction;
    private LinearLayout mDrawLayout;
    private LinearLayout mBrushLayout;
    private LinearLayout mEraserLayout;
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
    private LogManager mLogManager;
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

        mLogManager = new LogManager();

        mCurBrushValue = (int) Constant.PAINT_DEFAULT_WIDTH_PIXEL;
        mCurEraserSize = (int) Constant.PAINT_ERASER_WIDTH_PIXEL;
        mCurRedValue = 0;
        mCurGreenValue = 0;
        mCurBlueValue = 0;

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
            case R.id.menu_paint_pen:
                mPaintFunction.changePaint(Constant.PaintType.Brush);
                mPaintFunction.setColor(Color.rgb(mCurRedValue, mCurGreenValue, mCurBlueValue));
                mPaintFunction.setLineWidth(mCurBrushValue);
                if (mBrushLayout.getVisibility() == View.VISIBLE) {
                    mBrushLayout.setVisibility(View.GONE);
                } else {
                    if (mEraserLayout.getVisibility() == View.VISIBLE) {
                        mEraserLayout.setVisibility(View.GONE);
                    }
                    mBrushLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.menu_paint_eraser:
                mPaintFunction.changePaint(Constant.PaintType.Eraser);
                mPaintFunction.setLineWidth(mCurEraserSize);
                if (mEraserLayout.getVisibility() == View.VISIBLE) {
                    mEraserLayout.setVisibility(View.GONE);
                } else {
                    if (mBrushLayout.getVisibility() == View.VISIBLE) {
                        mBrushLayout.setVisibility(View.GONE);
                    }
                    mEraserLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.menu_paint_reset:
                mPaintFunction.resetPaint();
                break;
            case R.id.menu_paint_undo:
                mPaintFunction.undoCanvas();
                mPaintFunction.invalidate();
                break;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPaintFunction = null;
        mDrawLayout = null;
        mBrushLayout = null;
        mEraserLayout = null;
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
        mLogManager = null;
        mMenuItemUndo = null;
        mOpenFolderURL = null;
        mOpenFileName = null;
        mOpenFileURL = null;
        mLastLog = null;
        mRunnable = null;
        mBrushColor = null;
        System.gc();
    }

    @Override
    public void onBackPressed() {
        if (mPaintFunction.isModified()) {
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    switch (_which) {
                        case AlertDialog.BUTTON_POSITIVE:
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
                                                intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE);
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
                        case AlertDialog.BUTTON_NEGATIVE:
                            finish();
                            break;
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

    private void writeLog() {
        try {
            if (!mPaintFunction.isFileopen()) {
                mLogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath());
            }
        } catch (Exception e) {
            Log.e("PaintActivity(writeLog)", e.getMessage());
        }
    }

    private void initPaint() {
        if (mOpenFileURL == null) {
            mLastLog = new File(mOpenFolderURL + File.separator + Constant.FILE_LOG_COUNT);
            if (!mLastLog.exists()) {
                try {
                    if (mLastLog.createNewFile()) {
                        mMemoIndex = 1;
                        mLogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath());
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                try {
                    mMemoIndex = Integer.parseInt(mLogManager.openLog(mLastLog.getPath()));
                } catch (Exception e) {
                    Log.e("PaintActivity(init-)", e.getMessage());
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


    static class PaintFunction extends View {
        private float mCurX;
        private float mCurY;
        private float mPrevX;
        private float mPrevY;

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

        private void reset() {
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

            if ((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                mPath.reset();
                mPath.moveTo(mCurX, mCurY);
                mPrevX = mCurX;
                mPrevY = mCurY;
                invalidate();
                return true;
            } else if ((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                if (Math.abs(mCurX - mPrevX) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL || Math.abs(mCurY - mPrevY) >= Constant.PAINT_MINIMUM_LINE_LENGTH_PIXEL) {
                    mPath.quadTo(mPrevX, mPrevY, mCurX, mCurY);
                    mPrevX = mCurX;
                    mPrevY = mCurY;
                }
                mCanvas.drawPath(mPath, mBrushPaint);
                invalidate();
                return true;
            } else if ((_event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                mBrushObject.mBrushPaths.add(new Path(mPath));
                mBrushObject.mBrushSizes.add(mBrushPaint.getStrokeWidth());
                mBrushObject.mBrushColor.add(mCurColor);
                mBrushObject.mBrushPathsIdx++;
                mMenuItemUndo.setVisible(true);
                invalidate();
            }

            mIsModified = true;
            return false;
        }

        public void setColor(final int _color) {
            mCurColor = _color;
            mBrushPaint.setColor(mCurColor);
            invalidate();
        }

        public void setLineWidth(final float _lineWidth) {
            mCurSize = _lineWidth;
            mBrushPaint.setStrokeWidth(mCurSize);
            invalidate();
        }

        public void resetPaint() {
            reset();
            setColor(mCurColor);
            invalidate();
        }

        public boolean isFileopen() {
            return mFileopen;
        }

        public void setBitmap(final boolean _memoType, final String _folderUrl) {
            if (_memoType) {
                mFileopen = false;
            } else {
                this.mFilename = _folderUrl;
                mFileopen = true;
            }
            reset();
        }

        public void savePaint(final String _dir) {
            FileOutputStream fos = null;
            this.draw(mCanvas);
            try {
                fos = new FileOutputStream(new File(_dir));
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                Log.e("PaintFunction(save-)", e.getMessage());
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

        public boolean isModified() {
            return mIsModified;
        }

        public void undoCanvas() {
            if (mBrushObject.mBrushPathsIdx != 0) {
                mBrushObject.mBrushPaths.remove(--mBrushObject.mBrushPathsIdx);
                mBrushObject.mBrushSizes.remove(mBrushObject.mBrushPathsIdx);
                mBrushObject.mBrushColor.remove(mBrushObject.mBrushPathsIdx);
                mPath.reset();
                drawLine();
            }

            if (mBrushObject.mBrushPathsIdx == 0) {
                mMenuItemUndo.setVisible(false);
            }
            invalidate();
        }

        public void changePaint(final Constant.PaintType _type) {
            switch (_type) {
                case Brush:
                    mBrushPaint.setColor(mCurColor);
                    break;
                case Eraser:
                    mCurColor = Color.WHITE;
                    mBrushPaint.setColor(mCurColor);
                    break;
            }
            invalidate();
        }

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

            int idx = mBrushObject.mBrushPathsIdx;
            for (int i = 0; i < idx; i++) {
                mBrushPaint.setStrokeWidth(mBrushObject.mBrushSizes.get(i));
                mBrushPaint.setColor(mBrushObject.mBrushColor.get(i));
                mCanvas.drawPath(mBrushObject.mBrushPaths.get(i), mBrushPaint);
            }
            mBrushPaint.setStrokeWidth(mCurSize);
            mBrushPaint.setColor(mCurColor);
        }
    }
}
