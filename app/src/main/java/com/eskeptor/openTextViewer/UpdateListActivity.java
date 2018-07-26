package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import com.eskeptor.openTextViewer.textManager.RawTextManager;

public class UpdateListActivity extends AppCompatActivity {
    private TextView mUpdateList;
    private Thread mTextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings_information_updatelist_title);
        }

        mUpdateList = findViewById(R.id.updateList_contents);
        mTextThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mUpdateList.setText(RawTextManager.getRawText(getApplicationContext(), R.raw.updatelist));
            }
        });
        mTextThread.start();

        Context contextThis = getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        int font = sharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());
        if (font == Constant.FontType.BaeDal_JUA.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(contextThis, "fonts/bmjua.ttf"))
                    .addBold(Typekit.createFromAsset(contextThis, "fonts/bmjua.ttf"));
        } else if (font == Constant.FontType.KOPUB_Dotum.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(contextThis, "fonts/kopub_dotum_medium.ttf"))
                    .addBold(Typekit.createFromAsset(contextThis, "fonts/kopub_dotum_medium.ttf"));
        } else {
            Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextThread != null) {
            mTextThread.interrupt();
        }
        mTextThread = null;
        mUpdateList = null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}