package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.util.LinkedList;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * 그림메모를 리스트에 표현하기 위한 클래스
 */
public class BrushObject {
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public LinkedList<Float> mBrushSizes;               // 브러쉬크기
    public LinkedList<Object> mBrushPaths;              // 브러쉬의 종류에 따른 Path(일반펜, 원모양, 나머지도형)
    public LinkedList<Integer> mBrushColor;             // 브러쉬의 색
    public LinkedList<Constant.ShapeType> mBrushType;   // 브러쉬의 종류(일반펜, 원모양, 나머지도형)

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
            mBrushSizes = new LinkedList<>();
        }

        if (mBrushColor != null) {
            mBrushColor.clear();
        } else {
            mBrushColor = new LinkedList<>();
        }

        if (mBrushPaths != null) {
            mBrushPaths.clear();
        } else {
            mBrushPaths = new LinkedList<>();
        }

        if (mBrushType != null) {
            mBrushType.clear();
        } else {
            mBrushType = new LinkedList<>();
        }
    }
}