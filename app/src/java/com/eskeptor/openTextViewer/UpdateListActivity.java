package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import util.RawTextOpener;

public class UpdateListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_update_list);

        TextView updateList = (TextView) findViewById(R.id.updateList_contents);
        updateList.setText(RawTextOpener.getRawText(getApplicationContext(), R.raw.updatelist));

        Context contextThis = getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        int font = sharedPref.getInt(Constant.APP_FONT, Constant.FONT_DEFAULT);
        switch (font) {
            case Constant.FONT_DEFAULT:
                Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
                break;
            case Constant.FONT_BAEDAL_JUA:
                Typekit.getInstance().addNormal(Typekit.createFromAsset(contextThis, "fonts/bmjua.ttf"))
                        .addBold(Typekit.createFromAsset(contextThis, "fonts/bmjua.ttf"));
                break;
            case Constant.FONT_KOPUB_DOTUM:
                Typekit.getInstance().addNormal(Typekit.createFromAsset(contextThis, "fonts/kopub_dotum_medium.ttf"))
                        .addBold(Typekit.createFromAsset(contextThis, "fonts/kopub_dotum_medium.ttf"));
                break;
        }
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
}