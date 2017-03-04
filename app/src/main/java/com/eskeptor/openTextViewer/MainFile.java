package com.eskeptor.openTextViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MainFile
{
    String title;
    String url;
    String context_oneline;
    String date;
    int type;

    public MainFile(final File file, final String txtFileNoName, final String imgName, final SimpleDateFormat format)
    {
        if(file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION))
        {
            type = Constant.LISTVIEW_FILE_TYPE_IMAGE;
        }
        else
        {
            type = Constant.LISTVIEW_FILE_TYPE_TEXT;
        }

        if(type == Constant.LISTVIEW_FILE_TYPE_IMAGE)
        {
            title = imgName;
            url = file.getPath();
            date = format.format(new Date(file.lastModified()));
        }
        else
        {
            FileReader fr = null;
            BufferedReader br = null;
            String line;
            try{
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                if((line = br.readLine()) != null)
                {
                    title = line;
                }
                else
                {
                    title = txtFileNoName;
                }
                if((line = br.readLine()) != null)
                {
                    context_oneline = line;
                }
                else
                {
                    context_oneline = "";
                }

                url = file.getPath();
                date = format.format(new Date(file.lastModified()));
            }
            catch (Exception e){e.printStackTrace();}
            finally {
                try{br.close();}
                catch (Exception e) {e.printStackTrace();}
                try{fr.close();}
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }
}
