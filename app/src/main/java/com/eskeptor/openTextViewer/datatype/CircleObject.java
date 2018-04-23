package com.eskeptor.openTextViewer.datatype;

/*
 * Created by Esk on 2017-12-27.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * A circular class
 */
public class CircleObject {
    public float mX;        // X point
    public float mY;        // Y point
    public float mRadius;   // radius

    /**
     * About the generator method
     * @param _x x
     * @param _y y
     * @param _r radius
     */
    public CircleObject(final float _x, final float _y, final float _r) {
        mX = _x;
        mY = _y;
        mRadius = _r;
    }
}
