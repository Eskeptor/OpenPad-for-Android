package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.eskeptor.openTextViewer.textManager.LogManager;
import com.eskeptor.openTextViewer.textManager.TextManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by eskeptor on 17. 2. 1.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MemoActivity extends AppCompatActivity {
    private String openFileURL;
    private String openFileName;
    private String openFolderURL;

    private int memoType;
    private int memoIndex;
    private boolean isWidget;
    private boolean enhance;
    private int formatType;

    private EditText editText;
    private TextManager txtManager;
    private LogManager logManager;
    private Runnable nextRunnable;
    private Runnable prevRunnable;
    private LinearLayout btnLayout;
    private ScrollView scrollView;
    private Button btnPrev;
    private Button btnNext;
    private Button btnTop;

    private File lastLog;
    private AlertDialog.Builder alert;

    private Context context_this;

    private SharedPreferences pref;

    private MenuItem editMenu;
    private Drawable drawableModified;
    private Drawable drawableModifiedComplete;
    private InputMethodManager inputMethodManager;

    @Override
    public boolean onCreateOptionsMenu(Menu _menu)
    {
        if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
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

        enhance = pref.getBoolean(Constant.APP_EXPERIMENT_ENHANCEIO, false);
        memoType = getIntent().getIntExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
        isWidget = getIntent().getBooleanExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, false);
        editText = (EditText)findViewById(R.id.memo_etxtMain);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        txtManager = new TextManager();
        logManager = new LogManager();
        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        btnLayout = (LinearLayout)findViewById(R.id.memo_layoutButton);
        formatType = Constant.ENCODE_TYPE_UTF8;

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
            txtManager.initManager();
            btnLayout.setVisibility(View.GONE);
            setTitle(R.string.memo_title_newFile);
            editText.setText("");
            editText.setFocusable(true);
        }
        else if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
            openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
            setTitle(openFileName);


            if(enhance)
            {
                nextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        txtManager.setLines(pref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = txtManager.openText(openFileURL, Constant.MEMO_BLOCK_NEXT, enhance, formatType);
                        editText.setText(txtData);
                        editText.setFocusable(false);
                    }
                };
                prevRunnable = new Runnable() {
                    @Override
                    public void run() {
                        txtManager.setLines(pref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                        String txtData = txtManager.openText(openFileURL, Constant.MEMO_BLOCK_PREV, enhance, formatType);
                        editText.setText(txtData);
                        editText.setFocusable(false);
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
                    scrollView = (ScrollView)findViewById(R.id.memo_scroll);
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
    }

    @Override
    public void onBackPressed() {
        if(isModified())
        {
            if(isWidget)
            {
                openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_TEXT_EXTENSION);
                txtManager.saveText(editText.getText().toString(), Constant.WIDGET_LINKED_TOKEN + openFileURL, enhance);
                memoIndex++;
                writeLog();
                finish();
            }
            else
            {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        if(_which == AlertDialog.BUTTON_POSITIVE)
                        {
                            Log.e("Debug", "which : " + Integer.toString(_which));
                            Log.e("Debug", "memoType : " + Integer.toString(memoType));
                            if(memoType == Constant.MEMO_TYPE_NEW)
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
                                                txtManager.saveText(editText.getText().toString(), openFileURL, enhance);
                                                memoIndex++;
                                                writeLog();
                                            }
                                            finish();
                                        }
                                        _dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                            else if(memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL || memoType == Constant.MEMO_TYPE_OPEN_INTERNAL)
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
        if (memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
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
                logManager.saveLog(Integer.toString(memoIndex), lastLog.getPath());
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
