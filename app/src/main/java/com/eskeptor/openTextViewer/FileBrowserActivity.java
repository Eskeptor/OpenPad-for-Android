package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.eskeptor.openTextViewer.datatype.FileObject;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

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

// 내장된 파일 브라우저 클래스
public class FileBrowserActivity extends AppCompatActivity {
    private TextView mTxtPath;                       // 현재 파일 경로
    private ListView mFileList;                      // 파일 리스트
    private String mStrFilename;                    // 파일 이름
    private String mStrRoot;                        // 파일의 절대경로
    private ArrayList<FileObject> mFileListObjects;      // 파일의 목록을 저장할 배열리스트
    private FileObjectAdaptor mFileListObjectAdaptor;    // 파일용 커스텀 어댑터
    private Context mContextThis;                   // context
    private View mContextView;
    private int mBrowserType;                        // 외부파일 불러오기, 파일 저장하기
    private Constant.BrowserMenuSortType mSortType;                           // 정렬 기준

    // 메뉴 아이템
    private MenuItem mMenuItemDES;
    private MenuItem mMenuItemASC;

    // 파일을 다른이름으로 다른폴더에 저장할 때 쓰이는 것
    private LinearLayout mSaveLayout;
    private EditText mEditTxtSave;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_filebrowser);

        mContextThis = getApplicationContext();
        mContextView = findViewById(R.id.activity_filebrowser);

        mBrowserType = getIntent().getIntExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, 0);
        mStrRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        mSortType = Constant.BrowserMenuSortType.Asc;

        mTxtPath = (TextView) findViewById(R.id.browser_txtPath);
        mFileList = (ListView) findViewById(R.id.browser_lvFilecontrol);
        mSaveLayout = (LinearLayout) findViewById(R.id.browser_saveLayout);
        mEditTxtSave = (EditText) findViewById(R.id.browser_etxtSave);

        if (mBrowserType == Constant.BROWSER_TYPE_OPEN_EXTERNAL) {
            setTitle(R.string.filebrowser_name_open);
            mSaveLayout.setVisibility(View.GONE);
        } else if (mBrowserType == Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE) {
            setTitle(R.string.filebrowser_name_save);
            mSaveLayout.setVisibility(View.VISIBLE);
        }

        getDirectory(mStrRoot);

        mFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> _parent, View _view, final int _position, long _id) {
                final File file = new File(mFileListObjects.get(_position).mFilePath);

                if (file.isDirectory()) {
                    if (file.canRead()) {
                        getDirectory(mFileListObjects.get(_position).mFilePath);
                    }
                } else if (file.isFile()) {
                    if (file.length() >= Constant.TEXTMANAGER_BUFFER) {
                        Intent intent = new Intent();

                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(mContextThis, MemoActivity.class);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, mFileListObjects.get(_position).mFilePath);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, file.getName());
                        //intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_EXTERNAL);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_DIVIDE, true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        finish();
                    } else {
                        Intent intent = new Intent();

                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(mContextThis, MemoActivity.class);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, mFileListObjects.get(_position).mFilePath);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, file.getName());
                        //intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_EXTERNAL);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        finish();
                    }
                }
            }
        });

        SharedPreferences sharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        int font = sharedPref.getInt(Constant.APP_FONT, Constant.FONT_DEFAULT);
        switch (font) {
            case Constant.FONT_DEFAULT:
                Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
                break;
            case Constant.FONT_BAEDAL_JUA:
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"));
                break;
            case Constant.FONT_KOPUB_DOTUM:
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"));
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_filebrowser, _menu);
        mMenuItemDES = _menu.findItem(R.id.menu_des);
        mMenuItemASC = _menu.findItem(R.id.menu_asc);
        mMenuItemASC.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        int id = _item.getItemId();

        switch (id) {
            case R.id.menu_asc:
                mSortType = Constant.BrowserMenuSortType.Asc;
                getDirectory(mStrFilename);
                mMenuItemASC.setChecked(true);
                mMenuItemDES.setChecked(false);
                break;
            case R.id.menu_des:
                mSortType = Constant.BrowserMenuSortType.Des;
                getDirectory(mStrFilename);
                mMenuItemASC.setChecked(false);
                mMenuItemDES.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onBackPressed() {
        if (mStrFilename.equals(mStrRoot)) {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        } else {
            getDirectory(new File(mStrFilename).getParent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTxtPath = null;
        mFileList = null;
        mSaveLayout = null;
        mEditTxtSave = null;
        if (!mFileListObjects.isEmpty())
            mFileListObjects.clear();
        mFileListObjects = null;
        mFileListObjectAdaptor = null;
        mContextThis = null;
        mStrFilename = null;
        mStrRoot = null;
        mContextView = null;
    }

    public void onClick(View _v) {
        if (_v.getId() == R.id.browser_btnSave) {
            Pattern pattern = Pattern.compile(Constant.REGEX);
            if (!mEditTxtSave.getText().toString().equals("") && pattern.matcher(mEditTxtSave.getText().toString()).matches()) {
                if (!isExist(mEditTxtSave.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL, mStrFilename + File.separator);
                    intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL, mEditTxtSave.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(FileBrowserActivity.this);
                    dialog.setTitle(R.string.filebrowser_dialog_exist_title);
                    dialog.setMessage(R.string.filebrowser_dialog_exist_context);
                    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            if (_which == AlertDialog.BUTTON_POSITIVE) {
                                Intent intent = new Intent();
                                intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FOLDERURL, mStrFilename + File.separator);
                                intent.putExtra(Constant.INTENT_EXTRA_MEMO_SAVE_FILEURL, mEditTxtSave.getText().toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (_which == AlertDialog.BUTTON_NEGATIVE) {
                                mEditTxtSave.setText("");
                            }
                            _dialog.dismiss();
                        }
                    };
                    dialog.setPositiveButton(R.string.filebrowser_dialog_exist_overwrite, clickListener);
                    dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
                    dialog.show();
                }
            } else {
                Snackbar.make(mContextView, R.string.filebrowser_toast_error_regex, Snackbar.LENGTH_SHORT).show();
                mEditTxtSave.setText("");
            }
        }
    }

    /**
     * 파일 디렉토리를 가져옵니다.
     * @param _dir 경로
     */
    public void getDirectory(final String _dir) {
        if (mFileListObjects != null && !mFileListObjects.isEmpty()) {
            mFileListObjects.clear();
        }
        mFileListObjects = new ArrayList<>();

        File file = new File(_dir);
        File files[];

        switch (mBrowserType) {
            case Constant.BROWSER_TYPE_OPEN_EXTERNAL:
                files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File _pathname) {
                        String name = _pathname.getName();
                        return _pathname.isDirectory() || name.endsWith(Constant.FILE_TEXT_EXTENSION);
                    }
                });
                break;
            case Constant.BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE:
                files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File _pathname) {
                        return _pathname.isDirectory();
                    }
                });
                break;
            default:
                files = null;
                break;
        }

        sortFileArray(files, mSortType);

        if (files != null) {
            for (File newFile : files) {
                mFileListObjects.add(new FileObject(newFile));
            }
        }

        mTxtPath.setText(getResources().getString(R.string.filebrowser_Location) + " " + _dir);

        if (mFileListObjectAdaptor != null)
            mFileListObjectAdaptor = null;

        mFileListObjectAdaptor = new FileObjectAdaptor(this, mFileListObjects);
        mFileList.setAdapter(mFileListObjectAdaptor);
        mStrFilename = _dir;
    }

    /**
     * 파일 디렉토리를 정렬하여 보여줍니다.(오름차, 내림차)
     * @param _files 파일 디렉토리
     * @param _sortType 정렬 기준
     */
    private void sortFileArray(File[] _files, final Constant.BrowserMenuSortType _sortType) {
        Arrays.sort(_files, new Comparator<File>() {
            @Override
            public int compare(File _o1, File _o2) {
                switch (_sortType) {
                    case Asc:
                        return (_o1.getName().compareTo(_o2.getName()));
                    default:
                        return (_o2.getName().compareTo(_o1.getName()));
                }
            }
        });
    }

    /**
     * 해당 경로에 파일이 존재하는 여부를 반환합니다.
     * @param _filename 파일이름
     * @return 있다 혹은 없다
     */
    private boolean isExist(final String _filename) {
        for (int i = 0; i < mFileListObjects.size(); i++) {
            if (_filename.equals(mFileListObjects.get(i).mFileName)) {
                return true;
            }
        }
        return false;
    }
}
