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
import android.widget.EditText;

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

    public boolean onCreateOptionsMenu(Menu menu) {
        if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_memo_modified:
            {
                if(editText.isFocusable())
                {
                    editText.setFocusable(false);
                    editMenu.setIcon(drawableModified);
                }
                else
                {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editMenu.setIcon(drawableModifiedComplete);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE || requestCode == Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE)
            {
                String folderURL = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL);
                String fileName = data.getStringExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL) + Constant.FILE_EXTENSION;
                txtManager.saveText(editText.getText().toString(), folderURL + fileName);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        context_this = getApplicationContext();
        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);

        memoType = getIntent().getIntExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
        editText = (EditText)findViewById(R.id.memo_etxtMain);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
        txtManager = new TextManager();
        logManager = new LogManager();
        openFolderURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL);
        fileSize = getIntent().getLongExtra(Constant.INTENT_EXTRA_FILE_SIZE, 0L);

        if(memoType == Constant.MEMO_TYPE_NEW)
        {
            Log.e("Debug", "New File");
            lastLog = new File(openFolderURL + File.separator + Constant.LOG_FILE_COUNT);
            if(!lastLog.exists())
            {
                try
                {
                    lastLog.createNewFile();
                    memoIndex = 1;
                    logManager.saveLog(Integer.toString(memoIndex), lastLog.getPath());
                    Log.e("Debug", "Create Log");
                }
                catch (IOException ioe){ioe.printStackTrace();}
            }
            else
            {
                try
                {
                    Log.e("Debug", logManager.openLog(lastLog.getPath()));
                    memoIndex = Integer.parseInt(logManager.openLog(lastLog.getPath()));
                    Log.e("Debug", "Get Log");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Debug", "Exception!! : " + logManager.openLog(lastLog.getPath()));
                }
            }
            txtManager.initManager();
            setTitle(R.string.memo_title_newFile);
            editText.setText("");
            editText.setFocusable(true);
            editText.clearFocus();
        }
        else if(memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            Log.e("Debug", "Open File");
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

    private AlertDialog.Builder createSeletor(final int type)
    {
        AlertDialog.Builder openSelector = new AlertDialog.Builder(this);
        openSelector.setTitle(R.string.memo_alert_save_context);
        openSelector.setItems(R.array.main_selectalert, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(context_this, FileBrowserActivity.class);
                        intent.setType("text/plain");
                        switch (type)
                        {
                            case Constant.SELECTOR_TYPE_SAVE:
                            {
                                intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE);
                                startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE);
                                break;
                            }
                            case Constant.SELECTOR_TYPE_OPEN:
                            {
                                intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_OPEN_EXTERNAL);
                                startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE);
                                break;
                            }
                        }
                        break;
                    }
                    case 1:
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
                            Log.e("Debug", "isn't open");
                        }
                        finish();
                        break;
                    }
                }
                dialog.dismiss();
            }
        });
        return openSelector;
    }

    private boolean isModified() {
        if (memoType == Constant.MEMO_TYPE_OPEN_INTERNAL || memoType == Constant.MEMO_TYPE_OPEN_EXTERNAL)
        {
            String md5 = md5 = txtManager.createMD5(editText.getText().toString());
            Log.i("Debug", "Detect MD5 : " + md5);
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
        Log.e("Debug", "memoIndex : " + memoIndex);
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
                    switch (which)
                    {
                        case AlertDialog.BUTTON_POSITIVE:
                        {
                            switch (memoType)
                            {
                                case Constant.MEMO_TYPE_NEW:
                                {
                                    AlertDialog openDialog = createSeletor(Constant.SELECTOR_TYPE_SAVE).create();
                                    openDialog.show();
                                    break;
                                }
                                case Constant.MEMO_TYPE_OPEN_EXTERNAL:
                                {
                                    txtManager.saveText(editText.getText().toString(), txtManager.getFileopen_name());
                                    finish();
                                    break;
                                }
                                case Constant.MEMO_TYPE_OPEN_INTERNAL:
                                {
                                    txtManager.saveText(editText.getText().toString(), txtManager.getFileopen_name());
                                    finish();
                                    break;
                                }
                            }
                            break;
                        }
                        case AlertDialog.BUTTON_NEGATIVE:
                        {
                            switch (memoType)
                            {
                                case Constant.MEMO_TYPE_NEW:
                                {
                                    finish();
                                    break;
                                }
                                case Constant.MEMO_TYPE_OPEN_INTERNAL:
                                {
                                    writeLog();
                                    finish();
                                    break;
                                }
                                case Constant.MEMO_TYPE_OPEN_EXTERNAL:
                                {
                                    finish();
                                    break;
                                }
                            }
                            break;
                        }
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
}
