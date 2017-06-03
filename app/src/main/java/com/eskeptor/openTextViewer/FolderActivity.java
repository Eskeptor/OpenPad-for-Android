package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.eskeptor.openTextViewer.datatype.FolderObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class FolderActivity extends AppCompatActivity
{
    private ArrayList<FolderObject> folders;                        // 폴더를 나열할 ArrayList
    private FolderAdaptor folderAdaptor;                            // 폴더를 나열할 ArrayList 에 쓰일 어댑터
    private Context context_this;                                   // context 용
    private EditText editText;                                      // 폴더생성시 이름 넣을때 쓰는 edittext
    private int foldersLength;                                      // 폴더의 개수
    private String newFolderName;                                   // 새폴더 이름
    private AlertDialog.Builder dialog;                             // 다이얼로그 재활용
    private ListView folderList;                                    // 폴더 리스트
    private Runnable refreshRunnable;                               // 새로고침 할때 쓰일 Runnable
    private AdapterView.OnItemClickListener clickListener;          // 클릭 리스너(어댑터용)
    private AdapterView.OnItemLongClickListener longClickListener;  // 롱 클릭 리스너(어댑터용)

    public boolean onCreateOptionsMenu(Menu _menu)
    {
        getMenuInflater().inflate(R.menu.menu_folder, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item)
    {
        if(_item.getItemId() == R.id.menu_folderAdd)
        {
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.folder_dialog_title_create);
            View layout = LayoutInflater.from(context_this).inflate(R.layout.dialog_folder_create, null);
            editText = (EditText)layout.findViewById(R.id.dialog_folder_input);
            dialog.setView(layout);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which)
                {
                    if(_which == AlertDialog.BUTTON_POSITIVE)
                    {
                        newFolderName = editText.getText().toString();
                        if(!newFolderName.equals(""))
                        {
                            File file = new File(Constant.APP_INTERNAL_URL + File.separator + newFolderName);
                            if(file.exists())
                                Toast.makeText(context_this, R.string.folder_dialog_toast_exist, Toast.LENGTH_SHORT).show();
                            else
                            {
                                file.mkdir();
                                //refreshList();
                                runOnUiThread(refreshRunnable);
                            }
                        }
                    }
                    _dialog.dismiss();
                }
            };
            dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
            dialog.setPositiveButton(R.string.folder_dialog_button_create, clickListener);
            dialog.show();
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        setResult(RESULT_OK);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_folder);

        context_this = getApplicationContext();

        setTitle(R.string.folder_title);
        folderList = (ListView)findViewById(R.id.folder_list) ;
        folders = new ArrayList<>();
        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
                if(_position == foldersLength)
                {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setClass(context_this, FileBrowserActivity.class);
                    intent.setType("text/plain");
                    intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, Constant.BROWSER_TYPE_OPEN_EXTERNAL);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL, folders.get(_position).url);
                    setResult(RESULT_OK, intent);
                }
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        };
        longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> _parent, View _view, int _position, long _id) {

                if(_position == foldersLength)
                {
                    return false;
                }
                else
                {
                    deleteFolder(_position);
                    return true;
                }
            }
        };

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                File file = new File(Constant.APP_INTERNAL_URL);
                File files[] = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File _pathname) {
                        return _pathname.isDirectory();
                    }
                });
                foldersLength = files.length;

                folders.clear();
                for(int i = 0; i < foldersLength; i++)
                {
                    folders.add(new FolderObject(files[i].getName(), files[i].listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File _pathname) {
                            return _pathname.isFile() && (_pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION)
                                    || _pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION));
                        }
                    }).length, checkFolderType(files[i]), context_this));
                }
                // 파일 브라우저 연결
                folders.add(new FolderObject(getResources().getString(R.string.folder_externalBrowser), Constant.FOLDER_TYPE_EXTERNAL, Constant.FOLDER_TYPE_EXTERNAL, null));
                if(folderAdaptor == null)
                {
                    folderAdaptor = new FolderAdaptor(context_this, folders);
                    folderList.setAdapter(folderAdaptor);
                    folderList.setOnItemClickListener(clickListener);
                    folderList.setOnItemLongClickListener(longClickListener);
                }
                folderAdaptor.notifyDataSetChanged();
            }
        };

        runOnUiThread(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null)
        {
            dialog = null;
        }
        folderList = null;
        folderAdaptor = null;
        if(!folders.isEmpty())
        {
            folders.clear();
        }
        folders = null;
        context_this = null;
        refreshRunnable = null;
        editText = null;
        clickListener = null;
        longClickListener = null;
    }

    // 폴더의 타입을 체크함(기본폴더, 일반폴더)
    private int checkFolderType(final File _file)
    {
        if(_file.getName().equals(Constant.FOLDER_DEFAULT_NAME))
        {
            return Constant.FOLDER_TYPE_DEFAULT;
        }
        return Constant.FOLDER_TYPE_CUSTOM;
    }

    // 폴더 삭제
    private void deleteFolder(final int _index)
    {
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.folder_dialog_title_delete);
        dialog.setMessage(R.string.folder_dialog_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                if(_which == AlertDialog.BUTTON_POSITIVE)
                {
                    File file = new File(folders.get(_index).url);
                    if(file.exists())
                    {
                        if(!file.getName().equals(Constant.FOLDER_DEFAULT_NAME) && !file.getName().equals(Constant.FOLDER_WIDGET_NAME)
                                && file.delete())
                        {
                            Toast.makeText(context_this, R.string.folder_dialog_toast_delete, Toast.LENGTH_SHORT).show();
                            runOnUiThread(refreshRunnable);
                        }
                        else
                        {
                            Toast.makeText(context_this, R.string.folder_toast_remove_defaultfolder, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context_this, getString(R.string.error_folder_not_exist), Toast.LENGTH_SHORT).show();
                    }
                }
                _dialog.dismiss();
            }
        };
        dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
        dialog.setPositiveButton(R.string.folder_dialog_button_delete, clickListener);
        dialog.show();
    }
}
