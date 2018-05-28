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
    public enum BrowserIconType {
        None, Folder, Normal, Over1, Over2
    }

    private static final long MEGABYTE = 1048576L;
    private static final long KILOBYTE = 1024L;
    private static final long SAFE_LOAD_CAPACITY = 500L;

    // Get, set is good, but direct access gives you less load.
    public String mFilePath;                    // Absolute path to the file
    public String mFileName;                    // Name of file (file name only : 1.png)
    public BrowserIconType mIconType;  // Type of file icon
    public long mFileSize;                      // File Size (in KB)

    /**
     * Generator
     * @param file file
     */
    public FileObject(final File file) {
        mFilePath = file.getPath();
        mFileName = file.getName();

        if (file.isDirectory()) {
            mIconType = BrowserIconType.Folder;
        } else if (file.isFile()) {
            if (file.length() >= KILOBYTE * SAFE_LOAD_CAPACITY &&
                    file.length() <= MEGABYTE) {
                mIconType = BrowserIconType.Over1;
            } else if (file.length() >= MEGABYTE) {
                mIconType = BrowserIconType.Over2;
            } else {
                mIconType = BrowserIconType.Normal;
            }
        }

        if (file.length() >= KILOBYTE) {
            mFileSize = file.length() / KILOBYTE;
        } else {
            mFileSize = 1L;
        }
    }
}