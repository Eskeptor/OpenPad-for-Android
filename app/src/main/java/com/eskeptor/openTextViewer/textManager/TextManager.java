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
    public enum EncodeType {
        EucKR, UTF8
    }

    public static final String ENCODE_TYPE_EUCKR_STR = "EUC-KR";
    public static final String ENCODE_TYPE_UTF8_STR = "UTF-8";
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
     * @param fileName Name of the file to save
     * @param contents Contents
     * @return Success or failure
     */
    public boolean saveText(final String fileName, final String contents) {
        if (mContentsList.isEmpty()) {
            mContentsList.add(contents);
            mMaxPage++;
        } else {
            mContentsList.set(mCurPage, contents);
        }

        String strContents = listToString();
        FileOutputStream saveFileOutputStream = null;
        FileChannel saveFileChannel = null;
        ByteBuffer fileBuffer = null;
        try {
            saveFileOutputStream = new FileOutputStream(new File(fileName));
            saveFileChannel = saveFileOutputStream.getChannel();
            fileBuffer = ByteBuffer.allocate(strContents.getBytes().length);
            fileBuffer.put(strContents.getBytes());
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
     * @param fileName Name of the column file
     * @return True when opened, false when failed
     */
    public boolean openText(final String fileName) {
        FileInputStream openFileInputStream = null;
        FileChannel openFileChannel = null;
        ByteBuffer fileBuffer = null;
        StringTokenizer tokenizer = null;

        if (!mContentsList.isEmpty()) {
            mContentsList.clear();
        }

        try {
            openFileInputStream = new FileInputStream(new File(fileName));
            openFileChannel = openFileInputStream.getChannel();
            fileBuffer = ByteBuffer.allocate((int)openFileChannel.size());
            if (openFileInputStream.available() != 0) {
                openFileChannel.read(fileBuffer);
                fileBuffer.flip();
                if (formatDetector(fileBuffer) != null) {
                    mFileFormat = ENCODE_TYPE_UTF8_STR;
                } else {
                    mFileFormat = ENCODE_TYPE_EUCKR_STR;
                }
                mFileOpened = true;
                mOpenedFileName = fileName;
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
     * @param page Which page to open
     * @param format File format
     * @return The contents of the page
     */
    public String getText(final int page, final EncodeType format) {
        int thisPage;
        if (page == PAGE_FIRST) {
            thisPage = 0;
        } else {
            thisPage = mCurPage + page;
        }
        if (thisPage <= mMaxPage - 1 && thisPage >= 0) {
            mCurPage = thisPage;
            if (format == EncodeType.EucKR) {
                String str = "";
                try {
                    str = new String(mContentsList.get(mCurPage).getBytes(), ENCODE_TYPE_EUCKR_STR);
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

    public void setMD5(final String str, final EncodeType format) {
        if (format == EncodeType.EucKR) {
            String origin = "";
            try {
                origin = new String(mContentsList.get(mCurPage).getBytes(), ENCODE_TYPE_EUCKR_STR);
            } catch (Exception e) {
                TestLog.Tag("TextManager").Logging(TestLog.LogType.ERROR, e.getMessage());
            }
            mMD5 = createMD5(origin);
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
     * @param message Text
     * @return Success or failure
     */
    public String createMD5(final byte[] message) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(message);
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
     * @param message Text
     * @return Success or failure
     */
    public String createMD5(final String message) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            // UTF-8, regardless of type, is because it checks that it is already converted to UTF-8.
            messageDigest.update(message.getBytes(Charset.forName(ENCODE_TYPE_UTF8_STR)));
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
     * @param buffer Typed text
     * @return Returns the char type buffer.
     */
    private CharBuffer formatDetector(final ByteBuffer buffer) {
        CharBuffer charBuffer;
        try {
            CharsetDecoder decoder = Charset.forName(ENCODE_TYPE_UTF8_STR).newDecoder();
            charBuffer = decoder.decode(buffer);
        } catch (CharacterCodingException cce) {
            TestLog.Tag("TextManager(format-)").Logging(TestLog.LogType.ERROR, cce.getMessage());
            return null;
        }
        return charBuffer;
    }

    /**
     * Determines the number of lines you want to show at one time.(Enhanced File Open)
     * @param lines Lines
     */
    public void setLines(final int lines) {
        mLines = lines;
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
