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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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
    private long fileSize;

    private EditText editText;
    private TextManager txtManager;
    private LogManager logManager;

    private File lastLog;
    private AlertDialog.Builder alert;

    private Context context_this;

    private SharedPreferences pref;

    private MenuItem editMenu;
    private Drawable drawableModified;
    private Drawable drawableModifiedComplete;
    private InputMethodManager inputMethodManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            if(fileSize <= Constant.SAFE_LOAD_CAPACITY * Constant.KILOBYTE)
            {
                getMenuInflater().inflate(R.menu.menu_memo, menu);
                editMenu = menu.findItem(R.id.menu_memo_modified);
                editMenu.setIcon(drawableModified);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.menu_memo_modified)
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE)
            {
                String folderURL = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                String fileName = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_EXTENSION;
                if(txtManager.saveText(editText.getText().toString(), folderURL + fileName))
                {
                    Toast.makeText(context_this, String.format(getResources().getString(R.string.memo_toast_saveSuccess_external), fileName), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context_this, String.format(getResources().getString(R.string.memo_toast_saveFail_external), fileName), Toast.LENGTH_SHORT).show();
                }
            }
            else if(requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE)
            {
                String folderURL = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                String fileName = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_EXTENSION;
                if(txtManager.saveText(editText.getText().toString(), folderURL + fileName))
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        context_this = getApplicationContext();
        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        memoType = getIntent().getIntExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
        editText = (EditText)findViewById(R.id.memo_etxtMain);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        txtManager = new TextManager();
        logManager = new LogManager();
        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        fileSize = getIntent().getLongExtra(Constant.INTENT_EXTRA_FILE_SIZE, 0L);

        if(memoType == Constant.MEMO_TYPE_NEW)
        {
            lastLog = new File(openFolderURL + File.separator + Constant.LOG_FILE_COUNT);
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
            setTitle(R.string.memo_title_newFile);
            editText.setText("");
            editText.setFocusable(true);
        }
        else if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            openFileURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL);
            openFileName = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME);
            setTitle(openFileName);
            String txtData = txtManager.openText(openFileURL);
            editText.setText(txtData);
            editText.setFocusable(false);
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
    public void onBackPressed() {
        if((fileSize <= Constant.SAFE_LOAD_CAPACITY) && isModified())
        {
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == AlertDialog.BUTTON_POSITIVE)
                    {
                        if(memoType == Constant.MEMO_TYPE_NEW)
                        {
                            alert = new AlertDialog.Builder(MemoActivity.this);
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
                                        if(txtManager.isFileopen())
                                        {
                                            txtManager.saveText(editText.getText().toString(), openFileURL);
                                        }
                                        else
                                        {
                                            openFileURL = openFolderURL + File.separator + (memoIndex + Constant.FILE_EXTENSION);
                                            txtManager.saveText(editText.getText().toString(), openFileURL);
                                            memoIndex++;
                                            writeLog();
                                        }
                                        finish();
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }
                        else if(memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL || memoType == Constant.MEMO_TYPE_OPEN_INTERNAL)
                        {
                            txtManager.saveText(editText.getText().toString(), txtManager.getFileopen_name());
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
}
