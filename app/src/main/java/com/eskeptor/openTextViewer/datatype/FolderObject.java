package com.eskeptor.openTextViewer.datatype;

import android.content.Context;
import com.eskeptor.openTextViewer.Constant;
import com.eskeptor.openTextViewer.R;

import java.io.File;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * 폴더를 리스트에 표현하기 위한 클래스
 */
public class FolderObject {
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public String mFolderName;          // 폴더의 이름(폴더 명만 나옴)
    public String mFolderPath;          // 폴더의 절대경로
    public int mFileCountInFolder;      // 폴더 안에 있는 파일의 개수
    public Constant.FolderType mFolderType;             // 폴더의 타입

    /**
     * 생성자
     * @param _name 폴더의 이름
     * @param _count 폴더안의 파일 개수
     * @param _type 폴더의 타입
     * @param _context 컨텍스트
     */
    public FolderObject(final String _name, final int _count, final Constant.FolderType _type, final Context _context) {
        if (!_name.equals(Constant.FOLDER_DEFAULT_NAME) && !_name.equals(Constant.FOLDER_WIDGET_NAME)) {
            this.mFolderName = _name;
        } else {
            if (_name.equals(Constant.FOLDER_WIDGET_NAME))
                this.mFolderName = _context.getResources().getString(R.string.folder_widget);
            else
                this.mFolderName = _context.getResources().getString(R.string.folder_default);
        }

        this.mFileCountInFolder = _count;
        this.mFolderType = _type;

        if (_context != null) {
            mFolderPath = Constant.APP_INTERNAL_URL + File.separator + _name;
        }
    }
}