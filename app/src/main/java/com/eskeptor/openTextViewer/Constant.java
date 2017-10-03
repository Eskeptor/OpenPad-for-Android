package com.eskeptor.openTextViewer;

import android.os.Environment;
import java.io.File;

/**
 * Created by eskeptor on 17. 1. 26.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */


// 상수만 정의되어 있는 클래스
public class Constant
{
    public static final String FOLDER_DEFAULT_NAME = "Basic";
    public static final String FOLDER_WIDGET_NAME = "Widget";
    public static final int FOLDER_TYPE_DEFAULT = 1;
    public static final int FOLDER_TYPE_CUSTOM = 2;
    public static final int FOLDER_TYPE_EXTERNAL = -1;

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
    public static final String WIDGET_FILE_URL = "widget_file_url";
    public static final String WIDGET_ID = "widget_id";
    public static final int WIDGET_MAX_LINE = 20;

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
    public static final int SETTINGS_DEFAULT_VALUE_TEXT_LINES = 200;

    public static final int MEMO_SAVE_SELECT_TYPE_EXTERNAL = 0;
    public static final int MEMO_SAVE_SELECT_TYPE_INTERNAL = 1;
    public static final int MEMO_BLOCK_NEXT = 1;
    public static final int MEMO_BLOCK_PREV = -1;


    public static final int REQUEST_CODE_SAVE_COMPLETE_NONE_OPENEDFILE = 0;
    public static final int REQUEST_CODE_SAVE_COMPLETE_OPEN_COMPLETE = 1;
    //public static final int REQUEST_CODE_OPEN_FILE_EXTERNAL = 2;
    public static final int REQUEST_CODE_OPEN_FOLDER = 3;
    public static final int REQUEST_CODE_APP_PERMISSION_STORAGE = 4;

    public static final int LISTVIEW_FILE_TYPE_TEXT = 1;
    public static final int LISTVIEW_FILE_TYPE_IMAGE = 2;

    public static final String REGEX = "^[_a-zA-Z0-9.ㄱ-ㅎㅏ-ㅣ가-힣]*$";

    public static final long WAIT_FOR_SECOND = 2000L;
    public static final String FILE_TEXT_EXTENSION = ".txt";
    public static final String FILE_IMAGE_EXTENSION = ".png";
    public static final String FILE_LOG_COUNT = "lastCount.log";

    public static final char WIDGET_LINKED_TOKEN = 'w';

    public static final float PAINT_MINIMUM_LINE_LENGTH_PIXEL = 0.0f;
    public static final float PAINT_ERASER_WIDTH_PIXEL = 20.0f;
    public static final float PAINT_DEFAULT_WIDTH_PIXEL = 10.0f;
    public static final float PAINT_MAXIMUM_WIDTH = 40.0f;
    public static final int PAINT_COLOR_MAX = 255;

    public static final String BASIC_FILE_UNIT = "KB";
    public static final long MEGABYTE = 1048576L;
    public static final long KILOBYTE = 1024L;
    public static final long SAFE_LOAD_CAPACITY = 500L;

    public static final String ENCODE_TYPE_EUCKR_STR = "EUC-KR";
    public static final String ENCODE_TYPE_UTF8_STR = "UTF-8";

    public static final int TEXTMANAGER_BUFFER = 3000;

    /* 여기서 부터는 Intent에 쓰이는 것들 */
    public static final String INTENT_EXTRA_MEMO_OPEN_FILEURL = "MEMO_OPEN_FILEURL";
    public static final String INTENT_EXTRA_MEMO_OPEN_FILENAME = "MEMO_OPEN_FILENAME";
    //public static final String INTENT_EXTRA_MEMO_TYPE = "MEMO_TYPE";
    public static final String INTENT_EXTRA_MEMO_OPEN_FOLDERURL = "MEMO_OPEN_FOLDERURL";
    public static final String INTENT_EXTRA_BROWSER_TYPE = "BROWSER_TYPE";
    public static final String INTENT_EXTRA_CURRENT_FOLDERURL = "CURRENT_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FOLDERURL = "MEMO_SAVE_FOLDERURL";
    public static final String INTENT_EXTRA_MEMO_SAVE_FILEURL = "MEMO_SAVE_FILEURL";
    public static final String INTENT_EXTRA_HELP_INDEX = "HELP_INDEX";
    public static final String INTENT_EXTRA_MEMO_DIVIDE = "MEMO_OPEN_DIVIDE";
    public static final String INTENT_EXTRA_MEMO_ISWIDGET = "MEMO_ISWIDGET";
    public static final String INTENT_EXTRA_WIDGET_ID = "WIDGET_ID";

    public static final String DATE_FORMAT_WIDGET_KOREA = "yyyy년 MM월 dd일";
    public static final String DATE_FORMAT_WIDGET_USA = "MM/dd/yyyy";
    public static final String DATE_FORMAT_WIDGET_UK = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MAIN_KOREA = "yyyy년 MM월 dd일 hh:mm a";
    public static final String DATE_FORMAT_MAIN_USA = "MM/dd/yyyy hh:mm a";
    public static final String DATE_FORMAT_MAIN_UK = "dd/MM/yyyy hh:mm a";

    public static final int BROWSER_TYPE_OPEN_EXTERNAL = 1;
    public static final int BROWSER_TYPE_SAVE_EXTERNAL_NONE_OPENEDFILE = 2;

    public enum BrowserType
    {
        OpenExternal, SaveExternalNoneOpenFile
    }

    public enum BrowserMenuSortType
    {
        Asc, Des
    }

    public enum BrowserIconType
    {
        None, Folder, Normal, Over1, Over2
    }

    public enum EncodeType
    {
        EUCKR, UTF8
    }

    public enum PaintType
    {
        Brush, Eraser
    }

    public enum ActiveScreenType
    {
        Main, Font, Help
    }
}
