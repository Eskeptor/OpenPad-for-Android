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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class FolderActivity extends AppCompatActivity {
    private ArrayList<Folder> folders;
    private FolderAdaptor folderAdaptor;
    private Context context_this;
    private int folders_length;
    private String newFolderName;
    private AlertDialog.Builder dialog;
    private ListView folderList;
    private AdapterView.OnItemClickListener clickListener;
    private AdapterView.OnItemLongClickListener longClickListener;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_folderAdd:
            {
                createNewFolder();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        context_this = getApplicationContext();
        folderList = (ListView)findViewById(R.id.folder_list) ;
        folders = new ArrayList<>();
        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == folders_length)
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
                    intent.putExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL, folders.get(position).url);
                    setResult(RESULT_OK, intent);
                }
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        };
        longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == folders_length)
                {
                    return false;
                }
                else
                {
                    deleteFolder(position);
                    return true;
                }
            }
        };
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        folders.clear();
        folderList = null;
        finish();
    }

    private int checkFolderType(final File file)
    {
        if(file.getName().equals(Constant.FOLDER_DEFAULT_NAME))
        {
            return Constant.FOLDER_TYPE_DEFAULT;
        }
        return Constant.FOLDER_TYPE_CUSTOM;
    }

    private void refreshList()
    {
        File file = new File(Constant.APP_INTERNAL_URL);
        File files[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        folders_length = files.length;

        folders.clear();
        for(int i = 0; i < files.length; i++)
        {
            folders.add(new Folder(files[i].getName(), files[i].listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(Constant.FILE_EXTENSION);
                }
            }).length, checkFolderType(files[i]), this));
        }
        // 파일 브라우저 연결
        folders.add(new Folder(getResources().getString(R.string.folder_externalBrowser), Constant.FOLDER_TYPE_EXTERNAL, Constant.FOLDER_TYPE_EXTERNAL, null));
        folderAdaptor = new FolderAdaptor(this, folders);
        folderList.setAdapter(folderAdaptor);
        folderList.setOnItemClickListener(clickListener);
        folderList.setOnItemLongClickListener(longClickListener);
    }

    private void createNewFolder()
    {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_folder_create, null);
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_folder_title_create);
        dialog.setView(layout);
        final EditText editText = (EditText)layout.findViewById(R.id.dialog_folder_input);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case AlertDialog.BUTTON_POSITIVE:
                    {
                        newFolderName = editText.getText().toString();
                        if(!newFolderName.equals(""))
                        {
                            File file = new File(Constant.APP_INTERNAL_URL + File.separator + newFolderName);
                            if(file.exists())
                                Toast.makeText(context_this, R.string.dialog_folder_toast_exist, Toast.LENGTH_SHORT).show();
                            else
                            {
                                file.mkdir();
                                refreshList();
                            }
                        }
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
        dialog.setPositiveButton(R.string.dialog_folder_button_create, clickListener);
        dialog.show();
    }

    private void deleteFolder(final int index)
    {
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_folder_title_delete);
        dialog.setMessage(R.string.dialog_folder_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case AlertDialog.BUTTON_POSITIVE:
                    {
                        File file = new File(folders.get(index).url);
                        if(file.exists())
                            if(!file.getName().equals(Constant.FOLDER_DEFAULT_NAME) && file.delete())
                            {
                                Toast.makeText(context_this, getResources().getString(R.string.dialog_folder_toast_delete), Toast.LENGTH_SHORT).show();
                                refreshList();
                            }
                            else
                                Toast.makeText(context_this, getResources().getString(R.string.folder_toast_remove_defaultfolder), Toast.LENGTH_SHORT).show();
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
}
