package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class FirstStartActivity extends AppCompatActivity {
    private ViewPager mPager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);

        mPager = (ViewPager)findViewById(R.id.first_viewPager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor = pref.edit();
            editor.putBoolean(Constant.APP_TUTORIAL, true);
            editor.apply();
            editor.commit();
            finish();
        }
    };

    private class PagerAdapterClass extends PagerAdapter {
        private LayoutInflater mInflater;

        public PagerAdapterClass(final Context _context) {
            super();
            mInflater = LayoutInflater.from(_context);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            switch (position) {
                case 0:
                    view = mInflater.inflate(R.layout.first_view1, null);
                    view.findViewById(R.id.first_v1_txt);
                    break;
                case 1:
                    view = mInflater.inflate(R.layout.first_view2, null);
                    view.findViewById(R.id.first_v2_txt);
                    break;
                case 2:
                    view = mInflater.inflate(R.layout.first_view3, null);
                    view.findViewById(R.id.first_v3_txt);
                    view.findViewById(R.id.first_btnStart).setOnClickListener(mClickListener);
                    break;
            }
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public Parcelable saveState() {
            return super.saveState();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
