package com.eskeptor.openTextViewer;

import java.io.File;

/**
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class FileObject
{
    String url;
    String name;
    int type;   // Constant에서 BROWSER_ 참조
    long size;  // KB 단위
    public FileObject(final File file)
    {
        url = file.getPath();
        name = file.getName();

        if(file.isDirectory())
        {
            type = Constant.BROWSER_IMAGE_TYPE_FOLDER;
        }
        else if(file.isFile())
        {
            if(file.length() >= Constant.KILOBYTE * Constant.SAFE_LOAD_CAPACITY &&
                    file.length() <= Constant.MEGABYTE)
            {
                type = Constant.BROWSER_IMAGE_TYPE_OVER1;
            }
            else if(file.length() >= Constant.MEGABYTE)
            {
                type = Constant.BROWSER_IMAGE_TYPE_OVER2;
            }
            else
            {
                type = Constant.BROWSER_IMAGE_TYPE_NORMAL;
            }
        }

        if(file.length() >= Constant.KILOBYTE)
        {
            size = file.length() / Constant.KILOBYTE;
        }
        else
        {
            size = 1L;
        }
    }
}
