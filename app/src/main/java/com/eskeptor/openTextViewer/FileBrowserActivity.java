package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.eskeptor.openTextViewer.datatype.FileObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Created by eskeptor on 17. 1. 26.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class FileBrowserActivity extends AppCompatActivity
{
    private TextView txtPath;
    private ListView fileList;
    private LinearLayout saveLayout;
    private EditText etxtSave;

    private String str_filename;
    private String str_root;

    private ArrayList<FileObject> fileObjects;
    private FileObjectAdaptor fileObjectAdaptor;

    private int browserType;
    private int sortType;

    private Context context_this;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_filebrowser);

        context_this = getApplicationContext();

        browserType = getIntent().getIntExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, 0);
        str_root = Environment.getExternalStorageDirectory().getAbsolutePath();
        sortType = Constant.BROWSER_MENU_SORT_ASC;

        txtPath = (TextView)findViewById(R.id.browser_txtPath);
        fileList = (ListView)findViewById(R.id.browser_lvFilecontrol);
        saveLayout = (LinearLayout)findViewById(R.id.browser_saveLayout);
        etxtSave = (EditText)findViewById(R.id.browser_etxtSave);

        if(browserType == Constant.BROWSER_TYPE_OPEN_EXTERNAL)
        {
            setTitle(R.string.filebrowser_name_open);
            saveLayout.setVisibility(View.GONE);
        }
        else if(browserType == Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE)
        {
            setTitle(R.string.filebrowser_name_save);
            saveLayout.setVisibility(View.VISIBLE);
        }

        getDirectory(str_root);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> _parent, View _view, final int _position, long _id)
            {
                final File file = new File(fileObjects.get(_position).url);

                if(file.isDirectory())
                {
                    if(file.canRead())
                    {
                        getDirectory(fileObjects.get(_position).url);
                    }
                }
                else if(file.isFile())
                {
                    if(file.length() >= Constant.TEXTMANAGER_BUFFER)
                    {
                        Intent intent = new Intent();

                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(context_this, MemoActivity.class);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, fileObjects.get(_position).url);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, file.getName());
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_EXTERNAL);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_DIVIDE, true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent();

                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(context_this, MemoActivity.class);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, fileObjects.get(_position).url);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, file.getName());
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_EXTERNAL);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu)
    {
        getMenuInflater().inflate(R.menu.menu_filebrowser, _menu);
        MenuItem item = _menu.findItem(R.id.menu_spinner);
        Spinner menu_spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.menu_spinner_sort, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu_spinner.setAdapter(adapter);
        menu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> _parent, View _view, int _position, long _id)
            {
                if(_position == Constant.BROWSER_MENU_SORT_ASC)
                {
                    sortType = Constant.BROWSER_MENU_SORT_ASC;
                    getDirectory(str_filename);
                }
                else if(_position == Constant.BROWSER_MENU_SORT_DES)
                {
                    sortType = Constant.BROWSER_MENU_SORT_DES;
                    getDirectory(str_filename);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> _parent) {}
        });

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (str_filename.equals(str_root))
        {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        else
        {
            getDirectory(new File(str_filename).getParent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txtPath = null;
        fileList = null;
        saveLayout = null;
        etxtSave = null;
        if(!fileObjects.isEmpty())
            fileObjects.clear();
        fileObjects = null;
        fileObjectAdaptor = null;
        if(dialog != null)
            dialog = null;
        context_this = null;
    }

    public void onClick(View _v)
    {
        if(_v.getId() == R.id.browser_btnSave)
        {
            Pattern pattern = Pattern.compile(Constant.REGEX);
            if(!etxtSave.getText().toString().equals("") && pattern.matcher(etxtSave.getText().toString()).matches())
            {
                if(!isExist(etxtSave.getText().toString()))
                {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL, str_filename + File.separator);
                    intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL, etxtSave.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    dialog = new AlertDialog.Builder(FileBrowserActivity.this);
                    dialog.setTitle(R.string.filebrowser_dialog_exist_title);
                    dialog.setMessage(R.string.filebrowser_dialog_exist_context);
                    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which)
                        {
                            if(_which == AlertDialog.BUTTON_POSITIVE)
                            {
                                Intent intent = new Intent();
                                intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL, str_filename + File.separator);
                                intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL, etxtSave.getText().toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else if(_which == AlertDialog.BUTTON_NEGATIVE)
                            {
                                etxtSave.setText("");
                            }
                            _dialog.dismiss();
                        }
                    };
                    dialog.setPositiveButton(R.string.filebrowser_dialog_exist_overwrite, clickListener);
                    dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
                    dialog.show();
                }
            }
            else
            {
                Toast.makeText(this, R.string.filebrowser_toast_error_regex, Toast.LENGTH_SHORT).show();
                etxtSave.setText("");
            }
        }
    }

    public void getDirectory(final String _dir)
    {
        if(fileObjects != null && !fileObjects.isEmpty())
        {
            fileObjects.clear();
        }
        fileObjects = new ArrayList<>();

        File file = new File(_dir);
        File files[];
        if(browserType == Constant.BROWSER_TYPE_OPEN_EXTERNAL)
        {
            files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File _pathname)
                {
                    String name = _pathname.getName();
                    return _pathname.isDirectory() || name.endsWith(Constant.FILE_TEXT_EXTENSION);
                }
            });
        }
        else if(browserType == Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE)
        {
            files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File _pathname)
                {
                    return _pathname.isDirectory();
                }
            });
        }
        else
        {
            files = null;
        }

        if(sortType == Constant.BROWSER_MENU_SORT_ASC)
        {
            sortFileArray(files, sortType);
        }
        else if(sortType == Constant.BROWSER_MENU_SORT_DES)
        {
            sortFileArray(files, sortType);
        }

        if(files != null)
        {
            for(int i = 0; i < files.length; i++)
            {
                fileObjects.add(new FileObject(files[i]));
            }
        }

        txtPath.setText(getResources().getString(R.string.filebrowser_Location) + " " + _dir);

        if(fileObjectAdaptor != null)
            fileObjectAdaptor = null;

        fileObjectAdaptor = new FileObjectAdaptor(this, fileObjects);
        fileList.setAdapter(fileObjectAdaptor);
        str_filename = _dir;
    }

    private void sortFileArray(File[] _files, final int _sortType)
    {
        Arrays.sort(_files, new Comparator<File>() {
            @Override
            public int compare(File _o1, File _o2)
            {
                if(_sortType == Constant.BROWSER_MENU_SORT_ASC)
                {
                    return (_o1.getName().compareTo(_o2.getName()));
                }
                else
                {
                    return (_o2.getName().compareTo(_o1.getName()));
                }
            }
        });
    }

    private boolean isExist(final String _filename)
    {
        for(int i = 0; i < fileObjects.size(); i++)
        {
            if(_filename.equals(fileObjects.get(i).name))
            {
                return true;
            }
        }
        return false;
    }
}
