package com.eskeptor.openTextViewer;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.eskeptor.openTextViewer.textManager.LogManager;
import com.eskeptor.openTextViewer.textManager.TextManager;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import layout.MemoWidget;
import util.TestLog;

/**
 * Created by eskeptor on 17. 2. 1.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MemoActivity extends AppCompatActivity {
    public static final int MEMO_SAVE_SELECT_TYPE_EXTERNAL = 0;
    public static final int MEMO_SAVE_SELECT_TYPE_INTERNAL = 1;
    public enum MemoSaveType {
        BackKeySave(0), ButtonSave(1);

        private final int value;
        MemoSaveType(final int _value) {
            value = _value;
        }
        public int getValue() {
            return value;
        }
    }

    // Variables associated with files from MainActivity
    private String mOpenFileURL;
    private String mOpenFileName;
    private String mOpenFolderURL;
    private int mMemoIndex;                     // Number to paste on the newly created memo
    private TextManager.EncodeType mEncodeType;
    private boolean mIsDivided;

    // Widget related
    private boolean mIsWidget;
    private int mWidgetID;

    private EditText mEditText;
    private TextManager mTxtManager;            // Text Manager
    private File mLastLog;                      // Log files (numbers to paste on when creating a new note)
    private ScrollView mScrollView;
    private Context mContextThis;
    private boolean mIsModifiying;

    // Lower button
    private ScrollView mBtnLayout;
    private Button mBtnPrev;
    private Button mBtnNext;
    private ProgressBar mProgCurrent;
    private TextView mTxtProgCur;

    // Multi-touch gesture
    private GestureDetectorCompat mGestureDetectorCompat;

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    // Menu items
    private MenuItem mEditMenu;
    private Drawable mDrawableModified;
    private Drawable mDrawableModifiedComplete;

    // IntMpuethodManager for auto focus off
    private InputMethodManager mInputMethodManager;

    // File Handler(for TextManager)
    private FileIOHandler mHandler;
    private Thread mTextThread;
    private static final int HANDLER_FILE_OPENED = 1;
    private static final int HANDLER_PREV_PAGE = 2;
    private static final int HANDLER_NEXT_PAGE = 3;
    private static final int HANDLER_FIRST_PAGE = 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mOpenFileURL != null || mIsWidget) {
            getMenuInflater().inflate(R.menu.menu_memo, menu);
            mEditMenu = menu.findItem(R.id.menu_memo_modified);
            mEditMenu.setIcon(mDrawableModified);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_memo_modified:
                if (mIsModifiying) {
                    mEditText.setFocusable(false);
                    mEditMenu.setIcon(mDrawableModified);
                    mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    if (isModified()) {
                        saveMemo(MemoSaveType.ButtonSave);
                    }
                    mIsModifiying = false;
                } else {
                    mEditText.setFocusable(true);
                    mEditText.setFocusableInTouchMode(true);
                    mEditText.requestFocus();
                    mEditMenu.setIcon(mDrawableModifiedComplete);
                    mInputMethodManager.showSoftInput(mEditText, 0);
                    mIsModifiying = true;
                }
                return true;
            case R.id.menu_memo_charsetchange:
                AlertDialog.Builder alert = new AlertDialog.Builder(MemoActivity.this);
                alert.setTitle(R.string.menu_memo_charsetChange);
                alert.setItems(R.array.menu_memo_charset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String txtData = "Error";
                        if (which == 0)
                            mEncodeType = TextManager.EncodeType.EucKR;
                        else
                            mEncodeType = TextManager.EncodeType.UTF8;
                        if (mTxtManager.openText(mOpenFileURL)) {
                            txtData = mTxtManager.getText(TextManager.PAGE_NONE, mEncodeType);
                        }
                        mEditText.setText(txtData);
                        dialog.dismiss();
                    }
                });
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE: {
                    String folderURL = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                    String fileName = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                    if (mTxtManager.saveText(folderURL + fileName, mEditText.getText().toString())) {
                        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, String.format(getResources().getString(R.string.memo_toast_saveSuccess_external), fileName));
                    } else {
                        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, String.format(getResources().getString(R.string.memo_toast_saveFail_external), fileName));
                    }
                    break;
                }
                case Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE: {
                    String folderURL = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                    String fileName = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                    if (mTxtManager.saveText(folderURL + fileName, mEditText.getText().toString())) {
                        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, String.format(getResources().getString(R.string.memo_toast_saveSuccess_external), fileName));
                    } else {
                        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, String.format(getResources().getString(R.string.memo_toast_saveFail_external), fileName));
                    }
                    break;
                }
            }
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "onCreate");

        mContextThis = getApplicationContext();
        mSharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIsModifiying = false;
        mEditText = findViewById(R.id.memo_etxtMain);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSharedPref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        mTxtManager = new TextManager();
        mTxtManager.setLines(mSharedPref.getInt(Constant.APP_TEXT_LINES, Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
        mScrollView = findViewById(R.id.memo_scroll);
        mBtnLayout = findViewById(R.id.memo_layoutButton);
        mEncodeType = TextManager.EncodeType.UTF8;

        // Create a new file : If mOpenFileURL is null
        // Open previous file : If mOpenFileURL is not null
        mOpenFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        mOpenFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
        mOpenFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
        mIsWidget = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, false);
        mWidgetID = getIntent().getIntExtra(Constant.INTENT_EXTRA_WIDGET_ID, 999);
        mSharedPref = getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + mWidgetID, MODE_PRIVATE);
        mWidgetID = mSharedPref.getInt(MemoWidget.WIDGET_ID, 0);
        int fontStyle = mSharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());

        if (mOpenFileURL == null) {
            mLastLog = new File(mOpenFolderURL + File.separator + Constant.FILE_LOG_COUNT);
            if (!mLastLog.exists()) {
                try {
                    if(mLastLog.createNewFile()) {
                        mMemoIndex = 1;
                        if (LogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath())) {
                            TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "로그 저장 완료");
                        } else {
                            TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "로그 저장불가 완료");
                        }
                    }
                } catch (IOException ioe) {
                    TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, ioe.getMessage());
                }

            } else {
                try {
                    mMemoIndex = Integer.parseInt(LogManager.openLog(mLastLog.getPath()));
                } catch (Exception e) {
                    TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }

            mTxtManager.initManager();
            mBtnLayout.setVisibility(View.GONE);
            if (mIsWidget) {
                final File tmpFile = new File(mOpenFolderURL + File.separator + mWidgetID + Constant.FILE_TEXT_EXTENSION);
                if (tmpFile.exists()) {
                    setTitle(R.string.memo_title_widget);
                    mOpenFileURL = tmpFile.getPath();
                    if (mTxtManager.openText(mOpenFileURL)) {
                        String txtData = mTxtManager.getText(TextManager.PAGE_NONE, mEncodeType);
                        mEditText.setText(txtData);
                        mEditText.setFocusable(false);
                    }
                    mBtnLayout.setVisibility(View.GONE);
                } else {
                    setTitle(getString(R.string.memo_title_newFile) + "(" + getString(R.string.memo_title_newWidgetMemo) + ")");
                    mEditText.setText("");
                    mEditText.setFocusable(true);
                }

                if (Build.VERSION.SDK_INT >= 21) {
                    mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp, null);
                    mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp, null);
                } else {
                    mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp);
                    mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp);
                }
            } else {
                setTitle(R.string.memo_title_newFile);
                mEditText.setText("");
                mEditText.setFocusable(true);
            }
        } else {
            setTitle(mOpenFileName);
            mProgCurrent = findViewById(R.id.memo_Prog);
            mTxtProgCur = findViewById(R.id.memo_txtProg_cur);

            mHandler = new FileIOHandler(this);

            mTextThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mTxtManager.openText(mOpenFileURL)) {
                        TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "파일 열림");
                        mHandler.sendEmptyMessage(HANDLER_FILE_OPENED);
                    }
                }
            });
            mTextThread.start();

            mIsDivided = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_DIVIDE, false);

            if (mIsDivided) {
                mBtnLayout.setVisibility(View.VISIBLE);
                mBtnPrev = findViewById(R.id.memo_btnPrev);
                mBtnNext = findViewById(R.id.memo_btnNext);

                mScrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getPointerCount() > 1) {
                            TestLog.Tag("Test").Logging(TestLog.LogType.DEBUG, "double touch");
                            return mGestureDetectorCompat.onTouchEvent(event);
                        }
                        return false;
                    }
                });

                mGestureDetectorCompat = new GestureDetectorCompat(mContextThis, new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        if (distanceY > 0)
                            mBtnLayout.scrollTo((int) mBtnLayout.getX(), mBtnLayout.getBottom());
                        else
                            mBtnLayout.scrollTo((int) mBtnLayout.getX(), 0);
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        return true;
                    }

                });

                mBtnPrev.setEnabled(false);
                if (mTxtManager.getCurPage() == mTxtManager.getMaxPage() - 1) {
                    mBtnNext.setEnabled(false);
                }
            } else {
                mBtnLayout.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp, null);
                mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp, null);
            } else {
                mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp);
                mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp);
            }

            if (fontStyle == Constant.FontType.BaeDal_JUA.getValue()) {
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"));
            } else if (fontStyle == Constant.FontType.KOPUB_Dotum.getValue()) {
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"));
            } else {
                Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeLog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditText = null;
        mTxtManager.initManager();
        mTxtManager = null;
        mBtnLayout = null;
        mBtnPrev = null;
        mBtnNext = null;
        mScrollView = null;
        mContextThis = null;
        mSharedPref = null;
        mSharedPrefEditor = null;
        mEditMenu = null;
        mDrawableModified = null;
        mDrawableModifiedComplete = null;
        mInputMethodManager = null;
        mProgCurrent = null;
        mTxtProgCur = null;
        mGestureDetectorCompat = null;
        mLastLog = null;
        mOpenFileURL = null;
        mOpenFileName = null;
        mOpenFolderURL = null;
        mHandler = null;
        if (mTextThread != null) {
            mTextThread.interrupt();
        }
        mTextThread = null;
    }

    @Override
    public void onBackPressed() {
        if (isModified()) {
            if (mIsWidget) {
                int origin_id = mWidgetID;
                if (mOpenFileURL == null) {
                    mOpenFileURL = mOpenFolderURL + File.separator + (mWidgetID + Constant.FILE_TEXT_EXTENSION);
                    File tmpFile = new File(mOpenFileURL);
                    while (tmpFile.exists()) {
                        mWidgetID++;
                        mOpenFileURL = mOpenFolderURL + File.separator + (mWidgetID + Constant.FILE_TEXT_EXTENSION);
                        tmpFile = new File(mOpenFileURL);
                    }
                }
                mOpenFileURL = mOpenFolderURL + File.separator + (mWidgetID + Constant.FILE_TEXT_EXTENSION);
                if (mTxtManager.saveText(mOpenFileURL, mEditText.getText().toString())) {
                    TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "위젯내용 저장");
                } else {
                    TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "위젯내용 저장불가");
                }
                writeLog();

                mSharedPrefEditor = mContextThis.getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + origin_id, MODE_PRIVATE).edit();
                mSharedPrefEditor.putString(MemoWidget.WIDGET_FILE_URL, mOpenFileURL);
                mSharedPrefEditor.apply();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContextThis);
                MemoWidget.updateAppWidget(mContextThis, appWidgetManager, origin_id);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, origin_id);
                setResult(RESULT_OK, resultValue);
                finish();
            } else {
                saveMemo(MemoSaveType.BackKeySave);
            }
        } else {
            writeLog();
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private void saveMemo(final MemoSaveType _type) {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    if (mOpenFileURL == null) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MemoActivity.this);
                        alert.setTitle(R.string.memo_alert_save_context);
                        alert.setItems(R.array.main_selectalert, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case MEMO_SAVE_SELECT_TYPE_EXTERNAL:
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setClass(mContextThis, FileBrowserActivity.class);
                                        intent.setType("text/plain");
                                        intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, FileBrowserActivity.BrowserType.SaveExternalOpenedFile.getValue());
                                        startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE);
                                        break;
                                    case MEMO_SAVE_SELECT_TYPE_INTERNAL:
                                        if (mTxtManager.isFileOpened()) {
                                            if (mTxtManager.saveText(mOpenFileURL, mEditText.getText().toString())) {
                                                TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "내용 저장 완료(Internal)");
                                            } else {
                                                TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "내용 저장 불가(Internal)");
                                            }
                                        } else {
                                            mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_TEXT_EXTENSION);
                                            File tmpFile = new File(mOpenFileURL);
                                            while (tmpFile.exists()) {
                                                mMemoIndex++;
                                                mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_TEXT_EXTENSION);
                                                tmpFile = new File(mOpenFileURL);
                                            }
                                            if (mTxtManager.saveText(mOpenFileURL, mEditText.getText().toString())) {
                                                TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "내용 저장 완료(Internal)");
                                            } else {
                                                TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "내용 저장 불가(Internal)");
                                            }
                                            writeLog();
                                        }
                                        if (_type == MemoSaveType.BackKeySave) {
                                            finish();
                                        } else {
                                            mTxtManager.setMD5(mEditText.getText().toString(), mEncodeType);
                                        }
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    } else {
                        if (mTxtManager.saveText(mOpenFileURL, mEditText.getText().toString())) {
                            TestLog.Tag("MemoActivity").Logging(TestLog.LogType.DEBUG, "내용 저장 완료(Internal)");
                        } else {
                            TestLog.Tag("MemoActivity").Logging(TestLog.LogType.ERROR, "내용 저장 불가(Internal)");
                        }
                        if (_type == MemoSaveType.BackKeySave) {
                            finish();
                        } else {
                            mTxtManager.setMD5(mEditText.getText().toString(), mEncodeType);
                        }
                    }
                } else if (which == AlertDialog.BUTTON_NEGATIVE) {
                    if (_type == MemoSaveType.BackKeySave) {
                        finish();
                    } else {
                        int y = mScrollView.getScrollY();
                        mHandler.sendEmptyMessage(HANDLER_FILE_OPENED);
                        mScrollView.scrollTo(0, y);
                    }
                }
                dialog.dismiss();
            }
        };
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.memo_alert_modified_title);
        alert.setMessage(R.string.memo_alert_modified_context);
        alert.setPositiveButton(R.string.memo_alert_modified_btnSave, clickListener);
        alert.setNegativeButton(R.string.memo_alert_modified_btnDiscard, clickListener);
        alert.show();
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.memo_btnPrev:
                mHandler.sendEmptyMessage(HANDLER_PREV_PAGE);
                mScrollView.scrollTo(0, 0);
                break;
            case R.id.memo_btnFirst:
                mHandler.sendEmptyMessage(HANDLER_FIRST_PAGE);
                mScrollView.scrollTo(0, 0);
                break;
            case R.id.memo_btnTop:
                mScrollView.smoothScrollTo(0, 0);
                break;
            case R.id.memo_btnNext:
                mHandler.sendEmptyMessage(HANDLER_NEXT_PAGE);
                mScrollView.scrollTo(0, 0);
                break;
        }
    }

    /**
     * Determine if note content has changed
     * @return True or False
     */
    private boolean isModified() {
        if (mOpenFileURL != null) {
            String md5 = mTxtManager.createMD5(mEditText.getText().toString());
            return !mTxtManager.getMD5().equals(md5);
        } else {
            return !mEditText.getText().toString().equals("");
        }
    }

    /**
     * How to Write Logs
     */
    private void writeLog() {
        try {
            if (!mTxtManager.isFileOpened()) {
                if (LogManager.saveLog(Integer.toString(mIsWidget ? mWidgetID : mMemoIndex), mLastLog.getPath())) {
                    TestLog.Tag("MemoActivity(writeLog)").Logging(TestLog.LogType.DEBUG, "로그 저장 완료");
                } else {
                    TestLog.Tag("MemoActivity(writeLog)").Logging(TestLog.LogType.ERROR, "로그 저장 불가");
                }
            }
        } catch (Exception e) {
            TestLog.Tag("MemoActivity(writeLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
        }
    }

    /**
     * Create Previous and Subsequent Buttons
     */
    private void buttonEnabler() {
        if (mTxtManager.getCurPage() <= 0) {
            mBtnPrev.setEnabled(false);
        } else {
            mBtnPrev.setEnabled(true);
        }

        if (mTxtManager.getCurPage() >= mTxtManager.getMaxPage() - 1) {
            mBtnNext.setEnabled(false);
        } else {
            mBtnNext.setEnabled(true);
        }
    }

    /**
     * Gets content from TextManager
     * @param page Page
     */
    private void getTextFromTextManager(final int page) {
        String txtData = mTxtManager.getText(page, mEncodeType);
        mEditText.setText(txtData);
        mEditText.setFocusable(false);
        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        switch (page) {
            case TextManager.PAGE_NONE: {
                if (mIsDivided) {
                    buttonEnabler();
                    mProgCurrent.setProgress((int)mTxtManager.getProgress());
                    mTxtProgCur.setText(String.format(Locale.getDefault() ,"%.2f", mTxtManager.getProgress()));
                }
                break;
            }
            default: {
                buttonEnabler();
                mProgCurrent.setProgress((int)mTxtManager.getProgress());
                mTxtProgCur.setText(String.format(Locale.getDefault() ,"%.2f", mTxtManager.getProgress()));
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_FILE_OPENED: {
                getTextFromTextManager(TextManager.PAGE_NONE);
                break;
            }
            case HANDLER_PREV_PAGE: {
                getTextFromTextManager(TextManager.PAGE_PREV);
                break;
            }
            case HANDLER_NEXT_PAGE: {
                getTextFromTextManager(TextManager.PAGE_NEXT);
                break;
            }
            case HANDLER_FIRST_PAGE: {
                getTextFromTextManager(TextManager.PAGE_FIRST);
                break;
            }
        }
    }

    /**
     * Handler class for TextManager
     */
    static class FileIOHandler extends Handler {
        private final WeakReference<MemoActivity> mActivity;
        FileIOHandler(MemoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MemoActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}