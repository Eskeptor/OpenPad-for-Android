package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.TestLog;

/*
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Class to express notes from the list printed on MainActivity
 */
public class MainFileObject {
    public String mFileTitle;       // Title of the file
    public String mFilePath;        // Absolute path to the file
    public String mOneLinePreview;  // A one-line summary to be printed on the list (text notes are the second line, image notes are the summary printed)
    public String mModifyDate;      // Date modified
    public Constant.FileType mFileType;           // File Type

    /**
     * Generator
     * @param _file Source files
     * @param _txtFileNoName Title to print if the text file does not have a name
     * @param _imgName Image Name
     * @param _locale Locale
     * @param _viewImage Image Preview Status
     */
    public MainFileObject(final File _file, final String _txtFileNoName, final String _imgName,
                          final String _locale, final boolean _viewImage) {
        if (_file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION)) {
            mFileType = Constant.FileType.Image;
        } else {
            mFileType = Constant.FileType.Text;
        }

        if (_locale.equals(Locale.KOREA.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_KOREA, Locale.KOREA).format(new Date(_file.lastModified()));
        else if (_locale.equals(Locale.UK.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_UK, Locale.UK).format(new Date(_file.lastModified()));
        else
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_USA, Locale.US).format(new Date(_file.lastModified()));

        if (mFileType == Constant.FileType.Image) {
            mFileTitle = _imgName;
            mFilePath = _file.getPath();
            File imageSummary = new File(mFilePath + Constant.FILE_IMAGE_SUMMARY);
            if(imageSummary.exists() && !_viewImage) {
                FileReader fr = null;
                BufferedReader br = null;
                String line;
                try {
                    fr = new FileReader(imageSummary);
                    br = new BufferedReader(fr);
                    if ((line = br.readLine()) != null) {
                        mOneLinePreview = line;
                    } else {
                        mOneLinePreview = "";
                    }
                } catch (Exception e) {
                    TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e) {
                            TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                    if (fr != null) {
                        try {
                            fr.close();
                        } catch (Exception e) {
                            TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                }
            } else {
                mOneLinePreview = "";
            }
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
            } catch (Exception e) {
                TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception e) {
                        TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
            }
        }
    }
}
