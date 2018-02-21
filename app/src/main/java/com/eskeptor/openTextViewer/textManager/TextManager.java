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
 * 텍스트를 불러오는 클래스
 */
public class TextManager {
    public static final int PAGE_PREV = -1;
    public static final int PAGE_NONE = 0;
    public static final int PAGE_NEXT = 1;

    private String mOpenedFileName;           // 현재 열린 파일의 이름
    private String mMD5;                     // 파일의 MD5값
    private String mFileFormat;                  // 파일의 포멧
    private int mLines;                      // 향상된 파일열기에서 출력할 라인

    // 테스트용
    private int mCurPage;
    private int mMaxPage;
    private boolean mFileOpened;
    private ArrayList<String> mContentsList;

    /**
     * 생성과 동시에 initManager(초기화)를 수행합니다.
     */
    public TextManager() {
        initManager();
    }

    /**
     * TextManager를 초기화합니다.
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
     * 텍스트의 내용을 저장합니다.
     * @param _filename 저장할 파일의 이름
     * @param _contents 내용
     * @return 성공시 true, 실패시 false
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
     * 파일을 읽고 쓰기전에 엽니다.
     * @param _filename 열 파일의 이름
     * @return 열었을 때 true, 실패했을 때 false
     */
    public boolean openText(final String _filename) {
        FileInputStream openFileInputStream = null;
        FileChannel openFileChannel = null;
        ByteBuffer fileBuffer = null;
        StringTokenizer tokenizer = null;

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
                // 라인수가 0일때는 위젯이다.
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
     * 열려있는 파일의 해당 페이지의 내용을 포멧에 맞게 반환합니다.
     * @param _page 열 페이지
     * @param _format 파일 포멧
     * @return 해당 페이지의 내용
     */
    public String getText(final int _page, final Constant.EncodeType _format) {
        int page = mCurPage + _page;
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


    /**
     * 열려있는 파일의 이름을 반환
     * @return 파일이름
     */
    public String getOpenedFileName() {
        return mOpenedFileName;
    }


    /**
     * 파일 내용이 변경되었는지를 MD5값으로 판단
     * @return MD5값
     */
    public String getMD5() {
        return mMD5;
    }


    /**
     * 입력된 텍스트의 MD5값을 생성(Byte 방식의 텍스트)
     * @param _message 텍스트
     * @return 성공 혹은 실패
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
     * 입력된 텍스트의 MD5값을 생성(String 방식의 텍스트)
     * @param _message 텍스트
     * @return 성공 혹은 실패
     */
    public String createMD5(final String _message) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            // 타입에 상관없이 UTF8인 이유는 이미 UTF8로 변환되어있는 것을 체크하기 때문
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
     * 파일의 인코딩 포멧 형식을 판단합니다.
     * @param _buffer 입력된 텍스트
     * @return char형 Buffer를 반환합니다.
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
     * 한 번에 보여줄 라인수를 정합니다.(향상된 파일열기 기능)
     * @param _lines 라인수
     */
    public void setLines(final int _lines) {
        mLines = _lines;
    }


    /**
     * 현재 진행 퍼센트를 보여줍니다.
     * @return 퍼센트
     */
    public float getProgress() {
        if (mCurPage == mMaxPage - 1) {
            return 100.0f;
        } else {
            return mCurPage / (mMaxPage - 1);
        }
    }


    /**
     * 현재 열려있는 페이지를 반환합니다.
     * @return 열려있는 페이지
     */
    public int getCurPage() {
        return mCurPage;
    }


    /**
     * 현재 열려있는 파일의 최대 페이지를 반환합니다.(-1을 해서 사용해야함)
     * @return 최대 페이지(-1해야함)
     */
    public int getMaxPage() {
        return mMaxPage;
    }


    /**
     * 현재 파일이 열려있는가 유무를 판단합니다.
     * @return 열려있으면 true, 아니면 false
     */
    public boolean isFileOpened() {
        return mFileOpened;
    }


    /**
     * 열려있는 파일의 내용을 리스트한 객체를 다시 String으로 만들어줍니다.
     * @return 파일의 내용
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
