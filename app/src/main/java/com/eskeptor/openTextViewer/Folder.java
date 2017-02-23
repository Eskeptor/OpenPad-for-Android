package com.eskeptor.openTextViewer;

import android.content.Context;
import java.io.File;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class Folder
{
    String name;
    String url;
    int count;
    int type;
    public Folder(final String name, final int count, final int type, final Context context)
    {
        if(!name.equals(Constant.FOLDER_DEFAULT_NAME))
        {
            this.name = name;
        }
        else
        {
            this.name = context.getResources().getString(R.string.folder_default);
        }

        this.count = count;
        this.type = type;

        if(context != null)
        {
            url = Constant.APP_INTERNAL_URL + File.separator + name;
        }
    }
}
