package com.eskeptor.openTextViewer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by eskeptor on 17. 1. 28.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MainActivity extends AppCompatActivity
{
    private long backPressedTime;
    private String curFolderURL;
    private SwipeRefreshLayout refreshLayout;
    private StaggeredGridLayoutManager layoutManager;
    private RecyclerView curFolderGridView;
    private RecyclerViewPadding recyclerViewPadding;
    private ClickAction clickAction;
    private MainFileAdaptor curFileAdapter;
    private ArrayList<MainFile> curFolderFileList;
    private Runnable refreshListRunnable;
    private Drawable folderIcon;
    private AlertDialog.Builder dialog;
    private Context context_this;
    private FloatingActionButton fab;

    private AdView adView;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(context_this, FolderActivity.class);
            startActivityForResult(intent, Constant.REQUEST_CODE_OPEN_FOLDER);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        else if(id == R.id.menu_main_settings)
        {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(context_this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_out_bottom);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constant.REQUEST_CODE_OPEN_FOLDER)
            {
                curFolderURL = data.getStringExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL);
                if(curFolderURL != null)
                {
                    refreshList();
                    curFileAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constant.REQUEST_CODE_APP_PERMISSION_STORAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.main_dialog_restart_title);
                dialog.setMessage(R.string.main_dialog_restart_context);
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case AlertDialog.BUTTON_POSITIVE:
                            {
                                ActivityCompat.finishAffinity(MainActivity.this);
                                System.exit(0);
                                break;
                            }
                        }
                        dialog.dismiss();
                    }
                };
                dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
                dialog.show();
            }
            else
            {
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.main_dialog_restart_title_no);
                dialog.setMessage(R.string.main_dialog_restart_context_no);
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == AlertDialog.BUTTON_POSITIVE)
                        {
                            ActivityCompat.finishAffinity(MainActivity.this);
                            System.exit(0);
                        }
                        dialog.dismiss();
                    }
                };
                dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
                dialog.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_this = getApplicationContext();

        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();

        if(Build.VERSION.SDK_INT >= 21)
        {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white, null);
        }
        else
        {
            folderIcon = getResources().getDrawable(R.drawable.ic_folder_open_white);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(folderIcon);

        curFolderGridView = (RecyclerView)findViewById(R.id.main_curFolderFileList);
        curFolderFileList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_swipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                curFileAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        layoutManager = new StaggeredGridLayoutManager(2,1);


        clickAction = new ClickAction() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if(curFolderFileList.get(position).type == Constant.LISTVIEW_FILE_TYPE_IMAGE)
                {
                    intent.setClass(context_this, PaintActivity.class);
                }
                else
                {
                    intent.setClass(context_this, MemoActivity.class);
                }
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILEURL, curFolderFileList.get(position).url);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FILENAME, curFolderFileList.get(position).title);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_INTERNAL);
                intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, curFolderURL);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }

            @Override
            public void onLongClick(View view, int position) {
                deleteFile(position);
            }
        };
        recyclerViewPadding = new RecyclerViewPadding(10, 5, 5);

        fab = (FloatingActionButton) findViewById(R.id.main_add);
        fab.setOnClickListener(new View.OnClickListener() {
            PopupMenu addFabMenu;
            MenuInflater menuInflater;
            Menu menu;
            @Override
            public void onClick(View view)
            {
                if(addFabMenu == null && menuInflater == null && menu == null)
                {
                    addFabMenu = new PopupMenu(context_this, view);
                    menuInflater = addFabMenu.getMenuInflater();
                    menu = addFabMenu.getMenu();
                    menuInflater.inflate(R.menu.menu_main_add, menu);
                }
                addFabMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.menu_main_add_text)
                        {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setClass(context_this, MemoActivity.class);
                            intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
                            intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, curFolderURL);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        }
                        else
                        {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setClass(context_this, PaintActivity.class);
                            intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_NEW);
                            intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, curFolderURL);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        }
                        return false;
                    }
                });
                addFabMenu.show();
            }
        });

        refreshListRunnable = new Runnable() {
            @Override
            public void run() {
                refreshList();
                curFileAdapter = new MainFileAdaptor(curFolderFileList);
                curFileAdapter.setClickAction(clickAction);
                layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                layoutManager.invalidateSpanAssignments();
                curFolderGridView.setHasFixedSize(true);
                curFolderGridView.setLayoutManager(layoutManager);
                curFolderGridView.setAdapter(curFileAdapter);
                curFolderGridView.addItemDecoration(recyclerViewPadding);
            }
        };
        checkPermission();
    }

    @Override
    public void onBackPressed()
    {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && Constant.WAIT_FOR_SECOND >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(this, R.string.back_press, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE))
        {
            refreshList();
            curFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshLayout = null;
        layoutManager = null;
        curFolderGridView = null;
        clickAction = null;
        curFileAdapter = null;
        if(!curFolderFileList.isEmpty())
            curFolderFileList.clear();
        curFolderFileList = null;
        refreshListRunnable = null;
        folderIcon = null;
        if(dialog != null)
            dialog = null;
        context_this = null;
        if(adView != null)
            adView = null;
        pref = null;
        editor = null;
    }

    private void checkPermission()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_APP_PERMISSION_STORAGE);
                }
                else
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_APP_PERMISSION_STORAGE);
                }
            }
            else
            {
                defaultFolderCheck();
                runOnUiThread(refreshListRunnable);
                checkFirstExcecute();
                adMob();
            }
        }
        else
        {
            defaultFolderCheck();
            runOnUiThread(refreshListRunnable);
            checkFirstExcecute();
            adMob();
        }
    }

    private void defaultFolderCheck()
    {
        // 어플의 기본 폴더 체크
        File file = new File(Constant.APP_INTERNAL_URL);
        if(!file.exists())
        {
            file.mkdir();
        }

        // 어플의 위젯용 폴더 체크
        file = new File(Constant.APP_WIDGET_URL);
        if(!file.exists())
        {
            file.mkdir();
        }

        // 어플의 기본 메모 폴더 체크
        file = new File(Constant.APP_INTERNAL_URL + File.separator+ Constant.FOLDER_DEFAULT_NAME);
        if(!file.exists())
        {
            file.mkdir();
        }
        curFolderURL = file.getPath();
    }

    private void deleteFile(final int index)
    {
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.file_dialog_title_delete);
        dialog.setMessage(R.string.file_dialog_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == AlertDialog.BUTTON_POSITIVE)
                {
                    File file = new File(curFolderFileList.get(index).url);
                    if(file.exists())
                    {
                        if(file.delete())
                        {
                            Toast.makeText(context_this, R.string.file_dialog_toast_delete, Toast.LENGTH_SHORT).show();
                            curFolderFileList.remove(index);
                            curFolderGridView.removeViewAt(index);
                            curFileAdapter.notifyItemRemoved(index);
                            curFileAdapter.notifyItemRangeChanged(index, curFolderFileList.size());
                            curFileAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(context_this, getString(R.string.error_folder_not_exist), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else if(which == AlertDialog.BUTTON_NEGATIVE)
                {

                }
                dialog.dismiss();
            }
        };
        dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
        dialog.setPositiveButton(R.string.folder_dialog_button_delete, clickListener);
        dialog.show();
    }

    private void refreshList()
    {
        curFolderFileList.clear();
        File file = new File(curFolderURL);
        File files[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION) ||
                        pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION);
            }
        });

        sortFileArray(files);

        if(files != null)
        {
            for(int i = 0; i < files.length; i++)
            {
                if(files[i].getName().charAt(0) == 'w')
                    curFolderFileList.add(new MainFile(files[i], getResources().getString(R.string.file_noname), getResources().getString(R.string.file_imagememo),
                            new SimpleDateFormat(getResources().getString(R.string.file_dateformat)), true));
                else
                    curFolderFileList.add(new MainFile(files[i], getResources().getString(R.string.file_noname), getResources().getString(R.string.file_imagememo),
                            new SimpleDateFormat(getResources().getString(R.string.file_dateformat)), false));
            }
        }
    }

    private void checkFirstExcecute()
    {
        if(!pref.getBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_FIRST_EXECUTE))
        {
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.main_dialog_first_title);
            dialog.setMessage(R.string.main_dialog_first_context);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == AlertDialog.BUTTON_POSITIVE)
                    {
                        editor.putBoolean(Constant.APP_FIRST_SETUP_PREFERENCE, Constant.APP_TWICE_EXECUTE);
                        editor.commit();
                    }
                    dialog.dismiss();
                }
            };
            dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
            dialog.show();
        }
        if(!pref.getString(Constant.APP_VERSION_CHECK, "1.0.0").equals(Constant.APP_LASTED_VERSION))
        {
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            String update = "";

            try
            {
                inputStream = getResources().openRawResource(R.raw.update);
                byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                while((i = inputStream.read()) != -1)
                {
                    byteArrayOutputStream.write(i);
                }
                update = byteArrayOutputStream.toString();
            }
            catch (Exception e) { e.printStackTrace(); }
            finally {
                try{byteArrayOutputStream.close();}
                catch (Exception e){e.printStackTrace();}
                try{inputStream.close();}
                catch (Exception e){e.printStackTrace();}
            }

            dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.main_dialog_update_title);
            dialog.setMessage(update);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == AlertDialog.BUTTON_POSITIVE)
                    {
                        editor.putString(Constant.APP_VERSION_CHECK, Constant.APP_LASTED_VERSION);
                        editor.commit();
                    }
                    dialog.dismiss();
                }
            };
            dialog.setPositiveButton(R.string.settings_dialog_info_ok, clickListener);
            dialog.show();
        }
    }

    private void adMob()
    {
        if(pref.getBoolean(Constant.APP_ADMOB_VISIBLE, true))
        {
            MobileAds.initialize(context_this, getResources().getString(R.string.app_id));
            //adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            AdRequest adRequest = new AdRequest.Builder().build();
            adView = (AdView)findViewById(R.id.adView);

            adView.setEnabled(true);
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
            CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0, DPtoPixel(16), DPtoPixel(70));
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            fab.setLayoutParams(layoutParams);
        }
        else
        {
            CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0, DPtoPixel(16), DPtoPixel(16));
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            fab.setLayoutParams(layoutParams);
        }
    }

    private int DPtoPixel(final int DP)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context_this.getResources().getDisplayMetrics());
    }

    private void sortFileArray(File[] files)
    {
        // 최근 날짜순으로 정렬
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2)
            {
                Date d1 = new Date(o1.lastModified());
                Date d2 = new Date(o2.lastModified());
                return d2.compareTo(d1);
            }
        });
    }
}
