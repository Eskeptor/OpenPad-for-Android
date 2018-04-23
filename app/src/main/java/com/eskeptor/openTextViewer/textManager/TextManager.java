package com.eskeptor.openTextViewer.textManager;

import com.eskeptor.openTextViewer.Constant;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.StringTokenizer;

import util.TestLog;

/*
 * Created by eskeptor on 17. 1. 25.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 *
 * Text Manager 2.0Ver Beta
 */

/**
 * class from which the text is called
 */
public class TextManager {
    public static final int PAGE_PREV = -1;
    public static final int PAGE_NONE = 0;
    public static final int PAGE_NEXT = 1;
    public static final int PAGE_FIRST = 2;

    private String mOpenedFileName;         // Name of the currently open file
    private String mMD5;                    // MD5 values for the file
    private String mFileFormat;             // File format
    private int mLines;                     // Line to be output from opening the improved file

    private int mCurPage;
    private int mMaxPage;
    private boolean mFileOpened;
    private ArrayList<String> mContentsList;

    /**
     * Perform InitManager at the same time as creating
     */
    public TextManager() {
        initManager();
    }

    /**
     * Initiate the TextManager
     */
    public void initManager() {
        mOpenedFileName = "";
        mMD5 = "";
        mFileFormat = "";
        mLines = 0;

        if (mContentsList == null)
            mContentsList = new ArrayList<>();
        else
            mContentsList.clear();
        mCurPage = 0;
        mMaxPage = 0;
        mFileOpened = false;
    }

    /**
     * Save the content of the text
     * @param _filename Name of the file to save
     * @param _contents Contents
     * @return Success or failure
     */
    public boolean saveText(final String _filename, final String _contents) {
        if (mContentsList.isEmpty()) {
            mContentsList.add(_contents);
            mMaxPage++;
        } else {
            mContentsList.set(mCurPage, _contents);
        }

        String contents = listToString();
        FileOutputStream saveFileOutputStream = null;
        FileChannel saveFileChannel = null;
        ByteBuffer fileBuffer = null;
        try {
            saveFileOutputStream = new FileOutputStream(new File(_filename));
            saveFileChannel = saveFileOutputStream.getChannel();
            fileBuffer = ByteBuffer.allocate(contents.getBytes().length);
            fileBuffer.put(contents.getBytes());
            fileBuffer.flip();
            saveFileChannel.write(fileBuffer);
            return true;
        } catch (Exception e) {
            if (e.getMessage() != null)
                TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage());
        } finally {
            if (fileBuffer != null) {
                try {
                    fileBuffer.clear();
                } catch (Exception e) {
                    TestLog.Tag("TextManager(saveText)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
            if (saveFileChannel != null) {
                try {
                    saveFileChannel.close();
                } catch (Exception e) {
                    TestLog.Tag("TextManager(saveText)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
            if (saveFileOutputStream != null) {
                try {
                    saveFileOutputStream.close();
                } catch (Exception e) {
                    TestLog.Tag("TextManager(saveText)").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Open the file before you read and write it.
     * @param _filename Name of the column file
     * @return True when opened, false when failed
     */
    public boolean openText(final String _filename) {
        FileInputStream openFileInputStream = null;
        FileChannel openFileChannel = null;
        ByteBuffer fileBuffer = null;
        StringTokenizer tokenizer = null;

        if (!mContentsList.isEmpty()) {
            mContentsList.clear();
        }

        try {
            openFileInputStream = new FileInputStream(new File(_filename));
            openFileChannel = openFileInputStream.getChannel();
            fileBuffer = ByteBuffer.allocate((int)openFileChannel.size());
            if (openFileInputStream.available() != 0) {
                openFileChannel.read(fileBuffer);
                fileBuffer.flip();
                if (formatDetector(fileBuffer) != null) {
                    mFileFormat = Constant.ENCODE_TYPE_UTF8_STR;
                } else {
                    mFileFormat = Constant.ENCODE_TYPE_EUCKR_STR;
                }
                mFileOpened = true;
                mOpenedFileName = _filename;
                tokenizer = new StringTokenizer(new String(fileBuffer.array(), mFileFormat), "\n");
            } else {
                mFileOpened = false;
                mOpenedFileName = "";
            }
        } catch (Exception e) {
            TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage());
        } finally {
            if (fileBuffer != null) {
                try { fileBuffer.clear(); } catch (Exception e) { TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage()); }
            }
            if (openFileChannel != null) {
                try { openFileChannel.close(); } catch (Exception e) { TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage()); }
            }
            if (openFileInputStream != null) {
                try { openFileInputStream.close(); } catch (Exception e) { TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage()); }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            count++;
            stringBuilder.append(tokenizer.nextToken());
            if (mLines == 0) {
                // When the line count is 0, it is a widget
                mLines = Constant.SETTINGS_DEFAULT_VALUE_TEXT_LINES;
            }
            if(count % mLines == 0) {
                mContentsList.add(stringBuilder.toString());
                stringBuilder.setLength(0);
                count = 0;
            } else {
                stringBuilder.append("\n");
            }
        }
        if (stringBuilder.length() != 0) {
            mContentsList.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }
        mMaxPage = mContentsList.size();
        mCurPage = 0;
        return mFileOpened;
    }

    /**
     * Returns the contents of the page that is open in the formate
     * @param _page Which page to open
     * @param _format File format
     * @return The contents of the page
     */
    public String getText(final int _page, final Constant.EncodeType _format) {
        int page;
        if (_page == PAGE_FIRST) {
            page = 0;
        } else {
            page = mCurPage + _page;
        }
        if (page <= mMaxPage - 1 && page >= 0) {
            mCurPage = page;
            if (_format == Constant.EncodeType.EUCKR) {
                String str = "";
                try {
                    str = new String(mContentsList.get(mCurPage).getBytes(), Constant.ENCODE_TYPE_EUCKR_STR);
                } catch (Exception e) {
                    TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage());
                }
                mMD5 = createMD5(str);
                return str;
            } else {
                mMD5 = createMD5(mContentsList.get(mCurPage));
                return mContentsList.get(mCurPage);
            }
        }
        else {
            return "";
        }
    }

    public void setMD5(final String _str, final Constant.EncodeType _format) {
        if (_format == Constant.EncodeType.EUCKR) {
            String str = "";
            try {
                str = new String(mContentsList.get(mCurPage).getBytes(), Constant.ENCODE_TYPE_EUCKR_STR);
            } catch (Exception e) {
                TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage());
            }
            mMD5 = createMD5(str);
        } else {
            mMD5 = createMD5(mContentsList.get(mCurPage));
        }
    }


    /**
     * Return the name of the open file
     * @return File name
     */
    public String getOpenedFileName() {
        return mOpenedFileName;
    }


    /**
     * Return the MD5 value of the currently open file
     * @return MD5
     */
    public String getMD5() {
        return mMD5;
    }


    /**
     * Generates the MD5 values of the typed text (Byte Method Text)
     * @param _message Text
     * @return Success or failure
     */
    public String createMD5(final byte[] _message) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(_message);
            byte[] hash = messageDigest.digest();
            for (byte h : hash) {
                MD5String.append(String.format("%02x", h & 0xff));
            }
        } catch (Exception e) {
            TestLog.Tag("TextManager(createMD5)").Logging(TestLog.LogType.ERROR, e.getMessage());
        }
        TestLog.Tag("TextManager(createMD5)").Logging(TestLog.LogType.DEBUG, "MD5: " + MD5String);
        return MD5String.toString();
    }

    /**
     * Generates the MD5 values of the typed text (String Method Text)
     * @param _message Text
     * @return Success or failure
     */
    public String createMD5(final String _message) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            // UTF-8, regardless of type, is because it checks that it is already converted to UTF-8.
            messageDigest.update(_message.getBytes(Charset.forName(Constant.ENCODE_TYPE_UTF8_STR)));
            byte[] hash = messageDigest.digest();
            for (byte h : hash) {
                MD5String.append(String.format("%02x", h & 0xff));
            }
        } catch (Exception e) {
            TestLog.Tag("TextManager(createMD5)").Logging(TestLog.LogType.ERROR, e.getMessage());
        }
        TestLog.Tag("TextManager(createMD5)").Logging(TestLog.LogType.DEBUG, "MD5: " + MD5String);
        return MD5String.toString();
    }

    /**
     * Determine the encoding format for the file
     * @param _buffer Typed text
     * @return Returns the char type buffer.
     */
    private CharBuffer formatDetector(final ByteBuffer _buffer) {
        CharBuffer charBuffer;
        try {
            CharsetDecoder decoder = Charset.forName(Constant.ENCODE_TYPE_UTF8_STR).newDecoder();
            charBuffer = decoder.decode(_buffer);
        } catch (CharacterCodingException cce) {
            TestLog.Tag("TextManager(format-)").Logging(TestLog.LogType.ERROR, cce.getMessage());
            return null;
        }
        return charBuffer;
    }

    /**
     * Determines the number of lines you want to show at one time.(Enhanced File Open)
     * @param _lines Lines
     */
    public void setLines(final int _lines) {
        mLines = _lines;
    }


    /**
     * Returns the current progress percentage
     * @return Percentage
     */
    public float getProgress() {
        if (mCurPage + 1 == mMaxPage) {
            return 100.0f;
        } else {
            return (float)(mCurPage + 1) / (float)mMaxPage * 100f;
        }
    }


    /**
     * Returns the page that is currently open
     * @return Opened page
     */
    public int getCurPage() {
        return mCurPage;
    }


    /**
     * Returns the maximum page of a file that is currently open.(- 1 must be used)
     * @return Maximum page (- 1 must be done)
     */
    public int getMaxPage() {
        return mMaxPage;
    }


    /**
     * Determine if the file is currently open.
     * @return True if opened, false
     */
    public boolean isFileOpened() {
        return mFileOpened;
    }


    /**
     * Makes the contents of the open file a string of the listed objects
     * @return The contents of a file
     */
    private String listToString() {
        StringBuilder stringBuilder = new StringBuilder();
        int length = mContentsList.size();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(mContentsList.get(i));
            if (i != length - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
