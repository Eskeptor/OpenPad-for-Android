package com.eskeptor.openTextViewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;


/**
 * Created by eskeptor on 17. 2. 8.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static Constant.ActiveScreenType mActiveScene;
    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mSharedPrefEditor;
    private static Help mHelpAct;
    private static FontSettings mFontSettingAct;
    private static Settings mSettingsAct;
    private static ActionBar mActionBar;

    public static class Help extends PreferenceFragment {
        private Preference mainList;
        private Preference mainMemoCreate;
        private Preference mainDelete;
        private Preference mainOpenFolder;
        private Preference folderList;
        private Preference folderDefault;
        private Preference folderCreate;
        private Preference folderDelete;
        private Preference folderExternal;
        private Preference memoSaveLength;
        private Preference memoFontColor;
        private Preference memoEdit;
        private Preference etcAboutHelp;
        private Preference etcBackup;
        private Preference etcFileColor;
        private Preference etcPermission;
        private Preference etcAd;
        private Preference etcFontBroken;
        private Preference etcWidget;

        @Override
        public void onCreate(Bundle _savedInstanceState) {
            super.onCreate(_savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings_help);

            mActionBar.setTitle(R.string.settings_information_help_title);

            mainList = findPreference("settings_key_main_list");
            mainMemoCreate = findPreference("settings_key_main_memocreate");
            mainDelete = findPreference("settings_key_main_delete");
            mainOpenFolder = findPreference("settings_key_main_openfolder");
            folderList = findPreference("settings_key_folder_list");
            folderDefault = findPreference("settings_key_folder_default");
            folderCreate = findPreference("settings_key_folder_create");
            folderDelete = findPreference("settings_key_folder_delete");
            folderExternal = findPreference("settings_key_foler_external");
            memoSaveLength = findPreference("settings_key_memo_savelengthlimited");
            memoFontColor = findPreference("settings_key_memo_fontcolor");
            memoEdit = findPreference("settings_key_memo_edit");
            etcAboutHelp = findPreference("settings_key_etc_abouthelp");
            etcBackup = findPreference("settings_key_etc_backup");
            etcFileColor = findPreference("settings_key_etc_filecolor");
            etcPermission = findPreference("settings_key_etc_permission");
            etcAd = findPreference("settings_key_etc_ad");
            etcFontBroken = findPreference("settings_key_etc_fontbroken");
            etcWidget = findPreference("settings_key_etc_widget");

            Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference _preference) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setClass(getActivity(), HelpContentsActivity.class);
                    intent.putExtra(Constant.INTENT_EXTRA_HELP_INDEX, _preference.getKey());
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                    return false;
                }
            };

            mainList.setOnPreferenceClickListener(clickListener);
            mainMemoCreate.setOnPreferenceClickListener(clickListener);
            mainDelete.setOnPreferenceClickListener(clickListener);
            mainOpenFolder.setOnPreferenceClickListener(clickListener);
            folderExternal.setOnPreferenceClickListener(clickListener);
            folderDelete.setOnPreferenceClickListener(clickListener);
            folderCreate.setOnPreferenceClickListener(clickListener);
            folderDefault.setOnPreferenceClickListener(clickListener);
            folderList.setOnPreferenceClickListener(clickListener);
            memoFontColor.setOnPreferenceClickListener(clickListener);
            memoSaveLength.setOnPreferenceClickListener(clickListener);
            memoEdit.setOnPreferenceClickListener(clickListener);
            etcAboutHelp.setOnPreferenceClickListener(clickListener);
            etcBackup.setOnPreferenceClickListener(clickListener);
            etcFileColor.setOnPreferenceClickListener(clickListener);
            etcPermission.setOnPreferenceClickListener(clickListener);
            etcAd.setOnPreferenceClickListener(clickListener);
            etcFontBroken.setOnPreferenceClickListener(clickListener);
            etcWidget.setOnPreferenceClickListener(clickListener);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mainList = null;
            mainMemoCreate = null;
            mainDelete = null;
            mainOpenFolder = null;
            folderList = null;
            folderDefault = null;
            folderCreate = null;
            folderDelete = null;
            folderExternal = null;
            memoSaveLength = null;
            memoFontColor = null;
            memoEdit = null;
            etcAboutHelp = null;
            etcBackup = null;
            etcFileColor = null;
            etcPermission = null;
            etcAd = null;
            etcFontBroken = null;
            mActionBar.setTitle(R.string.settings_title);
        }
    }

    public static class FontSettings extends PreferenceFragment {
        private Preference textSize;

        @Override
        public void onCreate(Bundle _savedInstanceState) {
            super.onCreate(_savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings_font);

            textSize = findPreference("settings_key_font_fontsize");
            textSize.setSummary(Float.toString(mSharedPref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE)));
            textSize.setDefaultValue(Float.toString(mSharedPref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE)));
            Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference _preference) {
                    if (_preference.getKey().equals("settings_key_font_fontsize")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getResources().getString(R.string.settings_font_fontsize));
                        dialog.setMessage(getResources().getString(R.string.settings_dialog_font_context));
                        NumberPicker picker = new NumberPicker(getActivity());
                        picker.setMaxValue(30);
                        picker.setMinValue(8);
                        picker.setValue((int) mSharedPref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
                        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker _picker, int _oldVal, int _newVal) {
                                if (_oldVal != _newVal) {
                                    mSharedPrefEditor.putFloat("FontSize", _newVal);
                                    textSize.setSummary(Integer.toString(_newVal));
                                }
                            }
                        });
                        dialog.setView(picker);
                        dialog.setPositiveButton(getResources().getString(R.string.settings_dialog_info_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {
                                switch (_which) {
                                    case AlertDialog.BUTTON_POSITIVE:
                                        break;
                                }
                                _dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    return false;
                }
            };
            textSize.setOnPreferenceClickListener(clickListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            mSharedPrefEditor.apply();
        }
    }

    public static class Settings extends PreferenceFragment {
        private Preference mInfo;
        private Preference mFont;
        private Preference mVersion;
        private Preference mBugReport;
        private Preference mHelp;
        private Preference mUpdateList;
        private CheckBoxPreference mAdMob;
        private CheckBoxPreference mEnhanceIO;
        private Preference mEnhanceIOLine;
        private Preference mLicense;

        private long mPressTime = 0;

        @Override
        public void onCreate(Bundle _savedInstanceState) {
            super.onCreate(_savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            mInfo = findPreference("settings_key_info");
            mVersion = findPreference("settings_key_version");
            mFont = findPreference("settings_key_font");
            mBugReport = findPreference("settings_key_bugreport");
            mHelp = findPreference("settings_key_help");
            mUpdateList = findPreference("settings_key_updatelist");
            mAdMob = (CheckBoxPreference) findPreference("settings_key_admob");
            mEnhanceIO = (CheckBoxPreference) findPreference("settings_key_enhanceIO");
            mEnhanceIOLine = findPreference("settings_key_enhanceIO_Line");
            mLicense = findPreference("settings_key_license");

            Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference _preference) {
                    String key = _preference.getKey();
                    switch (key) {
                        case "settings_key_info": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(getResources().getString(R.string.settings_dialog_info_title));
                            String copyright = getResources().getString(R.string.settings_dialog_info_copyright);
                            String license = getResources().getString(R.string.settings_dialog_info_license);
                            String icons = getResources().getString(R.string.settings_dialog_info_icon);
                            String homepage = getResources().getString(R.string.settings_dialog_info_homepage);
                            String line = "\n";

                            dialog.setMessage(copyright + line + license + line + icons + line + homepage);
                            dialog.setPositiveButton(R.string.settings_dialog_info_ok, null);
                            dialog.show();
                            break;
                        }
                        case "settings_key_font": {
                            getFragmentManager().beginTransaction().replace(android.R.id.content, mFontSettingAct).commit();
                            mActiveScene = Constant.ActiveScreenType.Font;
                            break;
                        }
                        case "settings_key_bugreport": {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            String[] address = {Constant.APP_DEV_MAILADDRESS};
                            intent.setType("message/rfc822");
                            intent.putExtra(Intent.EXTRA_EMAIL, address);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            intent.putExtra(Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(intent, getString(R.string.settings_information_bug_email_choose)));
                            break;
                        }
                        case "settings_key_help": {
                            getFragmentManager().beginTransaction().replace(android.R.id.content, mHelpAct).commit();
                            mActiveScene = Constant.ActiveScreenType.Help;
                            break;
                        }
                        case "settings_key_updatelist": {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setClass(getActivity(), UpdateListActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                            break;
                        }
                        case "settings_key_version": {
                            if (System.currentTimeMillis() > mPressTime + 1000) {
                                mPressTime = System.currentTimeMillis();
                                return false;
                            }
                            if (System.currentTimeMillis() <= mPressTime + 1000) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), HiddenActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(0, 0);
                            }
                            break;
                        }
                        case "settings_key_enhanceIO_Line": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(getResources().getString(R.string.settings_expfunc_enhanceIO_Line_title));
                            dialog.setMessage(getResources().getString(R.string.settings_dialog_expfunc_enhanceIO_Line));
                            NumberPicker picker = new NumberPicker(getActivity());
                            picker.setMaxValue(500);
                            picker.setMinValue(10);
                            picker.setValue(mSharedPref.getInt("Lines", Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES));
                            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker _picker, int _oldVal, int _newVal) {
                                    if (_oldVal != _newVal) {
                                        mSharedPrefEditor.putInt("Lines", _newVal);
                                    }
                                }
                            });
                            dialog.setView(picker);
                            dialog.setPositiveButton(getResources().getString(R.string.settings_dialog_info_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface _dialog, int _which) {
                                    switch (_which) {
                                        case AlertDialog.BUTTON_POSITIVE:
                                            break;
                                    }
                                    _dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        }
                        case "settings_key_license": {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setClass(getActivity(), LicenseActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                            break;
                        }
                    }
                    return false;
                }
            };

            CheckBoxPreference.OnPreferenceClickListener checkClickListener = new CheckBoxPreference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference _preference) {
                    String key = _preference.getKey();
                    switch (key) {
                        case "settings_key_admob": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(R.string.settings_general_admob);
                            dialog.setMessage(R.string.settings_general_admob_dialog_context);
                            dialog.setPositiveButton(R.string.settings_dialog_info_ok, null);
                            dialog.show();
                            break;
                        }
                        case "settings_key_enhanceIO": {
                            if (mEnhanceIO.isChecked()) {
                                mEnhanceIOLine.setEnabled(true);
                            } else {
                                mEnhanceIOLine.setEnabled(false);
                            }
                            break;
                        }
                    }
                    return false;
                }
            };

            mInfo.setOnPreferenceClickListener(clickListener);
            mFont.setOnPreferenceClickListener(clickListener);
            mBugReport.setOnPreferenceClickListener(clickListener);
            mHelp.setOnPreferenceClickListener(clickListener);
            mUpdateList.setOnPreferenceClickListener(clickListener);
            mVersion.setOnPreferenceClickListener(clickListener);
            mVersion.setSummary(BuildConfig.VERSION_NAME);
            mEnhanceIOLine.setOnPreferenceClickListener(clickListener);
            mLicense.setOnPreferenceClickListener(clickListener);

            mAdMob.setOnPreferenceClickListener(checkClickListener);
            mAdMob.setChecked(mSharedPref.getBoolean(Constant.APP_ADMOB_VISIBLE, true));
            mEnhanceIO.setOnPreferenceClickListener(checkClickListener);
            mEnhanceIO.setChecked(mSharedPref.getBoolean(Constant.APP_EXPERIMENT_ENHANCEIO, false));
            mEnhanceIOLine.setEnabled(mEnhanceIO.isChecked());
        }

        @Override
        public void onPause() {
            super.onPause();
            mSharedPrefEditor.putBoolean(Constant.APP_ADMOB_VISIBLE, mAdMob.isChecked());
            mSharedPrefEditor.putBoolean(Constant.APP_EXPERIMENT_ENHANCEIO, mEnhanceIO.isChecked());
            mSharedPrefEditor.apply();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mInfo = null;
            mFont = null;
            mVersion = null;
            mBugReport = null;
            mHelp = null;
            mUpdateList = null;
            mAdMob = null;
            mEnhanceIO = null;
            mEnhanceIOLine = null;
            mLicense = null;
        }
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setTitle(R.string.settings_title);
        }
    }

    @Override
    protected boolean isValidFragment(String _fragmentName) {
        return PreferenceFragment.class.getName().equals(_fragmentName)
                || FontSettings.class.getName().equals(_fragmentName)
                || Settings.class.getName().equals(_fragmentName)
                || Help.class.getName().equals(_fragmentName);
    }

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setupActionBar();

        mHelpAct = new Help();
        mFontSettingAct = new FontSettings();
        mSettingsAct = new Settings();

        getFragmentManager().beginTransaction().replace(android.R.id.content, mSettingsAct).commit();
        mActiveScene = Constant.ActiveScreenType.Main;

        mSharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
    }

    @Override
    public void onBackPressed() {
        switch (mActiveScene) {
            case Main:
                super.onBackPressed();
                overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_top);
                break;
            default:
                getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();
                mActiveScene = Constant.ActiveScreenType.Main;
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelpAct = null;
        mFontSettingAct = null;
        mSettingsAct = null;
        mSharedPref = null;
        mSharedPrefEditor = null;
        mActionBar = null;
    }
}
