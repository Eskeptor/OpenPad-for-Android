package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.TestLog;

/*
 * Created by eskeptor on 17. 2. 4.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * MainActivity에 출력되는 리스트에서 메모들을 표현하기위한 클래스
 */
public class MainFileObject {
    public String mFileTitle;       // 파일의 제목
    public String mFilePath;        // 파일의 절대경로
    public String mOneLinePreview;  // 리스트에 출력할 한줄 요약(텍스트메모는 2번째줄, 이미지메모는 요약이 출력)
    public String mModifyDate;      // 수정한 날짜
    public Constant.FileType mFileType;           // 파일의 타입

    /**
     * 생성자
     * @param _file 원본 파일
     * @param _txtFileNoName 텍스트 파일의 이름이 없을 시 출력할 타이틀
     * @param _imgName 이미지 이름
     * @param _locale 로케일
     * @param _viewImage 이미지 미리보기 여부
     */
    public MainFileObject(final File _file, final String _txtFileNoName, final String _imgName,
                          final String _locale, final boolean _viewImage) {
        if (_file.getName().endsWith(Constant.FILE_IMAGE_EXTENSION)) {
            mFileType = Constant.FileType.Image;
        } else {
            mFileType = Constant.FileType.Text;
        }

        if (_locale.equals(Locale.KOREA.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_KOREA, Locale.KOREA).format(new Date(_file.lastModified()));
        else if (_locale.equals(Locale.UK.getDisplayCountry()))
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_UK, Locale.UK).format(new Date(_file.lastModified()));
        else
            mModifyDate = new SimpleDateFormat(Constant.DATE_FORMAT_MAIN_USA, Locale.US).format(new Date(_file.lastModified()));

        if (mFileType == Constant.FileType.Image) {
            mFileTitle = _imgName;
            mFilePath = _file.getPath();
            File imageSummary = new File(mFilePath + Constant.FILE_IMAGE_SUMMARY);
            if(imageSummary.exists() && !_viewImage) {
                FileReader fr = null;
                BufferedReader br = null;
                String line;
                try {
                    fr = new FileReader(imageSummary);
                    br = new BufferedReader(fr);
                    if ((line = br.readLine()) != null) {
                        mOneLinePreview = line;
                    } else {
                        mOneLinePreview = "";
                    }
                } catch (Exception e) {
                    TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e) {
                            TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                    if (fr != null) {
                        try {
                            fr.close();
                        } catch (Exception e) {
                            TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                        }
                    }
                }
            } else {
                mOneLinePreview = "";
            }
        } else {
            FileReader fr = null;
            BufferedReader br = null;
            String line;
            try {
                fr = new FileReader(_file);
                br = new BufferedReader(fr);
                if ((line = br.readLine()) != null) {
                    mFileTitle = line;
                } else {
                    mFileTitle = _txtFileNoName;
                }
                if ((line = br.readLine()) != null) {
                    mOneLinePreview = line;
                } else {
                    mOneLinePreview = "";
                }

                mFilePath = _file.getPath();
            } catch (Exception e) {
                TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception e) {
                        TestLog.Tag("MainFileObject").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
            }
        }
    }
}
