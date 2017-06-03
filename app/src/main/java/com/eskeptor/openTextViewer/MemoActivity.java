package com.eskeptor.openTextViewer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import layout.MemoWidget;

/**
 * Created by eskeptor on 17. 2. 1.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MemoActivity extends AppCompatActivity {
    // MainActivity로 부터 받아오는 파일과 관련된 변수
    private String openFileURL;
    private String openFileName;
    private String openFolderURL;
    private int memoIndex;      // 새로만든 메모에게 붙여줄 번호
    private boolean enhance;    // 향상된 기능 사용할것인지
    private int formatType;     // 텍스트, 이미지

    // 위젯관련
    private boolean isWidget;
    private int widgetID;

    private EditText editText;          // 텍스트 주체
    private TextManager txtManager;     // 텍스트 저장 및 불러오기 담당
    private LogManager logManager;      // 로그를 기록하는 것
    private File lastLog;               // 로그 파일(새로메모를 만들시 붙여줄 번호)
    private ScrollView scrollView;      // 텍스트 스크롤
    private Context context_this;       // context용

    // 향상된 불러오기의 하단 버튼
    private ScrollView btnLayout;
    private Button btnPrev;
    private Button btnNext;
    private Button btnTop;
    private Runnable nextRunnable;
    private Runnable prevRunnable;

    // 향상된 불러오기의 하단 버튼2
    private ProgressBar progCurrent;
    private TextView txtProgCur;

    // 멀티터치 제스쳐
    private GestureDetectorCompat gestureDetectorCompat;
    private boolean isFirstSwipe = true;
    private float yposStart;
    private float yposEnd;

    // 경고창 재활용
    private AlertDialog.Builder alert;

    // pref 불러오기
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // 메뉴 아이템들
    private MenuItem editMenu;
    private Drawable drawableModified;
    private Drawable drawableModifiedComplete;

    // 자동 포커스 끄기를 위한 InputMethodManager
    private InputMethodManager inputMethodManager;


    @Override
    public boolean onCreateOptionsMenu(Menu _menu)
    {
        if(openFileURL != null || isWidget)
        {
            getMenuInflater().inflate(R.menu.menu_memo, _menu);
            editMenu = _menu.findItem(R.id.menu_memo_modified);
            editMenu.setIcon(drawableModified);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item)
    {
        int id = _item.getItemId();
        if(id == R.id.menu_memo_modified)
        {
            if(editText.isFocusable())
            {
                editText.setFocusable(false);
                editMenu.setIcon(drawableModified);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
            else
            {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                editMenu.setIcon(drawableModifiedComplete);
                inputMethodManager.showSoftInput(editText, 0);
            }
            return true;
        }
        else if(id == R.id.menu_memo_charsetchange)
        {
            alert = new AlertDialog.Builder(MemoActivity.this);
            alert.setTitle(R.string.menu_memo_charsetChange);
            alert.setItems(R.array.menu_memo_charset, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    String txtData = txtManager.openText(openFileURL, 0, enhance, _which);
                    editText.setText(txtData);
                    formatType = _which;
                    _dialog.dismiss();
                }
            });
            alert.show();

        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data)
    {
        if(_resultCode == RESULT_OK)
        {
            if(_requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE)
            {
                String folderURL = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                String fileName = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                if(txtManager.saveText(editText.getText().toString(), folderURL + fileName, enhance))
                {
                    Toast.makeText(context_this, String.format(getResources().getString(R.string.memo_toast_saveSuccess_external), fileName), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context_this, String.format(getResources().getString(R.string.memo_toast_saveFail_external), fileName), Toast.LENGTH_SHORT).show();
                }
            }
            else if(_requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE)
            {
                String folderURL = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                String fileName = _data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_TEXT_EXTENSION;
                if(txtManager.saveText(editText.getText().toString(), folderURL + fileName, enhance))
                {
                    Toast.makeText(context_this, R.string.memo_toast_saveSuccess_internal, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context_this, R.string.memo_toast_saveFail_internal, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_memo);

        context_this = getApplicationContext();
        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        editText = (EditText)findViewById(R.id.memo_etxtMain);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        txtManager = new TextManager();
        logManager = new LogManager();
        scrollView = (ScrollView)findViewById(R.id.memo_scroll);
        btnLayout = (ScrollView)findViewById(R.id.memo_layoutButton);
        formatType = Constant.ENCODE_TYPE_UTF8;

        // 새 파일 생성 : openFileURL 이 null 인 경우
        // 이전 파일 열기 : openFileURL 이 null 이 아닌 경우
        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
        openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
        enhance = pref.getBoolean(Constant.APP_EXPERIMENT_ENHANCEIO, false);
        isWidget = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, false);
        widgetID = getIntent().getIntExtra(Constant.INTENT_EXTRA_WIDGET_ID, 999);
        Log.e("Debug", "getintent widget id : " + widgetID);
        pref = getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + widgetID, MODE_PRIVATE);
        widgetID = pref.getInt(Constant.WIDGET_ID, 0);
        Log.e("Debug", "pref widget id : " + widgetID);

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

            txtManager.initManager();
            btnLayout.setVisibility(View.GONE);
            if(isWidget)
            {
                Log.e("Debug", "widgetID : " + widgetID);
                final File tmpFile = new File(openFolderURL + File.separator + widgetID + Constant.FILE_TEXT_EXTENSION);
                if(tmpFile.exists())
                {
                    setTitle(R.string.memo_title_widget);
                    nextRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String txtData = txtManager.openText(tmpFile.getPath(), 0, enhance, formatType);
                            editText.setText(txtData);
                            editText.setFocusable(false);
                        }
                    };
                    runOnUiThread(nextRunnable);
                    btnLayout.setVisibility(View.GONE);
                }
                else
                {
                    setTitle(getString(R.string.memo_title_newFile) + "(" + getString(R.string.memo_title_newWidgetMemo) + ")");
                    editText.setText("");
                    editText.setFocusable(true);
                }
                if(Build.VERSION.SDK_INT >= 21)
                {
                    drawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp, null);
                    drawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp, null);
                }
                else
                {
                    drawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp);
                    drawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp);
                }
            }
            else
            {
                setTitle(R.string.memo_title_newFile);
                editText.setText("");
                editText.setFocusable(true);
            }
        }
        else
        {
            setTitle(openFileName);

            if(enhance)
            {
                progCurrent = (ProgressBar)findViewById(R.id.memo_Prog);
                txtProgCur = (TextView)findViewById(R.id.memo_txtProg_cur);
                nextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        txtManager.setLines(pref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = txtManager.openText(openFileURL, Constant.MEMO_BLOCK_NEXT, enhance, formatType);
                        editText.setText(txtData);
                        editText.setFocusable(false);
                        progCurrent.setProgress((int)txtManager.getProgress());
                        String cur = String.format("%.2f", txtManager.getProgress()) + "%";
                        txtProgCur.setText(cur);
                    }
                };
                prevRunnable = new Runnable() {
                    @Override
                    public void run() {
                        txtManager.setLines(pref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = txtManager.openText(openFileURL, Constant.MEMO_BLOCK_PREV, enhance, formatType);
                        editText.setText(txtData);
                        editText.setFocusable(false);
                        progCurrent.setProgress((int)txtManager.getProgress());
                        String cur = String.format("%.2f", txtManager.getProgress()) + "%";
                        txtProgCur.setText(cur);
                    }
                };

                runOnUiThread(nextRunnable);

                boolean divide = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_DIVIDE, false);

                if(divide)
                {
                    btnLayout.setVisibility(View.VISIBLE);
                    btnPrev = (Button)findViewById(R.id.memo_btnPrev);
                    btnTop = (Button)findViewById(R.id.memo_btnTop);
                    btnNext = (Button)findViewById(R.id.memo_btnNext);
                    scrollView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = MotionEventCompat.getActionMasked(event);

                            if(event.getPointerCount() > 1)
                            {
                                return gestureDetectorCompat.onTouchEvent(event);
                            }

                            if(action == 1)
                            {
                                isFirstSwipe = true;
                                if(yposStart - yposEnd > 0)
                                    btnLayout.scrollTo((int)btnLayout.getX(), btnLayout.getBottom());
                                else
                                    btnLayout.scrollTo((int)btnLayout.getX(), 0);
                            }
                            return false;
                        }
                    });

                    gestureDetectorCompat = new GestureDetectorCompat(context_this, new GestureDetector.OnGestureListener() {
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
                            yposEnd = e2.getY();

                            if(isFirstSwipe)
                            {
                                yposStart = e2.getY();
                                isFirstSwipe = false;
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

                    btnPrev.setEnabled(false);
                    if(!txtManager.isNext())
                    {
                        btnNext.setEnabled(false);
                    }
                }
                else
                {
                    btnLayout.setVisibility(View.GONE);
                }
            }
            else
            {
                nextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String txtData = txtManager.openText(openFileURL, 0, enhance, formatType);
                        editText.setText(txtData);
                        editText.setFocusable(false);
                    }
                };
                runOnUiThread(nextRunnable);
                btnLayout.setVisibility(View.GONE);
            }

            if(Build.VERSION.SDK_INT >= 21)
            {
                drawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp, null);
                drawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp, null);
            }
            else
            {
                drawableModified = getResources().getDrawable(R.drawable.ic_modifiy_white_24dp);
                drawableModifiedComplete = getResources().getDrawable(R.drawable.ic_save_white_24dp);
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
        editText = null;
        txtManager.initManager();
        txtManager = null;
        logManager = null;
        nextRunnable = null;
        prevRunnable = null;
        btnLayout = null;
        btnPrev = null;
        btnNext = null;
        btnTop = null;
        scrollView = null;
        alert = null;
        context_this = null;
        pref = null;
        editMenu = null;
        drawableModified = null;
        drawableModifiedComplete = null;
        inputMethodManager = null;
        progCurrent = null;
        txtProgCur = null;
        gestureDetectorCompat = null;
        Log.e("Debug", "onDestroy()");
    }

    @Override
    public void onBackPressed() {
        if(isModified())
        {
            if(isWidget)
            {
                openFileURL = openFolderURL + File.separator + (widgetID + Constant.FILE_TEXT_EXTENSION);
                File tmpFile = new File(openFileURL);
                while(tmpFile.exists())
                {
                    widgetID++;
                    openFileURL = openFolderURL + File.separator + (widgetID + Constant.FILE_TEXT_EXTENSION);
                    tmpFile = new File(openFileURL);
                }
                txtManager.saveText(editText.getText().toString(), openFileURL, enhance);
                writeLog();

                editor = context_this.getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + widgetID, MODE_PRIVATE).edit();
                editor.putString(Constant.WIDGET_FILE_URL, openFileURL);
                editor.apply();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context_this);
                MemoWidget.updateAppWidget(context_this, appWidgetManager, widgetID);
                finish();
            }
            else
            {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        if(_which == AlertDialog.BUTTON_POSITIVE)
                        {
                            if(openFileURL == null)
                            {
                                alert = new AlertDialog.Builder(MemoActivity.this);
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
                                            if(txtManager.isFileopen())
                                            {
                                                txtManager.saveText(editText.getText().toString(), openFileURL, enhance);
                                            }
                                            else
                                            {
                                                openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_TEXT_EXTENSION);
                                                File tmpFile = new File(openFileURL);
                                                while(tmpFile.exists())
                                                {
                                                    memoIndex++;
                                                    openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_TEXT_EXTENSION);
                                                    tmpFile = new File(openFileURL);
                                                }
                                                txtManager.saveText(editText.getText().toString(), openFileURL, enhance);
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
                                txtManager.saveText(editText.getText().toString(), txtManager.getFileopen_name(), enhance);
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
        }
        else
        {

            writeLog();
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private boolean isModified() {
        if (openFileURL != null)
        {
            String md5 = txtManager.createMD5(editText.getText().toString());
            if(!txtManager.getMD5().equals(md5))
            {
                return true;
            }
        }
        else
        {
            if (!editText.getText().toString().equals("")) {return true;}
        }
        return false;
    }

    private void writeLog()
    {
        try {
            if(!txtManager.isFileopen())
            {
                logManager.saveLog(Integer.toString(isWidget ? widgetID : memoIndex), lastLog.getPath());
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void onClick(final View _v)
    {
        int id = _v.getId();
        if(id == R.id.memo_btnPrev)
        {
            runOnUiThread(prevRunnable);
            scrollView.scrollTo(0,0);
            buttonEnabler();
        }
        else if(id == R.id.memo_btnTop)
        {
            scrollView.smoothScrollTo(0, 0);
        }
        else if(id == R.id.memo_btnNext)
        {
            runOnUiThread(nextRunnable);
            scrollView.scrollTo(0,0);
            buttonEnabler();
        }
    }

    private void buttonEnabler()
    {
        if(!txtManager.isPrev())
        {
            btnPrev.setEnabled(false);
        }
        else
        {
            btnPrev.setEnabled(true);
        }
        if(!txtManager.isNext())
        {
            btnNext.setEnabled(false);
        }
        else
        {
            btnNext.setEnabled(true);
        }
    }
}
