package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 1. 28.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MainActivity extends AppCompatActivity {
    private long backPressedTime;

    private String curFolderURL;

    private GridView curFolderGridView;
    private TextFileAdaptor curFileAdapter;
    private ArrayList<TextFile> curFolderFileList;
    private Drawable folderIcon;

    private AlertDialog.Builder dialog;
    private AdapterView.OnItemClickListener clickListener;
    private AdapterView.OnItemLongClickListener longClickListener;

    private Context context_this;

    private FloatingActionButton fab;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(context_this, FolderActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_OPEN_FOLDER);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                return true;
            }
            case R.id.menu_main_settings:
            {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(context_this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_out_bottom);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case Constant.REQUEST_CODE_OPEN_FOLDER:
                {
                    curFolderURL = data.getStringExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL);
                    curFolderFiles();
                    break;
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_this = getApplicationContext();

        if(Build.VERSION.SDK_INT >= 21)
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white, null);
        else
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(folderIcon);

        curFolderGridView = (GridView)findViewById(R.id.main_curFolderFileList);
        curFolderFileList = new ArrayList<>();

        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(context_this, MemoActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, curFolderFileList.get(position).url);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, curFolderFileList.get(position).title);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_INTERNAL);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, curFolderURL);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        };

        longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFile(position);
                return true;
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.main_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(context_this, MemoActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, curFolderURL);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        defaultFolderCheck();
        curFolderFiles();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && Constant.WAIT_FOR_SECOND >= intervalTime)
            super.onBackPressed();
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(this, getResources().getString(R.string.back_press), Toast.LENGTH_SHORT).show();
        }
    }

    private void defaultFolderCheck()
    {
        File file = new File(Constant.APP_INTERNAL_URL);
        if(!file.exists())
            file.mkdir();
        file = new File(Constant.APP_INTERNAL_URL + File.separator+ Constant.FOLDER_DEFAULT_NAME);
        if(!file.exists())
            file.mkdir();
        curFolderURL = file.getPath();
    }

    private void curFolderFiles()
    {
        File file = new File(curFolderURL);
        File files[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Constant.FILE_EXTENSION);
            }
        });
        curFolderFileList.clear();
        for(int i = 0; i < files.length; i++)
            curFolderFileList.add(new TextFile(files[i], getResources().getString(R.string.file_noname), new SimpleDateFormat(getResources().getString(R.string.file_dateformat))));

        curFileAdapter = new TextFileAdaptor(this, curFolderFileList);
        curFolderGridView.setAdapter(curFileAdapter);
        curFolderGridView.setOnItemClickListener(clickListener);
        curFolderGridView.setOnItemLongClickListener(longClickListener);
    }

    private void deleteFile(final int index)
    {
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_file_title_delete);
        dialog.setMessage(R.string.dialog_file_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case AlertDialog.BUTTON_POSITIVE:
                    {
                        File file = new File(curFolderFileList.get(index).url);
                        if(file.exists())
                            if(file.delete())
                            {
                                Toast.makeText(context_this, getResources().getString(R.string.dialog_file_toast_delete), Toast.LENGTH_SHORT).show();
                                curFolderFiles();
                            }
                            else
                                Toast.makeText(context_this, "Error code : " + ErrorCode.NO_FOLDER, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case AlertDialog.BUTTON_NEGATIVE:
                    {
                        break;
                    }
                }
                dialog.dismiss();
            }
        };
        dialog.setNegativeButton(R.string.dialog_folder_button_cancel, clickListener);
        dialog.setPositiveButton(R.string.dialog_folder_button_delete, clickListener);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        curFolderFiles();
    }
}
