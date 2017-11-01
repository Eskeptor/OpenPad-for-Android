package util;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Esk on 2017-10-07.
 */

public class RawTextOpener {
    public static String getRawText(final Context _context, @RawRes int _res) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String text = "";

        try {
            inputStream = _context.getResources().openRawResource(_res);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while ((i = inputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            text = byteArrayOutputStream.toString();
        } catch (Exception e) {
            Log.e("RawTextOpener", e.getMessage());
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    Log.e("RawTextOpener", e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("RawTextOpener", e.getMessage());
                }
            }
        }
        return text;
    }
}
