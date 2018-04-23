package com.eskeptor.openTextViewer;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.RelativeLayout;

import com.eskeptor.openTextViewer.datatype.MainFileObject;
import com.eskeptor.openTextViewer.textManager.RawTextManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


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
    private long mBackPressedTime;                              // Back Button Press Time
    private String mCurFolderPath;                              // Current folder path
    private SwipeRefreshLayout mRefreshLayout;                  // RefreshLayout
    private StaggeredGridLayoutManager mLayoutManager;          // StaggeredGridLayoutManager
    private RecyclerView mCurFolderGridView;                    // RecyclerView
    private MainFileAdaptor mCurFileAdapter;                    // Main List FileAdapter
    private ArrayList<MainFileObject> mCurFolderFileList;       // Main List's arrayList
    private Context mContextThis;                               // This view's context(getApplicationContext())
    private View mContextView;                                  // This view

    private static SharedPreferences mSharedPref;               // SharedPreferences
    private static SharedPreferences.Editor mSharedPrefEditor;  // SharedPreferences Editor

    private int mFontStyle;                                     // Current Font Style
    private int mPrevFontStyle;                                 // Previous Font Style
    private boolean mIsViewImage;                               // Whether to enable image preview

    private RefreshListHandler mHandler;                        // Main List refresh handler
    private Thread mListThread;                                 // Main List's thread

    private FloatingActionsMenu mActionMenu;

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
                    mCurFolderPath = _data.getStringExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL);
                    if (mCurFolderPath != null) {
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

        mHandler = new RefreshListHandler(this);

        // Folder Icon
        Drawable folderIcon;
        if (Build.VERSION.SDK_INT >= 21) {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white, null);
        } else {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white);
        }

        // Replace home button on toolbar with folder view icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(folderIcon);
        }
        // Transparent StatusBar Color
        setTranslucentStatusBar(getWindow());

        mCurFolderGridView = findViewById(R.id.main_curFolderFileList);
        mCurFolderFileList = new ArrayList<>();
        mRefreshLayout = findViewById(R.id.main_swipeRefresh);
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
                        mActionMenu.collapse();
                    }
                }, 500);
            }
        });
        mLayoutManager = new StaggeredGridLayoutManager(2, 1);

        // FloatingActionMenu
        mActionMenu = findViewById(R.id.main_memo);

        // FloatingActionButton - Add Text
        final FloatingActionButton btnAddText = findViewById(R.id.main_memo_text);
        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(mContextThis, MemoActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderPath);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        // FloatingActionButton - Add Paint memo
        final FloatingActionButton btnAddPaint = findViewById(R.id.main_memo_paint);
        btnAddPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(R.string.main_dialog_restart_title_no);
                dialog.setMessage(R.string.main_dialog_image_alert);
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(mContextThis, PaintActivity.class);
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderPath);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        dialog.dismiss();
                    }
                };
                dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
                dialog.show();
            }
        });

        // Tutorial Page
        if (!mSharedPref.getBoolean(Constant.APP_TUTORIAL, false)) {
            Intent intent = new Intent();
            intent.setClass(mContextThis, FirstStartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        // Font Settings
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
        if (mActionMenu.isExpanded()) {
            mActionMenu.collapse();
        }
        mIsViewImage = mSharedPref.getBoolean(Constant.APP_VIEW_IMAGE, true);
        mFontStyle = mSharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());

        if (mSharedPref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE)) {
            if (mCurFolderPath != null) {
                File folderCheck = new File(mCurFolderPath);
                if (!folderCheck.exists()) {
                    mCurFolderPath = Constant.APP_INTERNAL_URL + File.separator + Constant.FOLDER_DEFAULT_NAME;
                }
                if (mCurFileAdapter != null) {
                    refreshList();
                    mCurFileAdapter.notifyDataSetChanged();
                }
            }
        }

        // If the font is different from the previous one, the font has changed.
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
        mCurFolderPath = null;
        mContextView = null;
        mHandler = null;
        if (mListThread != null) {
            mListThread.interrupt();
        }
        mListThread = null;
    }

    /**
     * Default Folder Check Method
     */
    private void defaultFolderCheck() {
        // Check default folder for apps
        File file = new File(Constant.APP_INTERNAL_URL);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "기본폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "기본폴더 생성불가");
            }
        }

        // Check the widget folder for application
        file = new File(Constant.APP_WIDGET_URL);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "위젯폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "위젯폴더 생성불가");
            }
        }

        // Check the default memo folder for the app
        file = new File(Constant.APP_INTERNAL_URL + File.separator + Constant.FOLDER_DEFAULT_NAME);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "기본메모폴더 생성");
            } else {
                TestLog.Tag("MainActivity").Logging(TestLog.LogType.ERROR, "기본메모폴더 생성불가");
            }
        }
        mCurFolderPath = file.getPath();
        TestLog.Tag("MainActivity").Logging(TestLog.LogType.DEBUG, "mCurFolderPath: " + mCurFolderPath);
    }

    /**
     * How to remove files from the main list
     * @param _index Index of files to remove
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
     * How to update the main list
     */
    private void refreshList() {
        mCurFolderFileList.clear();
        File file = new File(mCurFolderPath);
        File files[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File _pathname) {
                return _pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION) ||
                        _pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION);
            }
        });

        if(file.exists())
            TestLog.Tag("Main").Logging(TestLog.LogType.ERROR, file.getName() + " is exist");
        if(files == null) {
            TestLog.Tag("Main").Logging(TestLog.LogType.ERROR, "null");
        }
        else {
            sortFileArray(files);
            for (File newFile : files) {
                mCurFolderFileList.add(new MainFileObject(newFile, getResources().getString(R.string.file_noname), getResources().getString(R.string.file_imagememo),
                        Locale.getDefault().getDisplayCountry(), mIsViewImage));
            }
        }
    }

    /**
     * How to Determine the Initial Installation
     */
    private void checkFirstExcecute() {
        if (!mSharedPref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.main_dialog_first_title);
            dialog.setMessage(R.string.main_dialog_first_context);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface _dialog, int _which) {
                    if (_which == AlertDialog.BUTTON_POSITIVE) {
                        if (mSharedPrefEditor == null)
                            mSharedPrefEditor = mSharedPref.edit();
                        mSharedPrefEditor.putBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_TWICE_EXECUTE);
                        mSharedPrefEditor.apply();

                        AlertDialog.Builder restart = new AlertDialog.Builder(MainActivity.this);
                        restart.setTitle(R.string.main_dialog_restart_title);
                        restart.setMessage(R.string.main_dialog_restart_context);
                        restart.setPositiveButton(R.string.settings_dialog_info_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mStartActivity = new Intent(getBaseContext(), MainActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
                                if (mgr != null) {
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                }
                                System.exit(0);
                                dialog.dismiss();
                            }
                        });
                        restart.show();
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
     * AdMob
     */
    private void adMob() {
        if (mSharedPref.getBoolean(Constant.APP_ADMOB_VISIBLE, true)) {
            MobileAds.initialize(mContextThis, getResources().getString(R.string.app_id));
            //adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = findViewById(R.id.adView);
            adView.setEnabled(true);
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.adView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
            } else {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            }
            mActionMenu.setLayoutParams(layoutParams);
        } else {
            AdView adView = (AdView) findViewById(R.id.adView);
            adView.setEnabled(false);
            adView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
            } else {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            }
            mActionMenu.setLayoutParams(layoutParams);
        }
    }

    /**
     * How to sort the main list
     * @param _files list
     */
    private void sortFileArray(File[] _files) {
        // Sort by most recent date
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
                        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, mCurFolderPath);
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

    static class RefreshListHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        RefreshListHandler(MainActivity _activity) {
            mActivity = new WeakReference<>(_activity);
        }

        @Override
        public void handleMessage(Message _msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(_msg);
            }
        }
    }


    /**
     * Transparency or colorization of StatusBar in Coordinatorlayout
     * 출처: https://stackoverflow.com/questions/33668668/coordinatorlayout-not-drawing-behind-status-bar-even-with-windowtranslucentstatu
     * @param _window window
     */
    private static void setTranslucentStatusBar(Window _window) {
        if (_window == null) {
            return;
        }
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(_window);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(_window);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setTranslucentStatusBarLollipop(Window _window) {
        _window.setStatusBarColor(
                _window.getContext()
                        .getResources()
                        .getColor(R.color.colorPrimaryDark));
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatusBarKiKat(Window _window) {
        _window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}
