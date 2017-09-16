package com.eskeptor.openTextViewer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import layout.MemoWidget;

/**
 * Created by eskeptor on 17. 2. 1.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MemoActivity extends AppCompatActivity {
    // MainActivity로 부터 받아오는 파일과 관련된 변수
    private String mOpenFileURL;
    private String mOpenFileName;
    private String mOpenFolderURL;
    private int mMemoIndex;      // 새로만든 메모에게 붙여줄 번호
    private boolean mIsEnhanced;    // 향상된 기능 사용할것인지
    private Constant.EncodeType mEncodeType;

    // 위젯관련
    private boolean mIsWidget;
    private int mWidgetID;

    private EditText mEditText;          // 텍스트 주체
    private TextManager mTxtManager;     // 텍스트 저장 및 불러오기 담당
    private LogManager mLogManager;      // 로그를 기록하는 것
    private File mLastLog;               // 로그 파일(새로메모를 만들시 붙여줄 번호)
    private ScrollView mScrollView;      // 텍스트 스크롤
    private Context mContextThis;       // context용
    private View mContextView;

    // 향상된 불러오기의 하단 버튼
    private ScrollView mBtnLayout;
    private Button mBtnPrev;
    private Button mBtnNext;
    private Button mBtnTop;
    private Runnable mNextRunnable;
    private Runnable mPrevRunnable;

    // 향상된 불러오기의 하단 버튼2
    private ProgressBar mProgCurrent;
    private TextView mTxtProgCur;

    // 멀티터치 제스쳐
    private GestureDetectorCompat mGestureDetectorCompat;
    private boolean mIsFirstSwipe = true;
    private float mTouchYposStart;
    private float mTouchYposEnd;

    // mSharedPref 불러오기
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    // 메뉴 아이템들
    private MenuItem mEditMenu;
    private Drawable mDrawableModified;
    private Drawable mDrawableModifiedComplete;

    // 자동 포커스 끄기를 위한 InputMethodManager
    private InputMethodManager mInputMethodManager;


    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        if (mOpenFileURL != null || mIsWidget) {
            getMenuInflater().inflate(R.menu.menu_memo, _menu);
            mEditMenu = _menu.findItem(R.id.menu_memo_modified);
            mEditMenu.setIcon(mDrawableModified);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        int id = _item.getItemId();
        switch (id) {
            case R.id.menu_memo_modified:
                if (mEditText.isFocusable()) {
                    mEditText.setFocusable(false);
                    mEditMenu.setIcon(mDrawableModified);
                    mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                } else {
                    mEditText.setFocusable(true);
                    mEditText.setFocusableInTouchMode(true);
                    mEditText.requestFocus();
                    mEditMenu.setIcon(mDrawableModifiedComplete);
                    mInputMethodManager.showSoftInput(mEditText, 0);
                }
                return true;
            case R.id.menu_memo_charsetchange:
                AlertDialog.Builder alert = new AlertDialog.Builder(MemoActivity.this);
                alert.setTitle(R.string.menu_memo_charsetChange);
                alert.setItems(R.array.menu_memo_charset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        String txtData;
                        if (_which == 0)
                            mEncodeType = Constant.EncodeType.EUCKR;
                        else
                            mEncodeType = Constant.EncodeType.UTF8;
                        txtData = mTxtManager.openText(mOpenFileURL, 0, mIsEnhanced, Constant.EncodeType.UTF8);
                        mEditText.setText(txtData);
                        _dialog.dismiss();
                    }
                });
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        if (_resultCode == RESULT_OK) {
            switch (_requestCode) {
                case Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE: {
                    String folderURL = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                    String fileName = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                    if (mTxtManager.saveText(mEditText.getText().toString(), folderURL + fileName, mIsEnhanced)) {
                        Snackbar.make(mContextView, String.format(getResources().getString(R.string.memo_toast_saveSuccess_external), fileName), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(mContextView, String.format(getResources().getString(R.string.memo_toast_saveFail_external), fileName), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE: {
                    String folderURL = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                    String fileName = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                    if (mTxtManager.saveText(mEditText.getText().toString(), folderURL + fileName, mIsEnhanced)) {
                        Snackbar.make(mContextView, R.string.memo_toast_saveSuccess_internal, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(mContextView, R.string.memo_toast_saveFail_internal, Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_memo);

        mContextThis = getApplicationContext();
        mContextView = findViewById(R.id.activity_memo);
        mSharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mEditText = (EditText) findViewById(R.id.memo_etxtMain);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSharedPref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        mTxtManager = new TextManager();
        mLogManager = new LogManager();
        mScrollView = (ScrollView) findViewById(R.id.memo_scroll);
        mBtnLayout = (ScrollView) findViewById(R.id.memo_layoutButton);
        mEncodeType = Constant.EncodeType.UTF8;

        // 새 파일 생성 : mOpenFileURL 이 null 인 경우
        // 이전 파일 열기 : mOpenFileURL 이 null 이 아닌 경우
        mOpenFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        mOpenFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
        mOpenFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
        mIsEnhanced = mSharedPref.getBoolean(Constant.APP_EXPERIMENT_ENHANCEIO, false);
        mIsWidget = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, false);
        mWidgetID = getIntent().getIntExtra(Constant.INTENT_EXTRA_WIDGET_ID, 999);
        mSharedPref = getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + mWidgetID, MODE_PRIVATE);
        mWidgetID = mSharedPref.getInt(Constant.WIDGET_ID, 0);

        if (mOpenFileURL == null) {
            mLastLog = new File(mOpenFolderURL + File.separator + Constant.FILE_LOG_COUNT);
            if (!mLastLog.exists()) {
                try {
                    if(mLastLog.createNewFile()) {
                        mMemoIndex = 1;
                        mLogManager.saveLog(Integer.toString(mMemoIndex), mLastLog.getPath());
                    }
                } catch (IOException ioe) {
                    Log.e("MemoActivity", ioe.getMessage());
                }

            } else {
                try {
                    mMemoIndex = Integer.parseInt(mLogManager.openLog(mLastLog.getPath()));
                } catch (Exception e) {
                    Log.e("MemoActivity", e.getMessage());
                }
            }

            mTxtManager.initManager();
            mBtnLayout.setVisibility(View.GONE);
            if (mIsWidget) {
                final File tmpFile = new File(mOpenFolderURL + File.separator + mWidgetID + Constant.FILE_TEXT_EXTENSION);
                if (tmpFile.exists()) {
                    setTitle(R.string.memo_title_widget);
                    mOpenFileURL = tmpFile.getPath();
                    mNextRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String txtData = mTxtManager.openText(mOpenFileURL, 0, mIsEnhanced, mEncodeType);
                            mEditText.setText(txtData);
                            mEditText.setFocusable(false);
                        }
                    };
                    runOnUiThread(mNextRunnable);
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

            if (mIsEnhanced) {
                mProgCurrent = (ProgressBar) findViewById(R.id.memo_Prog);
                mTxtProgCur = (TextView) findViewById(R.id.memo_txtProg_cur);
                mNextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTxtManager.setLines(mSharedPref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = mTxtManager.openText(mOpenFileURL, Constant.MEMO_BLOCK_NEXT, mIsEnhanced, mEncodeType);
                        mEditText.setText(txtData);
                        mEditText.setFocusable(false);
                        mProgCurrent.setProgress((int) mTxtManager.getProgress());
                        String cur = String.format(Locale.getDefault(), "%.2f", mTxtManager.getProgress()) + "%";
                        mTxtProgCur.setText(cur);
                    }
                };
                mPrevRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTxtManager.setLines(mSharedPref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = mTxtManager.openText(mOpenFileURL, Constant.MEMO_BLOCK_PREV, mIsEnhanced, mEncodeType);
                        mEditText.setText(txtData);
                        mEditText.setFocusable(false);
                        mProgCurrent.setProgress((int) mTxtManager.getProgress());
                        String cur = String.format(Locale.getDefault(), "%.2f", mTxtManager.getProgress()) + "%";
                        mTxtProgCur.setText(cur);
                    }
                };

                runOnUiThread(mNextRunnable);

                boolean divide = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_DIVIDE, false);

                if (divide) {
                    mBtnLayout.setVisibility(View.VISIBLE);
                    mBtnPrev = (Button) findViewById(R.id.memo_btnPrev);
                    mBtnTop = (Button) findViewById(R.id.memo_btnTop);
                    mBtnNext = (Button) findViewById(R.id.memo_btnNext);
                    mScrollView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = MotionEventCompat.getActionMasked(event);

                            if (event.getPointerCount() > 1) {
                                return mGestureDetectorCompat.onTouchEvent(event);
                            }

                            if (action == 1) {
                                mIsFirstSwipe = true;
                                if (mTouchYposStart - mTouchYposEnd > 0)
                                    mBtnLayout.scrollTo((int) mBtnLayout.getX(), mBtnLayout.getBottom());
                                else
                                    mBtnLayout.scrollTo((int) mBtnLayout.getX(), 0);
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
                            mTouchYposEnd = e2.getY();

                            if (mIsFirstSwipe) {
                                mTouchYposStart = e2.getY();
                                mIsFirstSwipe = false;
                            }
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
                    if (!mTxtManager.isNext()) {
                        mBtnNext.setEnabled(false);
                    }
                } else {
                    mBtnLayout.setVisibility(View.GONE);
                }
            } else {
                mNextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String txtData = mTxtManager.openText(mOpenFileURL, 0, mIsEnhanced, mEncodeType);
                        mEditText.setText(txtData);
                        mEditText.setFocusable(false);
                    }
                };
                runOnUiThread(mNextRunnable);
                mBtnLayout.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp, null);
                mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp, null);
            } else {
                mDrawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp);
                mDrawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp);
            }
        }
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
        mLogManager = null;
        mNextRunnable = null;
        mPrevRunnable = null;
        mBtnLayout = null;
        mBtnPrev = null;
        mBtnNext = null;
        mBtnTop = null;
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
        mContextView = null;
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
                mTxtManager.saveText(mEditText.getText().toString(), mOpenFileURL, false);
                writeLog();

                mSharedPrefEditor = mContextThis.getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + origin_id, MODE_PRIVATE).edit();
                mSharedPrefEditor.putString(Constant.WIDGET_FILE_URL, mOpenFileURL);
                mSharedPrefEditor.apply();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContextThis);
                MemoWidget.updateAppWidget(mContextThis, appWidgetManager, origin_id);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, origin_id);
                setResult(RESULT_OK, resultValue);
                finish();
            } else {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        if (_which == AlertDialog.BUTTON_POSITIVE) {
                            if (mOpenFileURL == null) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MemoActivity.this);
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
                                                if (mTxtManager.isFileopen()) {
                                                    mTxtManager.saveText(mEditText.getText().toString(), mOpenFileURL, mIsEnhanced);
                                                } else {
                                                    mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_TEXT_EXTENSION);
                                                    File tmpFile = new File(mOpenFileURL);
                                                    while (tmpFile.exists()) {
                                                        mMemoIndex++;
                                                        mOpenFileURL = mOpenFolderURL + File.separator + (mMemoIndex + Constant.FILE_TEXT_EXTENSION);
                                                        tmpFile = new File(mOpenFileURL);
                                                    }
                                                    mTxtManager.saveText(mEditText.getText().toString(), mOpenFileURL, mIsEnhanced);
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
                                mTxtManager.saveText(mEditText.getText().toString(), mTxtManager.getFileopen_name(), mIsEnhanced);
                                finish();
                            }
                        } else if (_which == AlertDialog.BUTTON_NEGATIVE) {
                            finish();
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
            }
        } else {
            writeLog();
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private boolean isModified() {
        if (mOpenFileURL != null) {
            String md5 = mTxtManager.createMD5(mEditText.getText().toString());
            if (!mTxtManager.getMD5().equals(md5)) {
                return true;
            }
        } else {
            if (!mEditText.getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }

    private void writeLog() {
        try {
            if (!mTxtManager.isFileopen()) {
                mLogManager.saveLog(Integer.toString(mIsWidget ? mWidgetID : mMemoIndex), mLastLog.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(final View _v) {
        int id = _v.getId();
        switch (id) {
            case R.id.memo_btnPrev:
                runOnUiThread(mPrevRunnable);
                mScrollView.scrollTo(0, 0);
                buttonEnabler();
                break;
            case R.id.memo_btnTop:
                mScrollView.smoothScrollTo(0, 0);
                break;
            case R.id.memo_btnNext:
                runOnUiThread(mNextRunnable);
                mScrollView.scrollTo(0, 0);
                buttonEnabler();
                break;
        }
    }

    private void buttonEnabler() {
        if (!mTxtManager.isPrev()) {
            mBtnPrev.setEnabled(false);
        } else {
            mBtnPrev.setEnabled(true);
        }

        if (!mTxtManager.isNext()) {
            mBtnNext.setEnabled(false);
        } else {
            mBtnNext.setEnabled(true);
        }
    }
}