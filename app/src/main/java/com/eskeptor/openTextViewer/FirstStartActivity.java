package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
 * 튜토리얼 페이지
 */
public class FirstStartActivity extends AppCompatActivity {
    private ViewPager mPager;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        mPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);

        mPager = (ViewPager)findViewById(R.id.first_viewPager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPager = null;
        mPrefEditor = null;
        mPref = null;
        System.gc();
    }

    private class PagerAdapterClass extends PagerAdapter {
        private final int VIEW_SIZE = 6;
        private final int[] IMAGE_ID = {R.drawable.firstview1, R.drawable.firstview2, R.drawable.firstview3, R.drawable.firstview4,
                R.drawable.firstview5, R.drawable.firstview6};
        private LayoutInflater mInflater;

        public PagerAdapterClass(Context _context) {
            super();
            mInflater = LayoutInflater.from(_context);
        }

        @Override
        public int getCount() {
            return VIEW_SIZE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            switch (position) {
                case 0: {
                    view = mInflater.inflate(R.layout.first_view1, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img1);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 1: {
                    view = mInflater.inflate(R.layout.first_view2, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img2);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 2: {
                    view = mInflater.inflate(R.layout.first_view3, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img3);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 3: {
                    view = mInflater.inflate(R.layout.first_view4, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img4);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                }
                case 4: {
                    view = mInflater.inflate(R.layout.first_view5, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img5);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    container.addView(view);
                    break;
                }
                case 5: {
                    view = mInflater.inflate(R.layout.first_view6, null);
                    ImageView imageView = (ImageView)view.findViewById(R.id.first_img6);
                    Glide.with(view).load(IMAGE_ID[position]).into(imageView);
                    view.findViewById(R.id.first_btnStart).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPrefEditor = mPref.edit();
                            mPrefEditor.putBoolean(Constant.APP_TUTORIAL, true);
                            mPrefEditor.apply();
                            finish();
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
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}

