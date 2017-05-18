package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class MainFileObject
{
    public String title;
    public String url;
    public String context_oneline;
    public String date;
    public int type;
    public boolean isLinkedWidget;

    public MainFileObject(final File _file, final String _txtFileNoName, final String _imgName,
                          final SimpleDateFormat _format, final boolean _Linked)
    {
        if(_file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION))
        {
            type = Constant.LISTVIEW_FILE_TYPE_IMAGE;
        }
        else
        {
            type = Constant.LISTVIEW_FILE_TYPE_TEXT;
        }

        isLinkedWidget = _Linked;

        if(type == Constant.LISTVIEW_FILE_TYPE_IMAGE)
        {
            title = _imgName;
            url = _file.getPath();
            date = _format.format(new Date(_file.lastModified()));
        }
        else
        {
            FileReader fr = null;
            BufferedReader br = null;
            String line;
            try{
                fr = new FileReader(_file);
                br = new BufferedReader(fr);
                if((line = br.readLine()) != null)
                {
                    title = line;
                }
                else
                {
                    title = _txtFileNoName;
                }
                if((line = br.readLine()) != null)
                {
                    context_oneline = line;
                }
                else
                {
                    context_oneline = "";
                }

                url = _file.getPath();
                date = _format.format(new Date(_file.lastModified()));
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
