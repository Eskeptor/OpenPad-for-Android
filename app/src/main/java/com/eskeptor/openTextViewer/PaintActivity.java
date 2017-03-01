package com.eskeptor.openTextViewer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class PaintActivity extends AppCompatActivity {
    private PaintFunction paintFunction;
    private LinearLayout drawLayout;

    private int normalColor;
    private int eraserColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        normalColor = Color.BLACK;

        paintFunction = new PaintFunction(getApplicationContext());
        paintFunction.setColor(Color.BLACK);

        drawLayout = (LinearLayout)findViewById(R.id.activity_paint);
        drawLayout.addView(paintFunction);
        drawLayout.setBackgroundColor(Color.WHITE);

        eraserColor = Color.WHITE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_paint_pen)
        {
            paintFunction.setColor(normalColor);
            paintFunction.setMaximumPixel(Constant.PAINT_MINIMUM_PIXEL);
        }
        else if(item.getItemId() == R.id.menu_paint_eraser)
        {
            paintFunction.setColor(eraserColor);
            paintFunction.setMaximumPixel(Constant.PAINT_ERASER_PIXEL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
