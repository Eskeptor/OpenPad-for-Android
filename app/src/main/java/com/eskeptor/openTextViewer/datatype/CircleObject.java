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
     * @param x x
     * @param y y
     * @param r radius
     */
    public CircleObject(final float x, final float y, final float r) {
        mX = x;
        mY = y;
        mRadius = r;
    }
}
