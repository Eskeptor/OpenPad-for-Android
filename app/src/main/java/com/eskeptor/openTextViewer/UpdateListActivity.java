package com.eskeptor.openTextViewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import util.RawTextOpener;

public class UpdateListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_update_list);

        TextView updateList = (TextView) findViewById(R.id.updateList_contents);
        updateList.setText(RawTextOpener.getRawText(getApplicationContext(), R.raw.updatelist));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}