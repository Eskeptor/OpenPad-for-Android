package com.eskeptor.openTextViewer.textManager;

import android.util.Log;

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

/**
 * Created by eskeptor on 17. 1. 25.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
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

    public TextManager() {
        initManager();
    }

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

    public String getFileopen_name() {
        return mFileopenName;
    }

    public boolean isFileopen() {
        return mIsFileopen;
    }

    public String getMD5() {
        return mMD5;
    }

    public boolean saveText(final String _strData, final String _filename, final boolean _enhance) {
        if (_strData == null || _strData.isEmpty()) {
            return false;
        }
        RandomAccessFile randomAccessFile = null;
        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;

        if (isFileopen()) {
            try {
                buffer = ByteBuffer.allocateDirect(_strData.getBytes().length);
                if (_enhance) {
                    randomAccessFile = new RandomAccessFile(new File(mFileopenName), "rw");
                    channel = randomAccessFile.getChannel();
                    randomAccessFile.seek(mPointerList.get(mCurPointer));
                } else {
                    fos = new FileOutputStream(new File(mFileopenName));
                    channel = fos.getChannel();
                }

                buffer.put(_strData.getBytes());
                buffer.flip();
                channel.write(buffer);
            } catch (Exception e) {
                Log.e("TextManager(saveText)", e.getMessage());
            } finally {
                if (buffer != null) {
                    try {
                        buffer.clear();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
            }
        } else {
            try {
                buffer = ByteBuffer.allocateDirect(_strData.getBytes().length);
                if (_enhance) {
                    randomAccessFile = new RandomAccessFile(new File(_filename), "rw");
                    channel = randomAccessFile.getChannel();
                } else {
                    fos = new FileOutputStream(new File(_filename));
                    channel = fos.getChannel();
                }
                buffer.put(_strData.getBytes());
                buffer.flip();
                channel.write(buffer);
            } catch (Exception e) {
                Log.e("TextManager(saveText)", e.getMessage());
            } finally {
                if (buffer != null) {
                    try {
                        buffer.clear();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (Exception e) {
                        Log.e("TextManager(saveText)", e.getMessage());
                    }
                }
            }
        }
        mIsSaved = true;
        return true;
    }

    public String openText(final String _filename, final int _sens, final boolean _enhance, final Constant.EncodeType _format) {
        if (_filename != null) {
            RandomAccessFile randomAccessFile = null;
            FileInputStream fis = null;
            FileChannel channel = null;
            ByteBuffer byteBuffer = null;
            StringBuilder stringBuilder = new StringBuilder();

            int _lines = mLines;

            try {
                if (_enhance) {
                    randomAccessFile = new RandomAccessFile(new File(_filename), "r");
                    channel = randomAccessFile.getChannel();
                    byteBuffer = ByteBuffer.allocateDirect(12);
                    String tmp;
                    mFileSize = randomAccessFile.length();
                    if (randomAccessFile.length() != 0) {
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        byteBuffer.clear();


                        if (mNextPointer == 0) {
                            mPrevPointer = 0;
                            mCurPointer = 0;
                            mPointerList.add(0, 0L);
                            mNextPointer++;
                        } else {
                            if (_sens == Constant.MEMO_BLOCK_NEXT) {
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
                        randomAccessFile.seek(mPointerList.get(mCurPointer));

                        while ((tmp = randomAccessFile.readLine()) != null) {
                            Charset utf = Charset.forName("ISO-8859-1");
                            byteBuffer = utf.encode(tmp);
                            if (_format == Constant.EncodeType.EUCKR) {
                                stringBuilder.append(new String(byteBuffer.array(), Constant.ENCODE_TYPE_EUCKR_STR));
                            } else {
                                stringBuilder.append(new String(byteBuffer.array()));
                            }
                            stringBuilder.append("\n");
                            if ((--_lines) == 0) {
                                break;
                            }
                        }

                        try {
                            mPointerList.get(mNextPointer);
                        } catch (IndexOutOfBoundsException ioobe) {
                            mPointerList.add(mNextPointer, randomAccessFile.getFilePointer());
                        }

                        mIsFileopen = true;
                        mFileopenName = _filename;
                        mMD5 = createMD5(stringBuilder.toString());

                        if (mPointerList.get(mNextPointer) == mFileSize)
                            mProgress = 100.0F;
                        else
                            mProgress = (float) mPointerList.get(mCurPointer) / (float) mFileSize * 100;

                        return new String(stringBuilder);
                    } else {
                        mIsFileopen = false;
                        mFileopenName = "";
                    }
                } else {
                    fis = new FileInputStream(new File(_filename));
                    channel = fis.getChannel();
                    byteBuffer = ByteBuffer.allocateDirect((int) channel.size());
                    if (fis.available() != 0) {
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        if (formatDetector(byteBuffer) != null) {
                            mFileFormat = Constant.ENCODE_TYPE_UTF8_STR;
                        } else {
                            mFileFormat = Constant.ENCODE_TYPE_EUCKR_STR;
                        }
//                        Log.e("Debug", "mFileFormat:" + mFileFormat);
                        mIsFileopen = true;
                        mFileopenName = _filename;
                        mMD5 = createMD5(byteBuffer.array(), false);
                        return new String(byteBuffer.array(), mFileFormat);
                    } else {
                        mIsFileopen = false;
                        mFileopenName = "";
                    }
                }
            } catch (Exception e) {
                Log.e("TextManager(openText)", e.getMessage());
            } finally {
                if (byteBuffer != null) {
                    try {
                        byteBuffer.clear();
                    } catch (Exception e) {
                        Log.e("TextManager(openText)", e.getMessage());
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        Log.e("TextManager(openText)", e.getMessage());
                    }
                }
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (Exception e) {
                        Log.e("TextManager(openText)", e.getMessage());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        Log.e("TextManager(openText)", e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    public String createMD5(final byte[] _message, final boolean _enhance) {
        MessageDigest messageDigest;
        StringBuilder sbuilder = new StringBuilder();
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
                sbuilder.append(String.format("%02x", h & 0xff));
            }
        } catch (Exception e) {
            Log.e("TextManager(createMD5)", e.getMessage());
        } finally {
            if (!_enhance) {
                if (charBuffer != null) {
                    try {
                        charBuffer.clear();
                    } catch (Exception e) {
                        Log.e("TextManager(createMD5)", e.getMessage());
                    }
                }
            }
        }
        return sbuilder.toString();
    }

    public String createMD5(final String _message) {
        MessageDigest messageDigest;
        StringBuilder sbuilder = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            // 타입에 상관없이 UTF8인 이유는 이미 UTF8로 변환되어있는 것을 체크하기 때문
            messageDigest.update(_message.getBytes(Charset.forName(Constant.ENCODE_TYPE_UTF8_STR)));
            byte[] hash = messageDigest.digest();
            for (byte h : hash) {
                sbuilder.append(String.format("%02x", h & 0xff));
            }
        } catch (Exception e) {
            Log.e("TextManager(createMD5)", e.getMessage());
        }
        return sbuilder.toString();
    }

    private CharBuffer formatDetector(final ByteBuffer _buffer) {
        CharBuffer charBuffer = null;
        try {
            CharsetDecoder decoder = Charset.forName(Constant.ENCODE_TYPE_UTF8_STR).newDecoder();
            charBuffer = decoder.decode(_buffer);
        } catch (CharacterCodingException cce) {
            Log.e("TextManager(format-)", cce.getMessage());
            return null;
        }
        return charBuffer;
    }

    public boolean isNext() {
        return mPointerList.get(mNextPointer) != mFileSize;
    }

    public boolean isPrev() {
        return !mPointerList.get(mPrevPointer).equals(mPointerList.get(mCurPointer));
    }

    public void setLines(final int _lines) {
        mLines = _lines;
    }

    public float getProgress() {
        return mProgress;
    }
}
