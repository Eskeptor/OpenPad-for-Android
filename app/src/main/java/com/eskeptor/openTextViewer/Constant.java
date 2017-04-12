package com.eskeptor.openTextViewer;

import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Created by eskeptor on 17. 1. 26.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class Constant
{
    public static final String APP_INTERNAL_URL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OpenPad";
    public static final String APP_SETTINGS_PREFERENCE = "Setting_Pref";
    public static final String APP_DEV_MAILADDRESS = "skyvvv624@gmail.com";
    public static final String APP_FIRST_SETUP_PREFERENCE = "isFirst_Pref";
    public static final String APP_ADMOB_VISIBLE = "adMob_Pref";
    public static final String APP_VERSION_CHECK = "version_Pref";
    public static final String APP_LASTED_VERSION = BuildConfig.VERSION_NAME;
    public static final String APP_EXPERIMENT_ENHANCEIO = "experiment_enhanceIO_Pref";

    public static final String WIDGET_TITLE_FONT_COLOR_RED = "widget_title_font_color_r_Pref";
    public static final String WIDGET_TITLE_FONT_COLOR_GREEN = "widget_title_font_color_g_Pref";
    public static final String WIDGET_TITLE_FONT_COLOR_BLUE = "widget_title_font_color_b_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_RED = "widget_title_back_color_r_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_GREEN = "widget_title_back_color_g_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_BLUE = "widget_title_back_color_b_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_RED = "widget_context_font_color_r_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_GREEN = "widget_context_font_color_g_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_BLUE = "widget_context_font_color_b_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_RED = "widget_context_back_color_r_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_GREEN = "widget_context_back_color_g_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_BLUE = "widget_context_back_color_b_Pref";

    public static final int WIDGET_TITLE_FONT_COLOR_RED_DEFAULT = 1;
    public static final int WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT = 1;
    public static final int WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT = 1;
    public static final int WIDGET_TITLE_BACK_COLOR_RED_DEFAULT = 239;
    public static final int WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT = 239;
    public static final int WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT = 239;
    public static final int WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT = 255;
    public static final int WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT = 255;
    public static final int WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT = 255;

    public static final boolean APP_FIRST_EXECUTE = false;
    public static final boolean APP_TWICE_EXECUTE = true;

    public static final float SETTINGS_DEFAULT_VALUE_TEXT_SIZE = 18F;

    //public static final int FOLDER_COUNT_LIMIT = 30;
    public static final String FOLDER_DEFAULT_NAME = "Basic";
    public static final int FOLDER_TYPE_DEFAULT = 1;
    public static final int FOLDER_TYPE_CUSTOM = 2;
    public static final int FOLDER_TYPE_EXTERNAL = -1;

    public static final int MEMO_TYPE_NEW = 1;
    public static final int MEMO_TYPE_OPEN_INTERNAL = 2;
    public static final int MEMO_TYPE_OPEN_EXTERNAL = 3;
    public static final int MEMO_SAVE_SELECT_TYPE_EXTERNAL = 0;
    public static final int MEMO_SAVE_SELECT_TYPE_INTERNAL = 1;
    public static final int MEMO_BLOCK_NEXT = 1;
    public static final int MEMO_BLOCK_PREV = -1;

    public static final int BROWSER_TYPE_OPEN_EXTERNAL = 1;
    public static final int BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE = 3;
    public static final int BROWSER_MENU_SORT_ASC = 0;
    public static final int BROWSER_MENU_SORT_DES = 1;
    public static final int BROWSER_IMAGE_TYPE_FOLDER = 1;
    public static final int BROWSER_IMAGE_TYPE_NORMAL = 2;
    public static final int BROWSER_IMAGE_TYPE_OVER1 = 3;
    public static final int BROWSER_IMAGE_TYPE_OVER2 = 4;

    public static final int REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE = 0;
    public static final int REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE = 1;
    public static final int REQUEST_CODE_OPEN_FILE_EXTERNAL = 2;
    public static final int REQUEST_CODE_OPEN_FOLDER = 3;
    public static final int REQUEST_CODE_APP_PERMISSION_STORAGE = 4;

    public static final int LISTVIEW_FILE_TYPE_TEXT = 1;
    public static final int LISTVIEW_FILE_TYPE_IMAGE = 2;

    public static final String REGEX = "^[_a-zA-Z0-9.ㄱ-ㅎㅏ-ㅣ가-힣]*$";

    public static final long WAIT_FOR_SECOND = 2000L;
    public static final String FILE_TEXT_EXTENSION = ".txt";
    public static final String FILE_IMAGE_EXTENSION = ".png";
    public static final String FILE_LOG_COUNT = "lastCount.log";

    public static final int SETTINGS_ACTIVESCREEN_MAIN = 1;
    public static final int SETTINGS_ACTIVESCREEN_FONT = 2;
    public static final int SETTINGS_ACTIVESCREEN_HELP = 3;
    public static final int SETTINGS_ACTIVESCREEN_HELP_CONTENTS = 4;

    public static final float PAINT_MINIMUM_LINE_LENGTH_PIXEL = 0.0f;
    public static final float PAINT_ERASER_WIDTH_PIXEL = 20.0f;
    public static final float PAINT_DEFAULT_WIDTH_PIXEL = 10.0f;
    public static final float PAINT_MAXIMUM_WIDTH = 40.0f;
    public static final int PAINT_COLOR_MAX = 255;
    public static final int PAINT_TYPE_BRUSH = 1;
    public static final int PAINT_TYPE_ERASER = 2;

    public static final String BASIC_FILE_UNIT = "KB";
    public static final long MEGABYTE = 1048576L;
    public static final long KILOBYTE = 1024L;
    public static final long SAFE_LOAD_CAPACITY = 500L;

    public static final String ENCODE_TYPE_UTF8 = "UTF-8";
    public static final String ENCODE_TYPE_EUCKR = "EUC-KR";

    public static final int TEXTMANAGER_BUFFER = 3000;

    /* 여기서 부터는 Intent에 쓰이는 것들 */
    public static final String INTENT_EXTRA_MEMO_OPEN_FILEURL = "MEMO_OPEN_FILEURL";
    public static final String INTENT_EXTRA_MEMO_OPEN_FILENAME = "MEMO_OPEN_FILENAME";
    public static final String INTENT_EXTRA_MEMO_TYPE = "MEMO_TYPE";
    public static final String INTENT_EXTRA_MEMO_OPEN_FOLDERURL = "MEMO_OPEN_FOLDERURL";
    public static final String INTENT_EXTRA_BROWSER_TYPE = "BROWSER_TYPE";
    public static final String INTENT_EXTRA_CURRENT_FOLDERURL = "CURRENT_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FOLDERURL = "MEMO_SAVE_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FILEURL = "MEMO_SAVE_FILEURL";
    public static final String INTENT_EXTRA_HELP_INDEX = "HELP_INDEX";
    public static final String INTENT_EXTRA_MEMO_DIVIDE = "MEMO_OPEN_DIVIDE";
}
