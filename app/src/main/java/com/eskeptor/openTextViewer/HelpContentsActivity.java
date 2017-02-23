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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpcontents);

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

    private void helpDetector()
    {
        if(helpIndex.equals("settings_key_main_list"))
        {
            txtTitle.setText(R.string.settings_help_main_list);
            txtContexts.setText(R.string.help_main_list);
        }
        else if(helpIndex.equals("settings_key_main_memocreate"))
        {
            txtTitle.setText(R.string.settings_help_main_memocreate);
            txtContexts.setText(R.string.help_main_memocreate);
        }
        else if(helpIndex.equals("settings_key_main_delete"))
        {
            txtTitle.setText(R.string.settings_help_main_delete);
            txtContexts.setText(R.string.help_main_delete);
        }
        else if(helpIndex.equals("settings_key_main_openfolder"))
        {
            txtTitle.setText(R.string.settings_help_main_openfolder);
            txtContexts.setText(R.string.help_main_openfolder);
        }
        else if(helpIndex.equals("settings_key_folder_list"))
        {
            txtTitle.setText(R.string.settings_help_folder_list);
            txtContexts.setText(R.string.help_folder_list);
        }
        else if(helpIndex.equals("settings_key_folder_default"))
        {
            txtTitle.setText(R.string.settings_help_folder_default);
            txtContexts.setText(R.string.help_folder_default);
        }
        else if(helpIndex.equals("settings_key_folder_create"))
        {
            txtTitle.setText(R.string.settings_help_folder_create);
            txtContexts.setText(R.string.help_folder_create);
        }
        else if(helpIndex.equals("settings_key_folder_delete"))
        {
            txtTitle.setText(R.string.settings_help_folder_delete);
            txtContexts.setText(R.string.help_folder_delete);
        }
        else if(helpIndex.equals("settings_key_foler_external"))
        {
            txtTitle.setText(R.string.settings_help_folder_external);
            txtContexts.setText(R.string.help_folder_external);
        }
        else if(helpIndex.equals("settings_key_memo_savelengthlimited"))
        {
            txtTitle.setText(R.string.settings_help_memo_savelengthlimited);
            txtContexts.setText(R.string.help_memo_savelength);
        }
        else if(helpIndex.equals("settings_key_memo_fontcolor"))
        {
            txtTitle.setText(R.string.settings_help_memo_fontcolor);
            txtContexts.setText(R.string.help_memo_fontcolor);
        }
        else if(helpIndex.equals("settings_key_memo_edit"))
        {
            txtTitle.setText(R.string.settings_help_memo_edit);
            txtContexts.setText(R.string.help_memo_edit);
        }
        else if(helpIndex.equals("settings_key_etc_filecolor"))
        {
            txtTitle.setText(R.string.settings_help_etc_filecolor);
            txtContexts.setText(R.string.help_etc_filecolor);
        }
    }
}
