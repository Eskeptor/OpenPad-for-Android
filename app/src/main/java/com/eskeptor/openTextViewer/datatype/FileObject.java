package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.io.File;

/*
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Class for expressing the file list in the list
 */
public class FileObject {
    // Get, set is good, but direct access gives you less load.
    public String mFilePath;                    // Absolute path to the file
    public String mFileName;                    // Name of file (file name only : 1.png)
    public Constant.BrowserIconType mIconType;  // Type of file icon
    public long mFileSize;                      // File Size (in KB)

    /**
     * Generator
     * @param _file file
     */
    public FileObject(final File _file) {
        mFilePath = _file.getPath();
        mFileName = _file.getName();

        if (_file.isDirectory()) {
            mIconType = Constant.BrowserIconType.Folder;
        } else if (_file.isFile()) {
            if (_file.length() >= Constant.KILOBYTE * Constant.SAFE_LOAD_CAPACITY &&
                    _file.length() <= Constant.MEGABYTE) {
                mIconType = Constant.BrowserIconType.Over1;
            } else if (_file.length() >= Constant.MEGABYTE) {
                mIconType = Constant.BrowserIconType.Over2;
            } else {
                mIconType = Constant.BrowserIconType.Normal;
            }
        }

        if (_file.length() >= Constant.KILOBYTE) {
            mFileSize = _file.length() / Constant.KILOBYTE;
        } else {
            mFileSize = 1L;
        }
    }
}