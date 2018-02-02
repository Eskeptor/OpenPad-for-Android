package com.eskeptor.openTextViewer.datatype;

/*
 * Created by Esk on 2017-12-27.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * 원을 그리는 브러쉬용 클래스
 */
public class CircleObject {
    public float mX;        // 원의 x좌표
    public float mY;        // 원의 y좌표
    public float mRadius;   // 원의 반지름

    /**
     * 생성자 메소드
     * @param _x x좌표
     * @param _y y좌표
     * @param _r 반지름
     */
    public CircleObject(final float _x, final float _y, final float _r) {
        mX = _x;
        mY = _y;
        mRadius = _r;
    }
}
