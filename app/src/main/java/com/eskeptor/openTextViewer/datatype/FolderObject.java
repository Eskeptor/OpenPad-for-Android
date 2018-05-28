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
    public enum FolderType {
        Default, Custom, External
    }

    // Get, set is good, but direct access gives you less load.
    public String mFolderName;          // Name of the folder (folder only appears)
    public String mFolderPath;          // Absolute path to the folder
    public int mFileCountInFolder;      // The number of files that are inside the folder.
    public FolderType mFolderType;             // Type of folder

    /**
     * Generator
     * @param name Name of the folder
     * @param count Number of files in the folder
     * @param type Type of folder
     * @param context Context
     */
    public FolderObject(final String name, final int count, final FolderType type, final Context context) {
        if (!name.equals(Constant.FOLDER_DEFAULT_NAME) && !name.equals(Constant.FOLDER_WIDGET_NAME)) {
            this.mFolderName = name;
        } else {
            if (name.equals(Constant.FOLDER_WIDGET_NAME))
                this.mFolderName = context.getResources().getString(R.string.folder_widget);
            else
                this.mFolderName = context.getResources().getString(R.string.folder_default);
        }

        this.mFileCountInFolder = count;
        this.mFolderType = type;

        if (context != null) {
            mFolderPath = Constant.APP_INTERNAL_URL + File.separator + name;
        }
    }
}