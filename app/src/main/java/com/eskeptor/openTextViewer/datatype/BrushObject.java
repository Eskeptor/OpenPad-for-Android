package com.eskeptor.openTextViewer.datatype;

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

// 그림메모를 리스트에 표현하기 위한 클래스
public class BrushObject
{
    // get, set을 이용하면 좋지만 직접접근이 덜 부하를 준다.
    public ArrayList<Float> brushSizes;
    public ArrayList<Path> brushPaths;
    public ArrayList<Integer> brushColor;
    public int brushPathsIdx;

    public BrushObject()
    {
        init();
    }

    public void init()
    {
        if(brushSizes != null)
        {
            brushSizes.clear();
        }
        else
        {
            brushSizes = new ArrayList<>();
        }

        if(brushColor != null)
        {
            brushColor.clear();
        }
        else
        {
            brushColor = new ArrayList<>();
        }

        if(brushPaths != null)
        {
            brushPaths.clear();
        }
        else
        {
            brushPaths = new ArrayList<>();
        }

        brushPathsIdx = 0;
    }
}
