package com.eskeptor.openTextViewer.license;

import android.content.Context;

import com.eskeptor.openTextViewer.R;

import de.psdev.licensesdialog.licenses.License;

/**
 * Created by Esk on 2018-03-05.
 *
 */

public class BmJUALicense extends License {
    @Override
    public String getName() {
        return "BM-JUA License";
    }

    @Override
    public String readSummaryTextFromResources(Context context) {
        return getContent(context, R.raw.bmjua_license_summary);
    }

    @Override
    public String readFullTextFromResources(Context context) {
        return getContent(context, R.raw.bmjua_license_full);
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getUrl() {
        return "http://www.woowahan.com";
    }
}
