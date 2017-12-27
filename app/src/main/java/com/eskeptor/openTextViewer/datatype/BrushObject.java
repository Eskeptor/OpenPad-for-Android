package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

// 그림메모를 리스트에 표현하기 위한 클래스
public class BrushObject {
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public ArrayList<Float> mBrushSizes;
    public ArrayList<Object> mBrushPaths;
    public ArrayList<Integer> mBrushColor;
    public ArrayList<Constant.ShapeType> mBrushType;
    public int mBrushPathsIdx;

    public BrushObject() {
        init();
    }

    public void init() {
        if (mBrushSizes != null) {
            mBrushSizes.clear();
        } else {
            mBrushSizes = new ArrayList<>();
        }

        if (mBrushColor != null) {
            mBrushColor.clear();
        } else {
            mBrushColor = new ArrayList<>();
        }

        if (mBrushPaths != null) {
            mBrushPaths.clear();
        } else {
            mBrushPaths = new ArrayList<>();
        }

        if (mBrushType != null) {
            mBrushType.clear();
        } else {
            mBrushType = new ArrayList<>();
        }

        mBrushPathsIdx = 0;
    }
}