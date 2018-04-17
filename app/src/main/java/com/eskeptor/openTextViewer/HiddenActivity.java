package com.eskeptor.openTextViewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.eskeptor.openTextViewer.textManager.RawTextManager;

/**
 * H i d d e n
 */
public class HiddenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_hidden);

        setTitle(R.string.hidden_title);

        TextView hidden = findViewById(R.id.hidden_txt);
        hidden.setText(RawTextManager.getRawText(getApplicationContext(), R.raw.hidden));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}