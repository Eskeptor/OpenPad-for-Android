package com.eskeptor.openTextViewer.datatype;

import android.util.Log;

import com.eskeptor.openTextViewer.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

// MainActivity에 출력되는 리스트에서 메모들을 표현하기위한 클래스
public class MainFileObject {
    public String mFileTitle;
    public String mFilePath;
    public String mOneLinePreview;
    public String mModifyDate;
    public int mFileType;
    public boolean mIsLinkedWidget;

    public MainFileObject(final File _file, final String _txtFileNoName, final String _imgName,
                          final String _locale, final boolean _Linked) {
        if (_file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION)) {
            mFileType = Constant.LISTVIEW_FILE_TYPE_IMAGE;
        } else {
            mFileType = Constant.LISTVIEW_FILE_TYPE_TEXT;
        }

        mIsLinkedWidget = _Linked;

        if (mFileType == Constant.LISTVIEW_FILE_TYPE_IMAGE) {
            mFileTitle = _imgName;
            mFilePath = _file.getPath();
            //mModifyDate = _format.format(new Date(_file.lastModified()));
            if (_locale.equals(Locale.KOREA.getDisplayCountry()))
                mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_KOREA, Locale.KOREA).format(new Date(_file.lastModified()));
            else if (_locale.equals(Locale.UK.getDisplayCountry()))
                mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_UK, Locale.UK).format(new Date(_file.lastModified()));
            else
                mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_USA, Locale.US).format(new Date(_file.lastModified()));
        } else {
            FileReader fr = null;
            BufferedReader br = null;
            String line;
            try {
                fr = new FileReader(_file);
                br = new BufferedReader(fr);
                if ((line = br.readLine()) != null) {
                    mFileTitle = line;
                } else {
                    mFileTitle = _txtFileNoName;
                }
                if ((line = br.readLine()) != null) {
                    mOneLinePreview = line;
                } else {
                    mOneLinePreview = "";
                }

                mFilePath = _file.getPath();
                if (_locale.equals(Locale.KOREA.getDisplayCountry()))
                    mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_KOREA, Locale.KOREA).format(new Date(_file.lastModified()));
                else if (_locale.equals(Locale.UK.getDisplayCountry()))
                    mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_UK, Locale.UK).format(new Date(_file.lastModified()));
                else
                    mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_USA, Locale.US).format(new Date(_file.lastModified()));
            } catch (Exception e) {
                Log.e("MainFileObject", e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        Log.e("MainFileObject", e.getMessage());
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception e) {
                        Log.e("MainFileObject", e.getMessage());
                    }
                }
            }
        }
    }
}
