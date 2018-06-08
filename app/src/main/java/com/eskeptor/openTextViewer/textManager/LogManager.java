package com.eskeptor.openTextViewer.textManager;

import com.eskeptor.openTextViewer.Constant;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import util.TestLog;

/*
 * Created by narut on 2017-02-20.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Class for Creating Log Files
 */
public class LogManager {

    /**
     * How to Save Logs
     * @param strData Message to save
     * @param fileName Name of log to be saved
     * @return Success or failure
     */
    public static boolean saveLog(final String strData, final String fileName) {
        if (strData == null || strData.isEmpty()) {
            return false;
        }

        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;
        try {
            fos = new FileOutputStream(new File(fileName));
            channel = fos.getChannel();
            buffer = ByteBuffer.allocate(strData.getBytes().length);
            buffer.put(strData.getBytes());
            buffer.flip();
            channel.write(buffer);
        } catch (Exception e) {
            TestLog.Tag("LogManager(saveLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
        } finally {
            if (buffer != null) {
                try {
                    buffer.clear();
                } catch (Exception e) {
                    TestLog.Tag("LogManager(saveLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    TestLog.Tag("LogManager(saveLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    TestLog.Tag("LogManager(saveLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
        }
        return true;
    }

    /**
     * How to import logs
     * @param fileName Name of the log to be recalled
     * @return The contents of the imported log
     */
    public static String openLog(final String fileName) {
        if (fileName != null) {
            FileInputStream fis = null;
            FileChannel channel = null;
            ByteBuffer byteBuffer = null;
            try {
                fis = new FileInputStream(new File(fileName));

                channel = fis.getChannel();
                byteBuffer = ByteBuffer.allocate((int) channel.size());
                if (fis.available() != 0) {
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    return new String(byteBuffer.array()).trim();
                }
            } catch (Exception e) {
                TestLog.Tag("LogManager(openLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
            } finally {
                if (byteBuffer != null) {
                    try {
                        byteBuffer.clear();
                    } catch (Exception e) {
                        TestLog.Tag("LogManager(openLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        TestLog.Tag("LogManager(openLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        TestLog.Tag("LogManager(openLog)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    public static boolean saveKey(final String key) {
        String keyUrl = Constant.APP_INTERNAL_URL + File.separator + "key.opkdc";
        return saveLog(key, keyUrl);
    }
}
