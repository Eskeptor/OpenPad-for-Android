package com.eskeptor.openTextViewer;

import android.os.Environment;

import java.io.File;

/**
 * Created by eskeptor on 17. 1. 26.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

class Constant
{
    public static final String APP_INTERNAL_URL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OpenPad";
    public static final String APP_SETTINGS_PREFERENCE = "Setting_Pref";
    public static final String APP_DEV_MAILADDRESS = "skyvvv624@gmail.com";

    public static final float SETTINGS_DEFAULT_VALUE_TEXT_SIZE = 18F;

    //public static final int FOLDER_COUNT_LIMIT = 30;
    public static final String FOLDER_DEFAULT_NAME = "Basic";
    public static final int FOLDER_TYPE_DEFAULT = 1;
    public static final int FOLDER_TYPE_CUSTOM = 2;
    public static final int FOLDER_TYPE_EXTERNAL = -1;

    public static final int MEMO_TYPE_NEW = 1;
    public static final int MEMO_TYPE_OPEN_INTERNAL = 2;
    public static final int MEMO_TYPE_OPEN_EXTERNAL = 3;

    public static final int BROWSER_TYPE_OPEN_EXTERNAL = 1;
    public static final int BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE = 3;
    public static final int BROWSER_MENU_SORT_ASC = 0;
    public static final int BROWSER_MENU_SORT_DES = 1;
    public static final int BROWSER_IMAGE_TYPE_FOLDER = 1;
    public static final int BROWSER_IMAGE_TYPE_NORMAL = 2;
    public static final int BROWSER_IMAGE_TYPE_OVER1 = 3;
    public static final int BROWSER_IMAGE_TYPE_OVER2 = 4;

    public static final int SELECTOR_TYPE_SAVE = 1;
    public static final int SELECTOR_TYPE_OPEN = 2;

    public static final int REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE = 0;
    public static final int REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE = 1;
    public static final int REQUEST_CODE_OPEN_FILE_EXTERNAL = 2;
    public static final int REQUEST_CODE_OPEN_FOLDER = 3;
    public static final int REQUEST_CODE_APP_PERMISSION_WRITE_STORAGE = 4;
    public static final int REQUEST_CODE_APP_PERMISSION_READ_STORAGE = 5;

    public static final int FILE_OPEN_TYPE_INTERNAL = 1;
    public static final int FILE_OPEN_TYPE_EXTERNAL = 2;

    public static final String REGEX = "^[_a-zA-Z0-9.]*$";

    public static final long WAIT_FOR_SECOND = 2000L;
    public static final String FILE_EXTENSION = ".txt";

    public static final String LOG_FILE_COUNT = "lastCount.log";

    public static final int SETTINGS_ACTIVESCREEN_MAIN = 1;
    public static final int SETTINGS_ACTIVESCREEN_FONT = 2;
    public static final int SETTINGS_ACTIVESCREEN_HELP = 3;
    public static final int SETTINGS_ACTIVESCREEN_HELP_CONTENTS = 4;

    public static final long MEGABYTE = 1048576L;
    public static final long KILOBYTE = 1024L;
    public static final long SAFE_LOAD_CAPACITY = 500L;

    public static final String ENCODE_TYPE_UTF8 = "UTF-8";
    public static final String ENCODE_TYPE_EUCKR = "EUC-KR";

    /* 여기서 부터는 Intent에 쓰이는 것들 */
    public static final String INTENT_EXTRA_MEMO_OPEN_FILEURL = "MEMO_OPEN_FILEURL";
    public static final String INTENT_EXTRA_MEMO_OPEN_FILENAME = "MEMO_OPEN_FILENAME";
    public static final String INTENT_EXTRA_MEMO_TYPE = "MEMO_TYPE";
    public static final String INTENT_EXTRA_MEMO_OPEN_FOLDERURL = "MEMO_OPEN_FOLDERURL";
    public static final String INTENT_EXTRA_BROWSER_TYPE = "BROWSER_TYPE";
    public static final String INTENT_EXTRA_CURRENT_FOLDERURL = "CURRENT_FOLDERURL";
    public static final String INTENT_EXTRA_FILE_SIZE = "FILE_SIZE";
    public static final String INTENT_EXTRA_MEMO_SAVE_FOLDERURL = "MEMO_SAVE_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FILEURL = "MEMO_SAVE_FILEURL";
    public static final String INTENT_EXTRA_HELP_INDEX = "HELP_INDEX";
}
