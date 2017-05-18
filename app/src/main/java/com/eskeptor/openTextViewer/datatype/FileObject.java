package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.io.File;

/**
 * Created by eskeptor on 17. 2. 14.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class FileObject
{
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public String url;
    public String name;
    public int type;   // Constant에서 BROWSER_ 참조
    public long size;  // KB 단위

    public FileObject(final File _file)
    {
        url = _file.getPath();
        name = _file.getName();

        if(_file.isDirectory())
        {
            type = Constant.BROWSER_IMAGE_TYPE_FOLDER;
        }
        else if(_file.isFile())
        {
            if(_file.length() >= Constant.KILOBYTE * Constant.SAFE_LOAD_CAPACITY &&
                    _file.length() <= Constant.MEGABYTE)
            {
                type = Constant.BROWSER_IMAGE_TYPE_OVER1;
            }
            else if(_file.length() >= Constant.MEGABYTE)
            {
                type = Constant.BROWSER_IMAGE_TYPE_OVER2;
            }
            else
            {
                type = Constant.BROWSER_IMAGE_TYPE_NORMAL;
            }
        }

        if(_file.length() >= Constant.KILOBYTE)
        {
            size = _file.length() / Constant.KILOBYTE;
        }
        else
        {
            size = 1L;
        }
    }
}
