package com.eskeptor.openTextViewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UpdateListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_update_list);

        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String update = "";

        try {
            inputStream = getResources().openRawResource(R.raw.updatelist);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while ((i = inputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            update = byteArrayOutputStream.toString();
        } catch (Exception e) {
            Log.e("UpdateListActivity", e.getMessage());
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    Log.e("UpdateListActivity", e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("UpdateListActivity", e.getMessage());
                }
            }
        }

        TextView updatelist = (TextView) findViewById(R.id.updateList_contents);
        updatelist.setText(update);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}