package util;

import android.util.Log;

import com.eskeptor.openTextViewer.BuildConfig;

/**
 * Created by Esk on 2018-01-29.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class TestLog {
    /*public static final int DEBUG = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int VERBOSE = 3;
    public static final int WARN = 4;*/
    public enum LogType {
        DEBUG, ERROR, INFO, VERBOSE, WARN
    }

    private String mTagName;
    public TestLog(final String tagName) {
        mTagName = tagName;
    }
    public static TestLog Tag(final String tagName) {
        return new TestLog(tagName);
    }
    public void Logging(final LogType mode, final String text) {
        if(BuildConfig.DEBUG) {
            switch (mode) {
                case DEBUG:
                    Log.d(mTagName, text);
                    break;
                case ERROR:
                    Log.e(mTagName, text);
                    break;
                case INFO:
                    Log.i(mTagName, text);
                    break;
                case VERBOSE:
                    Log.v(mTagName, text);
                    break;
                case WARN:
                    Log.w(mTagName, text);
                    break;
            }
        }
    }
}
