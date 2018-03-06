package com.eskeptor.openTextViewer.license;

import android.content.Context;

import com.eskeptor.openTextViewer.R;

import de.psdev.licensesdialog.licenses.License;

/**
 * Created by Esk on 2018-03-05.
 *
 */

public class GlideLicense extends License {

    @Override
    public String getName() {
        return "Glide 4 License";
    }

    @Override
    public String readSummaryTextFromResources(Context context) {
        return getContent(context, R.raw.glide_license_summary);
    }

    @Override
    public String readFullTextFromResources(Context context) {
        return getContent(context, R.raw.glide_license_full);
    }

    @Override
    public String getVersion() {
        return "4.5.0";
    }

    @Override
    public String getUrl() {
        return "https://www.apache.org/licenses/LICENSE-2.0.txt";
    }
}
