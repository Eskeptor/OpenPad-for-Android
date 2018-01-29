package com.eskeptor.openTextViewer.textManager;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import util.TestLog;

/**
 * Created by Esk on 2017-10-07.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Raw 텍스트를 여는 클래스
 */
public class RawTextManager {
    public static String getRawText(final Context _context, @RawRes int _res) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String text = "";

        try {
            inputStream = _context.getResources().openRawResource(_res);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while ((i = inputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            text = byteArrayOutputStream.toString();
        } catch (Exception e) {
            TestLog.Tag("RawTextManager").Logging(TestLog.ERROR, e.getMessage());
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    TestLog.Tag("RawTextManager").Logging(TestLog.ERROR, e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    TestLog.Tag("RawTextManager").Logging(TestLog.ERROR, e.getMessage());
                }
            }
        }
        return text;
    }
}
