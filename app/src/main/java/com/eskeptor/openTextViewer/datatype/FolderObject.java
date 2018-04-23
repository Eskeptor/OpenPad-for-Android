package com.eskeptor.openTextViewer.datatype;

import android.content.Context;
import com.eskeptor.openTextViewer.Constant;
import com.eskeptor.openTextViewer.R;

import java.io.File;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Class for representing folders in the list
 */
public class FolderObject {
    // Get, set is good, but direct access gives you less load.
    public String mFolderName;          // Name of the folder (folder only appears)
    public String mFolderPath;          // Absolute path to the folder
    public int mFileCountInFolder;      // The number of files that are inside the folder.
    public Constant.FolderType mFolderType;             // Type of folder

    /**
     * Generator
     * @param _name Name of the folder
     * @param _count Number of files in the folder
     * @param _type Type of folder
     * @param _context Context
     */
    public FolderObject(final String _name, final int _count, final Constant.FolderType _type, final Context _context) {
        if (!_name.equals(Constant.FOLDER_DEFAULT_NAME) && !_name.equals(Constant.FOLDER_WIDGET_NAME)) {
            this.mFolderName = _name;
        } else {
            if (_name.equals(Constant.FOLDER_WIDGET_NAME))
                this.mFolderName = _context.getResources().getString(R.string.folder_widget);
            else
                this.mFolderName = _context.getResources().getString(R.string.folder_default);
        }

        this.mFileCountInFolder = _count;
        this.mFolderType = _type;

        if (_context != null) {
            mFolderPath = Constant.APP_INTERNAL_URL + File.separator + _name;
        }
    }
}