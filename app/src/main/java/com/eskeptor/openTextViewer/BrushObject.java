package com.eskeptor.openTextViewer;

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by narut on 2017-03-14.
 */
public class BrushObject
{
    ArrayList<Float> brushSizes;
    ArrayList<Path> brushPaths;
    ArrayList<Integer> brushColor;
    int brushPathsIdx;

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
