package com.eskeptor.openTextViewer.textManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import util.TestLog;

/*
 * Created by narut on 2017-02-20.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * 로그파일 생성용 클래스
 */
public class LogManager {

    /**
     * 로그를 저장하는 메소드
     * @param _strData 저장할 메시지
     * @param _filename 저장되어질 로그의 이름
     * @return 성공 혹은 실패
     */
    public static boolean saveLog(final String _strData, final String _filename) {
        if (_strData == null || _strData.isEmpty()) {
            return false;
        }

        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;
        try {
            fos = new FileOutputStream(new File(_filename));
            channel = fos.getChannel();
            buffer = ByteBuffer.allocate(_strData.getBytes().length);
            buffer.put(_strData.getBytes());
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
     * 로그를 불러오는 메소드
     * @param _filename 불러올 로그의 이름
     * @return 불러온 로그의 내용
     */
    public static String openLog(final String _filename) {
        if (_filename != null) {
            FileInputStream fis = null;
            FileChannel channel = null;
            ByteBuffer byteBuffer = null;
            try {
                fis = new FileInputStream(new File(_filename));

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
}
