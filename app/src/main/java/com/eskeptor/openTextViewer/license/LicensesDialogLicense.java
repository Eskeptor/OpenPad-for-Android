package com.eskeptor.openTextViewer.license;

import android.content.Context;

import com.eskeptor.openTextViewer.R;

import de.psdev.licensesdialog.licenses.License;

/**
 * Created by Esk on 2018-03-06.
 *
 */

public class LicensesDialogLicense extends License {
    @Override
    public String getName() {
        return "Licenses Dialog License";
    }

    @Override
    public String readSummaryTextFromResources(Context context) {
        return getContent(context, R.raw.licensesdialog_license_summary);
    }

    @Override
    public String readFullTextFromResources(Context context) {
        return getContent(context, R.raw.apache2_license_full);
    }

    @Override
    public String getVersion() {
        return "1.8.3";
    }

    @Override
    public String getUrl() {
        return "https://github.com/PSDev/LicensesDialog";
    }
}
