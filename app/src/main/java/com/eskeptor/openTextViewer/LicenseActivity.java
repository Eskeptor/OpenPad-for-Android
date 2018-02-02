package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import com.eskeptor.openTextViewer.textManager.RawTextManager;

/**
 * 라이센스 페이지
 */
public class LicenseActivity extends AppCompatActivity {
    private Thread mLicense1;
    private Thread mLicense2;
    private Thread mLicense3;
    private Thread mLicense4;
    private Context mContextThis;
    private ScrollView mScrollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        mContextThis = getApplicationContext();
        mScrollViewLayout = (ScrollView)findViewById(R.id.license_layout);

        mLicense1 = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)findViewById(R.id.license_openpad);
                tv.setText(RawTextManager.getRawText(mContextThis, R.raw.openpad_license));
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mScrollViewLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            }
        });
        mLicense2 = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)findViewById(R.id.license_kopub);
                tv.setText(RawTextManager.getRawText(mContextThis, R.raw.kopub_dotum_license));
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mScrollViewLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            }
        });
        mLicense3 = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)findViewById(R.id.license_jua);
                tv.setText(RawTextManager.getRawText(mContextThis, R.raw.bmjua_license));
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mScrollViewLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            }
        });
        mLicense4 = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)findViewById(R.id.license_glide);
                tv.setText(RawTextManager.getRawText(mContextThis, R.raw.glide_license));
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mScrollViewLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            }
        });
        mLicense1.start();
        mLicense2.start();
        mLicense3.start();
        mLicense4.start();

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
    protected void onDestroy() {
        super.onDestroy();
        if (mLicense1 != null)
            mLicense1.interrupt();
        mLicense1 = null;
        if (mLicense2 != null)
            mLicense2.interrupt();
        mLicense2 = null;
        if (mLicense3 != null)
            mLicense3.interrupt();
        mLicense3 = null;
        if (mLicense4 != null)
            mLicense4.interrupt();
        mLicense4 = null;
        mContextThis = null;
        mScrollViewLayout = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
