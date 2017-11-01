package util;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by Esk on 2017-10-07.
 */

public class FontChanger extends Application {
    private Typeface mCustomFont;
    private static String mCustomFontName;

    @Override
    public void onCreate() {
        super.onCreate();
        if(mCustomFontName != null)
            mCustomFont = Typeface.createFromAsset(this.getAssets(), mCustomFontName + ".ttf");
    }

    public static FontChanger getApplication(Context _context, @Nullable final String _fontName) {
        mCustomFontName = _fontName;
        return (FontChanger) _context.getApplicationContext();
    }

    public void setCustomFontView(TextView... _views) {
        if (mCustomFontName != null) {
            for (TextView view : _views) {
                view.setTypeface(mCustomFont);
            }
        }
    }
}
