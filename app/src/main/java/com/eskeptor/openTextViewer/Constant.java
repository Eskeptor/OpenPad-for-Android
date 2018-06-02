package com.eskeptor.openTextViewer;

import android.os.Environment;
import java.io.File;

/**
 * Created by eskeptor on 17. 1. 26.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */


// 상수만 정의되어 있는 클래스
public class Constant {
    public static final String FOLDER_DEFAULT_NAME = "Basic";
    public static final String FOLDER_WIDGET_NAME = "Widget";

    public static final String APP_INTERNAL_URL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OpenPad";
    public static final String APP_WIDGET_URL = APP_INTERNAL_URL + File.separator + FOLDER_WIDGET_NAME;
    public static final String APP_SETTINGS_PREFERENCE = "Setting_Pref";
    public static final String APP_WIDGET_PREFERENCE = "Widget_Pref";
    public static final String APP_DEV_MAILADDRESS = "skyvvv624@gmail.com";
    public static final String APP_FIRST_SETUP_PREFERENCE = "isFirst_Pref";
    public static final String APP_TUTORIAL = "tutorial_Pref";
    public static final String APP_ADMOB_VISIBLE = "adMob_Pref";
    public static final String APP_VERSION_CHECK = "version_Pref";
    public static final String APP_LASTED_VERSION = BuildConfig.VERSION_NAME;
    public static final String APP_FONT = "font_Pref";
    public static final String APP_VIEW_IMAGE = "isViewImage_Pref";
    public static final String APP_TEXT_LINES = "textLines_Pref";
    public static final String APP_PASSWORD_SET = "isSetPassword_Pref";
    public static final String APP_PASSWORD_VALUE = "passwordValue_Pref";
    public static final String APP_PASSWORD_KEY = "passwordKey_Pref";
    public static final String APP_PASSWORD_SET_APP = "isSetPassword_APP_Pref";
    public static final String APP_PASSWORD_SET_FILE = "isSetPassword_FILE_Pref";
    public static final String APP_SWIPE_DELETE_PREFERENCE = "swipe_delete_Pref";
    public static final String APP_PASSWORD_KEY_DEFAULT = "ABCDABCDABCDABCD";

    public static final boolean APP_FIRST_EXECUTE = false;
    public static final boolean APP_TWICE_EXECUTE = true;

    public static final float SETTINGS_DEFAULT_VALUE_TEXT_SIZE = 18F;
    public static final int SETTINGS_DEFAULT_VALUE_TEXT_LINES = 150;

    public static final int REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE = 0;
    public static final int REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE = 1;
    public static final int REQUEST_CODE_OPEN_FOLDER = 3;
    public static final int REQUEST_CODE_APP_PERMISSION_STORAGE = 4;
    public static final int REQUEST_CODE_PERMISSION_GRANT = 5;
    public static final int REQUEST_CODE_PASSWORD = 6;

    public static final String REGEX = "^[_a-zA-Z0-9.ㄱ-ㅎㅏ-ㅣ가-힣]*$";

    public static final String FILE_TEXT_EXTENSION = ".txt";
    public static final String FILE_IMAGE_EXTENSION = ".png";
    public static final String FILE_LOG_COUNT = "lastCount.log";
    public static final String FILE_IMAGE_SUMMARY = ".smy";

    public static final String BASIC_FILE_UNIT = "KB";

    /* 여기서 부터는 Intent에 쓰이는 것들 */
    public static final String INTENT_EXTRA_MEMO_OPEN_FILEURL = "MEMO_OPEN_FILEURL";
    public static final String INTENT_EXTRA_MEMO_OPEN_FILENAME = "MEMO_OPEN_FILENAME";
    public static final String INTENT_EXTRA_MEMO_OPEN_FOLDERURL = "MEMO_OPEN_FOLDERURL";
    public static final String INTENT_EXTRA_BROWSER_TYPE = "BROWSER_TYPE";
    public static final String INTENT_EXTRA_CURRENT_FOLDERURL = "CURRENT_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FOLDERURL = "MEMO_SAVE_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FILEURL = "MEMO_SAVE_FILEURL";
    public static final String INTENT_EXTRA_HELP_INDEX = "HELP_INDEX";
    public static final String INTENT_EXTRA_MEMO_DIVIDE = "MEMO_OPEN_DIVIDE";
    public static final String INTENT_EXTRA_MEMO_ISWIDGET = "MEMO_ISWIDGET";
    public static final String INTENT_EXTRA_WIDGET_ID = "WIDGET_ID";
    public static final String INTENT_EXTRA_PASSWORD = "PASSWORD_INTENT_TYPE";
    public static final String INTENT_EXTRA_PASSWORD_MATCH = "PASSWORD_MATCH";
    public static final String INTENT_EXTRA_PASSWORD_SET = "PASSWORD_SET";

    public static final String DATE_FORMAT_WIDGET_KOREA = "yyyy년 MM월 dd일";
    public static final String DATE_FORMAT_WIDGET_USA = "MM/dd/yyyy";
    public static final String DATE_FORMAT_WIDGET_UK = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MAIN_KOREA = "yyyy년 MM월 dd일 hh:mm a";
    public static final String DATE_FORMAT_MAIN_USA = "MM/dd/yyyy hh:mm a";
    public static final String DATE_FORMAT_MAIN_UK = "dd/MM/yyyy hh:mm a";

    public enum PasswordIntentType {
        Set(0), Reset(1), Execute(2), MainExecute(3);

        private final int value;
        PasswordIntentType(final int _value) {
            value = _value;
        }
        public int getValue() {
            return value;
        }
    }

    public enum FontType {
        Default(0), BaeDal_JUA(1), KOPUB_Dotum(2);

        private final int value;
        FontType(final int _value) {
            value = _value;
        }
        public int getValue() {
            return value;
        }
    }

    public static final int HANDLER_REFRESH_LIST = 1;

}
