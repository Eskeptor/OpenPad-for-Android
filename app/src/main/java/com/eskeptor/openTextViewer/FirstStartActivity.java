package com.eskeptor.openTextViewer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Tutorial Page Class
 */
public class FirstStartActivity extends AppCompatActivity {
    private ViewPager mPager;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEditor;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUEST_CODE_APP_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mPrefEditor = mPref.edit();
                mPrefEditor.putBoolean(Constant.APP_TUTORIAL, true);
                mPrefEditor.apply();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.main_dialog_restart_title_no);
                dialog.setMessage(R.string.main_dialog_restart_context_no);
                dialog.setPositiveButton(R.string.settings_dialog_info_ok, null);
                dialog.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        mPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);

        mPager = findViewById(R.id.first_viewPager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPager = null;
        mPrefEditor = null;
        mPref = null;
    }

    @Override
    public void onBackPressed() {

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_APP_PERMISSION_STORAGE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_APP_PERMISSION_STORAGE);
                }
            }
        }
    }

    private class PagerAdapterClass extends PagerAdapter {
        private final int VIEW_SIZE = 6;
        private final int[] IMAGE_ID = {R.drawable.firstview1, R.drawable.firstview2, R.drawable.firstview3, R.drawable.firstview4,
                R.drawable.firstview5, R.drawable.firstview6};
        private LayoutInflater mInflater;

        public PagerAdapterClass(Context context) {
            super();
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return VIEW_SIZE;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;

            switch (position) {
                case 0: {
                    view = mInflater.inflate(R.layout.first_view1, null);
                    ImageView imageView = view.findViewById(R.id.first_img1);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 1: {
                    view = mInflater.inflate(R.layout.first_view2, null);
                    ImageView imageView = view.findViewById(R.id.first_img2);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 2: {
                    view = mInflater.inflate(R.layout.first_view3, null);
                    ImageView imageView = view.findViewById(R.id.first_img3);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 3: {
                    view = mInflater.inflate(R.layout.first_view4, null);
                    ImageView imageView = view.findViewById(R.id.first_img4);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                }
                case 4: {
                    view = mInflater.inflate(R.layout.first_view5, null);
                    ImageView imageView = view.findViewById(R.id.first_img5);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 5: {
                    view = mInflater.inflate(R.layout.first_view6, null);
                    ImageView imageView = view.findViewById(R.id.first_img6);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    view.findViewById(R.id.first_btnStart).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermission();
                        }
                    });
                    container.addView(view);
                    break;
                }
            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}

