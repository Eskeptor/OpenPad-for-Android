package com.eskeptor.openTextViewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import util.RawTextOpener;

public class HiddenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_hidden);

        setTitle(R.string.hidden_title);

        TextView hidden = (TextView) findViewById(R.id.hidden_txt);
        hidden.setText(RawTextOpener.getRawText(getApplicationContext(), R.raw.hidden));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}