package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.util.ArrayList;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * 그림메모를 리스트에 표현하기 위한 클래스
 */
public class BrushObject {
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public ArrayList<Float> mBrushSizes;                // 브러쉬크기
    public ArrayList<Object> mBrushPaths;               // 브러쉬의 종류에 따른 Path(일반펜, 원모양, 나머지도형)
    public ArrayList<Integer> mBrushColor;              // 브러쉬의 색
    public ArrayList<Constant.ShapeType> mBrushType;    // 브러쉬의 종류(일반펜, 원모양, 나머지도형)
    public int mBrushPathsIdx;                          // 브러쉬 인덱스
    // 브러쉬 인덱스는 브러쉬를 이용하여 그릴경우(도형, 지우개 포함) 1씩 증가한다.
    // 처음 그린 브러쉬가 일반이라고 한다면(크기는 10, 색은 검정)
    // mBrushPathsIdx = 0;
    // mBrushSizes[mBrushPathsIdx] = 10;
    // mBrushPaths[mBrushPathsIdx] = new Path(...)
    // mBrushColor[mBrushPathsIdx] = 검정색
    // mBrushType[mBrushPathsIdx] = Constant.ShapeType.NONE
    // mBrushPathsIdx++;


    /**
     * 생성자(생성과 동시에 init 실행)
     */
    public BrushObject() {
        init();
    }

    /**
     * 내부의 모든 필드를 초기화한다.
     */
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