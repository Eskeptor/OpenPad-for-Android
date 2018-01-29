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

import util.TestLog;

/**
 * Created by eskeptor on 17. 1. 25.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 *
 * Text Manager 1.0Ver
 */


public class TextManager {
    private boolean mIsFileopen;               // 파일이 열렸는지 체크
    private boolean mIsSaved;                  // 파일이 저장되었는지 체크
    private String mFileopenName;           // 현재 열린 파일의 이름
    private String mMD5;                     // 파일의 MD5값
    private String mFileFormat;                  // 파일의 포멧
    private long mFileSize;                  // 파일의 크기

    // 향상된 파일열기용
    private int mPrevPointer;                // 이전 포인터(배열 위치)
    private int mCurPointer;                 // 현재 포인터(배열 위치)
    private int mNextPointer;                // 다음 포인터(배열 위치)
    private ArrayList<Long> mPointerList;    // randomAccessFile의 포인터를 저장할 배열리스트
    private int mLines;                      // 향상된 파일열기에서 출력할 라인
    private float mProgress;                 // 진행률

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
        mIsFileopen = false;
        mFileopenName = "";
        mIsSaved = false;
        mMD5 = "";
        mFileFormat = "";
        mProgress = 0;
        if (mPointerList == null)
            mPointerList = new ArrayList<>();
        else
            mPointerList.clear();
        mPrevPointer = 0;
        mCurPointer = 0;
        mNextPointer = 0;
        mFileSize = 0;
        mLines = 0;
    }

    /**
     * 열려있는 파일의 이름을 반환
     * @return 파일이름
     */
    public String getFileopenName() {
        return mFileopenName;
    }

    /**
     * 현재 파일이 열려있는 여부를 반환
     * @return 파일이 열려있는가
     */
    public boolean isFileopen() {
        return mIsFileopen;
    }

    /**
     * 파일 내용이 변경되었는지를 MD5값으로 판단
     * @return MD5값
     */
    public String getMD5() {
        return mMD5;
    }

    /**
     * 텍스트 내용을 저장합니다.
     * @param _strData 저장할 내용
     * @param _filename 저장될 파일의 이름(절대 경로 포함)
     * @param _enhance 향상된 파일열기 기능 여부
     * @return 파일 내용을 저장했는지 성공 여부
     */
    public boolean saveText(final String _strData, final String _filename, final boolean _enhance) {
        if (_strData == null || _strData.isEmpty()) {
            return false;
        }
        RandomAccessFile saveFile = null;
        FileOutputStream saveFileOutputStream = null;
        FileChannel saveFileChannel = null;
        ByteBuffer fileBuffer = null;
        long backupSize = 0L;

        if (isFileopen()) {
            try {
                fileBuffer = ByteBuffer.allocateDirect(_strData.getBytes().length);
                ByteBuffer backFileBuffer = null;
                if (_enhance) {
                    saveFile = new RandomAccessFile(new File(mFileopenName), "rw");
                    saveFileChannel = saveFile.getChannel();
                    saveFile.seek(mPointerList.get(mCurPointer));

                    // 파일 보기가 여러페이지가 존재하면
                    if(isNext()) {
                        // 저장하는 지점 이후의 부분을 임시적으로 다른 버퍼에 저장한 후
                        backupSize = mPointerList.get(mNextPointer);
                        RandomAccessFile backFile = new RandomAccessFile(new File(mFileopenName), "rw");
                        FileChannel backFileChannel = backFile.getChannel();
                        backFile.seek(backupSize);
                        backupSize = mFileSize - backupSize;
                        backFileBuffer = ByteBuffer.allocateDirect((int)backupSize);
                        backFileChannel.read(backFileBuffer);
                        backFileBuffer.flip();
                        backFileChannel.close();
                        backFile.close();
                    }
                } else {
                    saveFileOutputStream = new FileOutputStream(new File(mFileopenName));
                    saveFileChannel = saveFileOutputStream.getChannel();
                }
                fileBuffer.put(_strData.getBytes());
                fileBuffer.flip();
                saveFileChannel.write(fileBuffer);

                // 새롭게 변경된 부분을 먼저 저장한 이후에 임시적으로 다른 버퍼에 넣어놨던 이후의 부분을 뒷부분에 이어서 저장
                if(_enhance && isNext()) {
                    saveFile.seek(mPointerList.get(mCurPointer) + _strData.getBytes().length);
                    saveFileChannel.write(backFileBuffer);
                    backFileBuffer.clear();
                }
            } catch (Exception e) {
                TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
            } finally {
                if (fileBuffer != null) {
                    try {
                        fileBuffer.clear();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (saveFileChannel != null) {
                    try {
                        saveFileChannel.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (saveFile != null) {
                    try {
                        saveFile.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (saveFileOutputStream != null) {
                    try {
                        saveFileOutputStream.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
            }
        } else {
            try {
                fileBuffer = ByteBuffer.allocateDirect(_strData.getBytes().length);
                if (_enhance) {
                    saveFile = new RandomAccessFile(new File(_filename), "rw");
                    saveFileChannel = saveFile.getChannel();
                } else {
                    saveFileOutputStream = new FileOutputStream(new File(_filename));
                    saveFileChannel = saveFileOutputStream.getChannel();
                }
                fileBuffer.put(_strData.getBytes());
                fileBuffer.flip();
                saveFileChannel.write(fileBuffer);
            } catch (Exception e) {
                TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
            } finally {
                if (fileBuffer != null) {
                    try {
                        fileBuffer.clear();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (saveFileChannel != null) {
                    try {
                        saveFileChannel.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (saveFile != null) {
                    try {
                        saveFile.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(saveText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
            }
        }
        mIsSaved = true;
        return true;
    }

    /**
     * 파일을 열어서 텍스트를 가져옵니다.
     * @param _filename 열 파일의 이름(절대 경로 포함)
     * @param _pointer 향상된 파일열기의 경우 포인터 위치
     * @param _enhance 향상된 파일열기 여부
     * @param _format 파일 포멧(UTF-8 또는 EUC-KR)
     * @return 파일 내용을 열었는지 성공 여부
     */
    public String openText(final String _filename, final int _pointer, final boolean _enhance, final Constant.EncodeType _format) {
        if (_filename != null) {
            RandomAccessFile openFile = null;
            FileInputStream openFileInputStream = null;
            FileChannel openFileChannel = null;
            ByteBuffer fileBuffer = null;
            StringBuilder fileContents = new StringBuilder();

            int lines = mLines;

            try {
                if (_enhance) {
                    openFile = new RandomAccessFile(new File(_filename), "r");
                    String tmp;
                    mFileSize = openFile.length();
                    if (mFileSize != 0) {
                        if (mNextPointer == 0 || _pointer == Constant.MEMO_BLOCK_ENCODING_RESET) {
                            mPrevPointer = 0;
                            mCurPointer = 0;
                            mPointerList.add(0, 0L);
                            mNextPointer = 1;
                        } else {
                            if (_pointer == Constant.MEMO_BLOCK_NEXT) {
                                mPrevPointer = mCurPointer;
                                mCurPointer = mNextPointer;
                                mNextPointer++;
                            } else {
                                mNextPointer = mCurPointer;
                                mCurPointer = mPrevPointer;
                                if (mPrevPointer != 0)
                                    mPrevPointer--;
                            }
                        }
                        openFile.seek(mPointerList.get(mCurPointer));

                        while ((tmp = openFile.readLine()) != null) {
                            Charset utf = Charset.forName("ISO-8859-1");
                            fileBuffer = utf.encode(tmp);
                            if (_format == Constant.EncodeType.EUCKR) {
                                fileContents.append(new String(fileBuffer.array(), Constant.ENCODE_TYPE_EUCKR_STR));
                            } else {
                                fileContents.append(new String(fileBuffer.array()));
                            }
                            fileContents.append("\n");
                            if ((--lines) == 0) {
                                break;
                            }
                        }

                        try {
                            mPointerList.get(mNextPointer);
                        } catch (IndexOutOfBoundsException ioobe) {
                            mPointerList.add(mNextPointer, openFile.getFilePointer());
                        }

                        mIsFileopen = true;
                        mFileopenName = _filename;
                        mMD5 = createMD5(fileContents.toString());

                        if (mPointerList.get(mNextPointer) == mFileSize)
                            mProgress = 100.0F;
                        else
                            mProgress = (float) mPointerList.get(mNextPointer) / (float) mFileSize * 100;

                        TestLog.Tag("Page Pointer").Logging(TestLog.VERBOSE, "prev: " + mPrevPointer);
                        TestLog.Tag("Page Pointer").Logging(TestLog.VERBOSE, "cur: " + mCurPointer);
                        TestLog.Tag("Page Pointer").Logging(TestLog.VERBOSE, "next: " + mNextPointer);
                        TestLog.Tag("Page Pointer").Logging(TestLog.VERBOSE, mCurPointer + " : " + mPointerList.get(mCurPointer));

                        return new String(fileContents);
                    } else {
                        mIsFileopen = false;
                        mFileopenName = "";
                    }
                } else {
                    openFileInputStream = new FileInputStream(new File(_filename));
                    openFileChannel = openFileInputStream.getChannel();
                    fileBuffer = ByteBuffer.allocateDirect((int) openFileChannel.size());
                    if (openFileInputStream.available() != 0) {
                        openFileChannel.read(fileBuffer);
                        fileBuffer.flip();
                        if (formatDetector(fileBuffer) != null) {
                            mFileFormat = Constant.ENCODE_TYPE_UTF8_STR;
                        } else {
                            mFileFormat = Constant.ENCODE_TYPE_EUCKR_STR;
                        }
//                        Log.e("Debug", "mFileFormat:" + mFileFormat);
                        mIsFileopen = true;
                        mFileopenName = _filename;
                        mProgress = 100.0F;
                        mMD5 = createMD5(fileBuffer.array(), false);
                        return new String(fileBuffer.array(), mFileFormat);
                    } else {
                        mIsFileopen = false;
                        mFileopenName = "";
                    }
                }
            } catch (Exception e) {
                TestLog.Tag("TextManager(openText)").Logging(TestLog.ERROR, e.getMessage());
            } finally {
                if (fileBuffer != null) {
                    try {
                        fileBuffer.clear();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(openText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (openFileChannel != null) {
                    try {
                        openFileChannel.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(openText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (openFile != null) {
                    try {
                        openFile.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(openText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
                if (openFileInputStream != null) {
                    try {
                        openFileInputStream.close();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(openText)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    /**
     * 입력된 텍스트의 MD5값을 생성(Byte 방식의 텍스트)
     * @param _message 텍스트
     * @param _enhance 향상된 파일열기 여부
     * @return 성공 혹은 실패
     */
    public String createMD5(final byte[] _message, final boolean _enhance) {
        MessageDigest messageDigest;
        StringBuilder MD5String = new StringBuilder();
        CharBuffer charBuffer = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            if (!_enhance) {
                charBuffer = Charset.forName(mFileFormat).newDecoder().decode(ByteBuffer.wrap(_message));
                messageDigest.update(charBuffer.toString().getBytes());
            } else {
                messageDigest.update(_message);
            }
            byte[] hash = messageDigest.digest();
            for (byte h : hash) {
                MD5String.append(String.format("%02x", h & 0xff));
            }
        } catch (Exception e) {
            TestLog.Tag("TextManager(createMD5)").Logging(TestLog.ERROR, e.getMessage());
        } finally {
            if (!_enhance) {
                if (charBuffer != null) {
                    try {
                        charBuffer.clear();
                    } catch (Exception e) {
                        TestLog.Tag("TextManager(createMD5)").Logging(TestLog.ERROR, e.getMessage());
                    }
                }
            }
        }
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
            TestLog.Tag("TextManager(createMD5)").Logging(TestLog.ERROR, e.getMessage());
        }
        return MD5String.toString();
    }

    /**
     * 파일의 인코딩 포멧 형식을 판단합니다.
     * @param _buffer 입력된 텍스트
     * @return char형 Buffer를 반환합니다.
     */
    private CharBuffer formatDetector(final ByteBuffer _buffer) {
        CharBuffer charBuffer = null;
        try {
            CharsetDecoder decoder = Charset.forName(Constant.ENCODE_TYPE_UTF8_STR).newDecoder();
            charBuffer = decoder.decode(_buffer);
        } catch (CharacterCodingException cce) {
            TestLog.Tag("TextManager(format-)").Logging(TestLog.ERROR, cce.getMessage());
            return null;
        }
        return charBuffer;
    }

    /**
     * 다음 페이지가 있는지 확인합니다.(향상된 파일열기 기능)
     * @return 있다 혹은 없다
     */
    public boolean isNext() {
        return mPointerList.get(mNextPointer) != mFileSize;
    }

    /**
     * 이전 페이지가 있는지 확인합니다.(향상된 파일열기 기능)
     * @return 있다 혹은 없다
     */
    public boolean isPrev() {
        return !mPointerList.get(mPrevPointer).equals(mPointerList.get(mCurPointer));
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
        return mProgress;
    }
}
