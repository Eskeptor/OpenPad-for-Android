package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.eskeptor.openTextViewer.datatype.MainFileObject;
import com.eskeptor.openTextViewer.textManager.RawTextManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import util.TestLog;

/**
 * Created by eskeptor on 17. 1. 28.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MainActivity extends AppCompatActivity {
    private long mBackPressedTime;
    private String mCurFolderURL;
    private SwipeRefreshLayout mRefreshLayout;
    private StaggeredGridLayoutManager mLayoutManager;
    private RecyclerView mCurFolderGridView;
    private MainFileAdaptor mCurFileAdapter;
    private ArrayList<MainFileObject> mCurFolderFileList;
    private Context mContextThis;
    private View mContextView;
    private FloatingActionButton mFloatingActionButton;

    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mSharedPrefEditor;

    private int mFontStyle;
    private int mPrevFontStyle;
    private boolean mIsViewImage;

    private RefreshList mHandler;
    private Thread mListThread;

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_main, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        int id = _item.getItemId();
        switch (id) {
            case android.R.id.home: {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(mContextThis, FolderActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_OPEN_FOLDER);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
            }
            case R.id.menu_main_settings: {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(mContextThis, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_out_bottom);
                break;
            }
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        if (_resultCode == RESULT_OK) {
            switch (_requestCode) {
                case Constant.REQUEST_CODE_OPEN_FOLDER: {
                    mCurFolderURL = _data.getStringExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL);
                    if (mCurFolderURL != null) {
                        refreshList();
                        mCurFileAdapter.notifyDataSetChanged();
                    }
                    break;
                }
                case Constant.REQUEST_CODE_PERMISSION_GRANT: {
                    if (mListThread != null) {
                        mListThread.interrupt();
                    }
                    mListThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            defaultFolderCheck();
                            refreshList();
                            mHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_LIST);
                        }
                    });
                    mListThread.start();
                    checkFirstExcecute();
                    adMob();
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_main);

        mContextThis = getApplicationContext();
        mContextView = findViewById(R.id.content_main);

        mSharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        mFontStyle = mSharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());
        mPrevFontStyle = mFontStyle;
        mIsViewImage = mSharedPref.getBoolean(Constant.APP_VIEW_IMAGE, true);

        mHandler = new RefreshList(this);

        // 폴더 아이콘
        Drawable folderIcon;
        if (Build.VERSION.SDK_INT >= 21) {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white, null);
        } else {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white);
        }

        // 툴바의 홈버튼을 폴더보기 아이콘으로 교체
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(folderIcon);
        }

        mCurFolderGridView = (RecyclerView) findViewById(R.id.main_curFolderFileList);
        mCurFolderFileList = new ArrayList<>();
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipeRefresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurFileAdapter != null) {
                            mCurFileAdapter.notifyDataSetChanged();
                        }
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
        mLayoutManager = new StaggeredGridLayoutManager(2, 1);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.main_add);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            Context wrapper = new ContextThemeWrapper(mContextThis, R.style.AppTheme);
            PopupMenu addFabMenu;
            MenuInflater menuInflater;
            Menu menu;

            @Override
            public void onClick(View view) {
                if (addFabMenu == null && menuInflater == null && menu == null) {
                    addFabMenu = new PopupMenu(wrapper, view);
                    menuInflater = addFabMenu.getMenuInflater();
                    menu = addFabMenu.getMenu();
                    menuInflater.inflate(R.menu.menu_main_add, menu);
                }
                addFabMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem _item) {
                        int id = _item.getItemId();
                        switch (id) {
                            case R.id.menu_main_add_text: {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setClass(mContextThis, MemoActivity.class);
                                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderURL);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                                break;
                            }
                            default: {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog.setTitle(R.string.main_dialog_restart_title_no);
                                dialog.setMessage(R.string.main_dialog_image_alert);
                                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setClass(mContextThis, PaintActivity.class);
                                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderURL);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                                        dialog.dismiss();
                                    }
                                };
                                dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
                                dialog.show();
                                break;
                            }
                        }
                        return false;
                    }
                });
                addFabMenu.show();
            }
        });

        // 튜토리얼 페이지
        if (!mSharedPref.getBoolean(Constant.APP_TUTORIAL, false)) {
            Intent intent = new Intent();
            intent.setClass(mContextThis, FirstStartActivity.class);
            startActivityForResult(intent, Constant.REQUEST_CODE_PERMISSION_GRANT);
        } else {
            if (mListThread != null) {
                mListThread.interrupt();
            }
            mListThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    defaultFolderCheck();
                    refreshList();
                    mHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_LIST);
                }
            });
            mListThread.start();
            checkFirstExcecute();
            adMob();
        }

        // 설정의 폰트에 맞게 폰트 변환
        if (mFontStyle == Constant.FontType.BaeDal_JUA.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"))
                    .addBold(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"));
        } else if (mFontStyle == Constant.FontType.KOPUB_Dotum.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"))
                    .addBold(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"));
        } else {
            Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - mBackPressedTime;

        if (0 <= intervalTime && Constant.WAIT_FOR_SECOND >= intervalTime) {
            super.onBackPressed();
        } else {
            mBackPressedTime = tempTime;
            Snackbar.make(mContextView, R.string.back_press, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsViewImage = mSharedPref.getBoolean(Constant.APP_VIEW_IMAGE, true);
        mFontStyle = mSharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());

        if (mSharedPref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE)) {
            if (mCurFolderURL != null) {
                File folderCheck = new File(mCurFolderURL);
                if (!folderCheck.exists()) {
                    mCurFolderURL = Constant.APP_INTERNAL_URL + File.separator + Constant.FOLDER_DEFAULT_NAME;
                }
                if (mCurFileAdapter != null) {
                    refreshList();
                    mCurFileAdapter.notifyDataSetChanged();
                }
            }
        }

        if(mPrevFontStyle != mFontStyle) {
            mPrevFontStyle = mFontStyle;
            if (mFontStyle == Constant.FontType.BaeDal_JUA.getValue()) {
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"));
            } else if (mFontStyle == Constant.FontType.KOPUB_Dotum.getValue()) {
                Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"))
                        .addBold(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"));
            } else {
                Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
            }
            recreate();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefreshLayout = null;
        mLayoutManager = null;
        mCurFolderGridView = null;
        mCurFileAdapter = null;
        if (!mCurFolderFileList.isEmpty()) {
            mCurFolderFileList.clear();
        }
        mCurFolderFileList = null;
        mContextThis = null;
        mSharedPref = null;
        mSharedPrefEditor = null;
        mCurFolderURL = null;
        mFloatingActionButton = null;
        mContextView = null;
        mHandler = null;
        if (mListThread != null) {
            mListThread.interrupt();
        }
        mListThread = null;
    }

    /**
     * 기본 폴더 체크 메소드
     */
    private void defaultFolderCheck() {
        // 어플의 기본 폴더 체크
        File file = new File(Constant.APP_INTERNAL_URL);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "기본폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "기본폴더 생성불가");
            }
        }

        // 어플의 위젯용 폴더 체크
        file = new File(Constant.APP_WIDGET_URL);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "위젯폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "위젯폴더 생성불가");
            }
        }

        // 어플의 기본 메모 폴더 체크
        file = new File(Constant.APP_INTERNAL_URL + File.separator + Constant.FOLDER_DEFAULT_NAME);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "기본메모폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "기본메모폴더 생성불가");
            }
        }
        mCurFolderURL = file.getPath();
        TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "mCurFolderURL: " + mCurFolderURL);
    }

    /**
     * 메인 리스트에서 파일을 제거하는 메소드
     * @param _index 제거할 파일의 인덱스
     */
    private void deleteFile(final int _index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.file_dialog_title_delete);
        dialog.setMessage(R.string.file_dialog_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                if (_which == AlertDialog.BUTTON_POSITIVE) {
                    File file = new File(mCurFolderFileList.get(_index).mFilePath);
                    if (file.exists()) {
                        if (file.delete()) {
                            if (mCurFolderFileList.get(_index).mFileType == Constant.FileType.Image) {
                                File imageSummary = new File(file.getPath() + Constant.FILE_IMAGE_SUMMARY);
                                if (imageSummary.delete()) {
                                    TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "이미지 요약 제거");
                                } else {
                                    TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "이미지 요약 제거불가");
                                }
                            }
                            mCurFolderFileList.remove(_index);
                            mCurFolderGridView.removeViewAt(_index);
                            mCurFileAdapter.notifyItemRemoved(_index);
                            mCurFileAdapter.notifyItemRangeChanged(_index, mCurFolderFileList.size());
                            mCurFileAdapter.notifyDataSetChanged();
                            Snackbar.make(mContextView, R.string.file_dialog_toast_delete, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mContextView, R.string.error_folder_not_exist, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                _dialog.dismiss();
            }
        };
        dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
        dialog.setPositiveButton(R.string.folder_dialog_button_delete, clickListener);
        dialog.show();
    }

    /**
     * 메인 리스트를 갱신하는 메소드
     */
    private void refreshList() {
        mCurFolderFileList.clear();
        File file = new File(mCurFolderURL);
        File files[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File _pathname) {
                return _pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION) ||
                        _pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION);
            }
        });

        if(file.exists())
            TestLog.Tag("Main").Logging(TestLog.LogType.ERROR, file.getName() + " is exist");
        if(files == null)
            TestLog.Tag("Main").Logging(TestLog.LogType.ERROR, "null");

        sortFileArray(files);

        if (files != null) {
            for (File newFile : files) {
                mCurFolderFileList.add(new MainFileObject(newFile, getResources().getString(R.string.file_noname), getResources().getString(R.string.file_imagememo),
                        Locale.getDefault().getDisplayCountry(), mIsViewImage));
            }
        }
    }

    /**
     * 초기 설치 여부를 판단하는 메소드
     */
    private void checkFirstExcecute() {
        if (!mSharedPref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.main_dialog_first_title);
            dialog.setMessage(R.string.main_dialog_first_context);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    if (_which == AlertDialog.BUTTON_POSITIVE) {
                        if (mSharedPrefEditor == null)
                            mSharedPrefEditor = mSharedPref.edit();
                        mSharedPrefEditor.putBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_TWICE_EXECUTE);
                        mSharedPrefEditor.apply();
                    }
                    _dialog.dismiss();
                }
            };
            dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
            dialog.show();
        }

        if (!mSharedPref.getString(Constant.APP_VERSION_CHECK, "1.0.0").equals(Constant.APP_LASTED_VERSION)) {
            String update = RawTextManager.getRawText(mContextThis, R.raw.update);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.main_dialog_update_title);
            dialog.setMessage(update);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int _which) {
                    if (_which == AlertDialog.BUTTON_POSITIVE) {
                        if (mSharedPrefEditor == null)
                            mSharedPrefEditor = mSharedPref.edit();
                        mSharedPrefEditor.putString(Constant.APP_VERSION_CHECK, Constant.APP_LASTED_VERSION);
                        mSharedPrefEditor.apply();
                    }
                    _dialog.dismiss();
                }
            };
            dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
            dialog.show();
        }
    }

    /**
     * 애드몹
     */
    private void adMob() {
        if (mSharedPref.getBoolean(Constant.APP_ADMOB_VISIBLE, true)) {
            MobileAds.initialize(mContextThis, getResources().getString(R.string.app_id));
            //adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = (AdView) findViewById(R.id.adView);

            adView.setEnabled(true);
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
            CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, DPtoPixel(16), DPtoPixel(70));
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            mFloatingActionButton.setLayoutParams(layoutParams);
        } else {
            CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, DPtoPixel(16), DPtoPixel(16));
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            mFloatingActionButton.setLayoutParams(layoutParams);
        }
    }

    /**
     * DP 단위를 Pixel 단위로 변경시켜주는 메소드
     * @param _DP DP
     * @return Pixel 값
     */
    private int DPtoPixel(final int _DP) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _DP, mContextThis.getResources().getDisplayMetrics());
    }

    /**
     * 메인 리스트를 정렬하는 메소드
     * @param _files 리스트
     */
    private void sortFileArray(File[] _files) {
        // 최근 날짜순으로 정렬
        Arrays.sort(_files, new Comparator<File>() {
            @Override
            public int compare(File _o1, File _o2) {
                Date d1 = new Date(_o1.lastModified());
                Date d2 = new Date(_o2.lastModified());
                return d2.compareTo(d1);
            }
        });
    }

    private void handleMessage(Message _message) {
        int what = _message.what;
        switch (what) {
            case Constant.HANDLER_REFRESH_LIST: {
                mCurFileAdapter = new MainFileAdaptor(mCurFolderFileList, mSharedPref);
                mCurFileAdapter.setClickAction(new ClickAction() {
                    @Override
                    public void onClick(View _view, int _position) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (mCurFolderFileList.get(_position).mFileType == Constant.FileType.Image) {
                            intent.setClass(mContextThis, PaintActivity.class);
                        } else {
                            intent.setClass(mContextThis, MemoActivity.class);
                        }
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, mCurFolderFileList.get(_position).mFilePath);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, mCurFolderFileList.get(_position).mFileTitle);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderURL);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                    }

                    @Override
                    public void onLongClick(View _view, int _position) {
                        deleteFile(_position);
                    }
                });
                mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mLayoutManager.invalidateSpanAssignments();
                mCurFolderGridView.setHasFixedSize(true);
                mCurFolderGridView.setLayoutManager(mLayoutManager);
                mCurFolderGridView.setAdapter(mCurFileAdapter);
                mCurFolderGridView.addItemDecoration(new RecyclerViewPadding(10, 5, 5));
                break;
            }
        }
    }

    static class RefreshList extends Handler {
        private final WeakReference<MainActivity> mActivity;
        RefreshList(MainActivity _activity) {
            mActivity = new WeakReference<>(_activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
