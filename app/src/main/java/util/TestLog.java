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
    public TestLog(final String _tagName) {
        mTagName = _tagName;
    }
    public TestLog() {}
    public static TestLog Tag(final String _tagName) {
        return new TestLog(_tagName);
    }
    public void Logging(final LogType _mode, final String _text) {
        if(BuildConfig.DEBUG) {
            switch (_mode) {
                case DEBUG:
                    Log.d(mTagName, _text);
                    break;
                case ERROR:
                    Log.e(mTagName, _text);
                    break;
                case INFO:
                    Log.i(mTagName, _text);
                    break;
                case VERBOSE:
                    Log.v(mTagName, _text);
                    break;
                case WARN:
                    Log.w(mTagName, _text);
                    break;
            }
        }
    }
}
