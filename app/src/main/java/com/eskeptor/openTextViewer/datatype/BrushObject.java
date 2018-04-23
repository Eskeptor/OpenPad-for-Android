package com.eskeptor.openTextViewer.datatype;

import com.eskeptor.openTextViewer.Constant;

import java.util.LinkedList;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Classes to represent picture notes to the list
 */
public class BrushObject {
    // Get, set is good, but direct access gives you less load.
    public LinkedList<Float> mBrushSizes;               // Brush Size
    public LinkedList<Object> mBrushPaths;              // Path according to the type of brush (pen, circle, and other shapes)
    public LinkedList<Integer> mBrushColor;             // Brush Color
    public LinkedList<Constant.ShapeType> mBrushType;   // Types of brushes (regular pens, circle shapes, and other shapes)

    /**
     * Generator (execute init upon creation)
     */
    public BrushObject() {
        init();
    }

    /**
     * Initiate all internal fields.
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