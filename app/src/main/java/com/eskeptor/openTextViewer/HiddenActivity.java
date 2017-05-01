package com.eskeptor.openTextViewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class HiddenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden);

        setTitle(R.string.hidden_title);

        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String hiddenText = "";

        try
        {
            inputStream = getResources().openRawResource(R.raw.hidden);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while((i = inputStream.read()) != -1)
            {
                byteArrayOutputStream.write(i);
            }
            hiddenText = byteArrayOutputStream.toString();
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            try{byteArrayOutputStream.close();}
            catch (Exception e){e.printStackTrace();}
            try{inputStream.close();}
            catch (Exception e){e.printStackTrace();}
        }

        TextView hidden = (TextView)findViewById(R.id.hidden_txt);
        hidden.setText(hiddenText);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
