package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

/*
 * Created by eskeptor on 17. 2. 15.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Help screen on the Settings page
 */
public class HelpContentsActivity extends AppCompatActivity {
    private TextView mTxtTitle;
    private TextView mTxtContexts;
    private String mHelpIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpcontents);

        setTitle(R.string.settings_information_help_title);
        mTxtTitle = findViewById(R.id.help_txtTitle);
        mTxtContexts = findViewById(R.id.help_contexts);

        mHelpIndex = getIntent().getStringExtra(Constant.INTENT_EXTRA_HELP_INDEX);
        helpDetector();

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTxtTitle = null;
        mTxtContexts = null;
        mHelpIndex = null;
    }

    private void helpDetector() {
        switch (mHelpIndex) {
            case "settings_key_main_list":
                mTxtTitle.setText(R.string.settings_help_main_list);
                mTxtContexts.setText(R.string.settings_help_main_list_context);
                break;
            case "settings_key_main_memocreate":
                mTxtTitle.setText(R.string.settings_help_main_memocreate);
                mTxtContexts.setText(R.string.settings_help_main_memocreate_context);
                break;
            case "settings_key_main_delete":
                mTxtTitle.setText(R.string.settings_help_main_delete);
                mTxtContexts.setText(R.string.settings_help_main_delete_context);
                break;
            case "settings_key_main_openfolder":
                mTxtTitle.setText(R.string.settings_help_main_openfolder);
                mTxtContexts.setText(R.string.settings_help_main_openfolder_context);
                break;
            case "settings_key_folder_list":
                mTxtTitle.setText(R.string.settings_help_folder_list);
                mTxtContexts.setText(R.string.settings_help_folder_list_context);
                break;
            case "settings_key_folder_default":
                mTxtTitle.setText(R.string.settings_help_folder_default);
                mTxtContexts.setText(R.string.settings_help_folder_default_context);
                break;
            case "settings_key_folder_create":
                mTxtTitle.setText(R.string.settings_help_folder_create);
                mTxtContexts.setText(R.string.settings_help_folder_create_context);
                break;
            case "settings_key_folder_delete":
                mTxtTitle.setText(R.string.settings_help_folder_delete);
                mTxtContexts.setText(R.string.settings_help_folder_delete_context);
                break;
            case "settings_key_foler_external":
                mTxtTitle.setText(R.string.settings_help_folder_external);
                mTxtContexts.setText(R.string.settings_help_folder_external_context);
                break;
            case "settings_key_memo_savelengthlimited":
                mTxtTitle.setText(R.string.settings_help_memo_savelengthlimited);
                mTxtContexts.setText(R.string.settings_help_memo_savelength_context);
                break;
            case "settings_key_memo_fontcolor":
                mTxtTitle.setText(R.string.settings_help_memo_fontcolor);
                mTxtContexts.setText(R.string.settings_help_memo_fontcolor_context);
                break;
            case "settings_key_memo_edit":
                mTxtTitle.setText(R.string.settings_help_memo_edit);
                mTxtContexts.setText(R.string.settings_help_memo_edit_context);
                break;
            case "settings_key_etc_abouthelp":
                mTxtTitle.setText(R.string.settings_help_etc_abouthelp);
                mTxtContexts.setText(R.string.settings_help_etc_abouthelp_context);
                break;
            case "settings_key_etc_backup":
                mTxtTitle.setText(R.string.settings_help_etc_backup);
                mTxtContexts.setText(R.string.settings_help_etc_backup_context);
                break;
            case "settings_key_etc_filecolor":
                mTxtTitle.setText(R.string.settings_help_etc_filecolor);
                mTxtContexts.setText(R.string.settings_help_etc_filecolor_context);
                break;
            case "settings_key_etc_permission":
                mTxtTitle.setText(R.string.settings_help_etc_permission);
                mTxtContexts.setText(R.string.settings_help_etc_permission_context);
                break;
            case "settings_key_etc_ad":
                mTxtTitle.setText(R.string.settings_help_etc_ad);
                mTxtContexts.setText(R.string.settings_help_etc_ad_context);
                break;
            case "settings_key_etc_fontbroken":
                mTxtTitle.setText(R.string.settings_help_etc_fontbroken);
                mTxtContexts.setText(R.string.settings_help_etc_fontbroken_context);
                break;
            case "settings_key_etc_widget":
                mTxtTitle.setText(R.string.settings_help_etc_widget);
                mTxtContexts.setText(R.string.settings_help_etc_widget_context);
                break;
        }
    }
}