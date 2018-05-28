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
    public enum FileType {
        Text(0), Image(1);

        private final int value;
        FileType(final int _value) {
            value = _value;
        }
        public int getValue() {
            return value;
        }
    }

    public String mFileTitle;       // Title of the file
    public String mFilePath;        // Absolute path to the file
    public String mOneLinePreview;  // A one-line summary to be printed on the list (text notes are the second line, image notes are the summary printed)
    public String mModifyDate;      // Date modified
    public FileType mFileType;           // File Type

    /**
     * Generator
     * @param file Source files
     * @param txtFileNoName Title to print if the text file does not have a name
     * @param imgName Image Name
     * @param locale Locale
     * @param viewImage Image Preview Status
     */
    public MainFileObject(final File file, final String txtFileNoName, final String imgName,
                          final String locale, final boolean viewImage) {
        if (file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION)) {
            mFileType = FileType.Image;
        } else {
            mFileType = FileType.Text;
        }

        if (locale.equals(Locale.KOREA.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_KOREA, Locale.KOREA).format(new Date(file.lastModified()));
        else if (locale.equals(Locale.UK.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_UK, Locale.UK).format(new Date(file.lastModified()));
        else
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_USA, Locale.US).format(new Date(file.lastModified()));

        if (mFileType == FileType.Image) {
            mFileTitle = imgName;
            mFilePath = file.getPath();
            File imageSummary = new File(mFilePath + Constant.FILE_IMAGE_SUMMARY);
            if(imageSummary.exists() && !viewImage) {
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
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                if ((line = br.readLine()) != null) {
                    mFileTitle = line;
                } else {
                    mFileTitle = txtFileNoName;
                }
                if ((line = br.readLine()) != null) {
                    mOneLinePreview = line;
                } else {
                    mOneLinePreview = "";
                }

                mFilePath = file.getPath();
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
