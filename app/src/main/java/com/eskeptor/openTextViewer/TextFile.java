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

public class TextFile {
    String title;
    String url;
    String context_oneline;
    String date;

    public TextFile(final File file, final String noname, final SimpleDateFormat format)
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
                title = noname;
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
