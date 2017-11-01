package com.eskeptor.openTextViewer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import util.RawTextOpener;

public class LicenseActivity extends AppCompatActivity {
    private Thread mLicense1;
    private Thread mLicense2;
    private Thread mLicense3;
    private Context mThisContext;
    private ScrollView mScrollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        mThisContext = getApplicationContext();
        mScrollViewLayout = (ScrollView)findViewById(R.id.license_layout);

        mLicense1 = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)findViewById(R.id.license_openpad);
                tv.setText(RawTextOpener.getRawText(mThisContext, R.raw.openpad_license));
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
                tv.setText(RawTextOpener.getRawText(mThisContext, R.raw.kopub_dotum_license));
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
                tv.setText(RawTextOpener.getRawText(mThisContext, R.raw.bmjua_license));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLicense1 != null)
            mLicense1.interrupt();
        if (mLicense2 != null)
            mLicense2.interrupt();
        if (mLicense3 != null)
            mLicense3.interrupt();
        mThisContext = null;
        mScrollViewLayout = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
