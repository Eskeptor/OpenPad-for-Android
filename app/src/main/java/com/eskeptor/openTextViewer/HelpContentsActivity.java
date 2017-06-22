package com.eskeptor.openTextViewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by eskeptor on 17. 2. 15.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class HelpContentsActivity extends AppCompatActivity
{
    private TextView txtTitle;
    private TextView txtContexts;
    private String helpIndex;

    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_helpcontents);

        setTitle(R.string.settings_information_help_title);
        txtTitle = (TextView)findViewById(R.id.help_txtTitle);
        txtContexts = (TextView)findViewById(R.id.help_contexts);

        helpIndex = getIntent().getStringExtra(Constant.INTENT_EXTRA_HELP_INDEX);
        helpDetector();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txtTitle = null;
        txtContexts = null;
        helpIndex = null;
    }

    private void helpDetector()
    {
        if(helpIndex.equals("settings_key_main_list"))
        {
            txtTitle.setText(R.string.settings_help_main_list);
            txtContexts.setText(R.string.settings_help_main_list_context);
        }
        else if(helpIndex.equals("settings_key_main_memocreate"))
        {
            txtTitle.setText(R.string.settings_help_main_memocreate);
            txtContexts.setText(R.string.settings_help_main_memocreate_context);
        }
        else if(helpIndex.equals("settings_key_main_delete"))
        {
            txtTitle.setText(R.string.settings_help_main_delete);
            txtContexts.setText(R.string.settings_help_main_delete_context);
        }
        else if(helpIndex.equals("settings_key_main_openfolder"))
        {
            txtTitle.setText(R.string.settings_help_main_openfolder);
            txtContexts.setText(R.string.settings_help_main_openfolder_context);
        }
        else if(helpIndex.equals("settings_key_folder_list"))
        {
            txtTitle.setText(R.string.settings_help_folder_list);
            txtContexts.setText(R.string.settings_help_folder_list_context);
        }
        else if(helpIndex.equals("settings_key_folder_default"))
        {
            txtTitle.setText(R.string.settings_help_folder_default);
            txtContexts.setText(R.string.settings_help_folder_default_context);
        }
        else if(helpIndex.equals("settings_key_folder_create"))
        {
            txtTitle.setText(R.string.settings_help_folder_create);
            txtContexts.setText(R.string.settings_help_folder_create_context);
        }
        else if(helpIndex.equals("settings_key_folder_delete"))
        {
            txtTitle.setText(R.string.settings_help_folder_delete);
            txtContexts.setText(R.string.settings_help_folder_delete_context);
        }
        else if(helpIndex.equals("settings_key_foler_external"))
        {
            txtTitle.setText(R.string.settings_help_folder_external);
            txtContexts.setText(R.string.settings_help_folder_external_context);
        }
        else if(helpIndex.equals("settings_key_memo_savelengthlimited"))
        {
            txtTitle.setText(R.string.settings_help_memo_savelengthlimited);
            txtContexts.setText(R.string.settings_help_memo_savelength_context);
        }
        else if(helpIndex.equals("settings_key_memo_fontcolor"))
        {
            txtTitle.setText(R.string.settings_help_memo_fontcolor);
            txtContexts.setText(R.string.settings_help_memo_fontcolor_context);
        }
        else if(helpIndex.equals("settings_key_memo_edit"))
        {
            txtTitle.setText(R.string.settings_help_memo_edit);
            txtContexts.setText(R.string.settings_help_memo_edit_context);
        }
        else if(helpIndex.equals("settings_key_etc_abouthelp"))
        {
            txtTitle.setText(R.string.settings_help_etc_abouthelp);
            txtContexts.setText(R.string.settings_help_etc_abouthelp_context);
        }
        else if(helpIndex.equals("settings_key_etc_backup"))
        {
            txtTitle.setText(R.string.settings_help_etc_backup);
            txtContexts.setText(R.string.settings_help_etc_backup_context);
        }
        else if(helpIndex.equals("settings_key_etc_filecolor"))
        {
            txtTitle.setText(R.string.settings_help_etc_filecolor);
            txtContexts.setText(R.string.settings_help_etc_filecolor_context);
        }
        else if(helpIndex.equals("settings_key_etc_permission"))
        {
            txtTitle.setText(R.string.settings_help_etc_permission);
            txtContexts.setText(R.string.settings_help_etc_permission_context);
        }
        else if(helpIndex.equals("settings_key_etc_ad"))
        {
            txtTitle.setText(R.string.settings_help_etc_ad);
            txtContexts.setText(R.string.settings_help_etc_ad_context);
        }
        else if(helpIndex.equals("settings_key_etc_fontbroken"))
        {
            txtTitle.setText(R.string.settings_help_etc_fontbroken);
            txtContexts.setText(R.string.settings_help_etc_fontbroken_context);
        }
        else if(helpIndex.equals("settings_key_etc_widget"))
        {
            txtTitle.setText(R.string.settings_help_etc_widget);
            txtContexts.setText(R.string.settings_help_etc_widget_context);
        }
    }
}
