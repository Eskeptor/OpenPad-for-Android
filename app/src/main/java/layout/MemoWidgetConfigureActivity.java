package layout;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.eskeptor.openTextViewer.Constant;
import com.eskeptor.openTextViewer.R;

/**
 * The configuration screen for the {@link MemoWidget MemoWidget} AppWidget.
 */
public class MemoWidgetConfigureActivity extends Activity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private SharedPreferences mSharedPref;
    private SeekBar mTab1BackSeekRed;
    private int mTab1CurBackRed;
    private int mTab1CurBackRed_backup;
    private SeekBar mTab1BackSeekGreen;
    private int mTab1CurBackGreen;
    private int mTab1CurBackGreen_backup;
    private SeekBar mTab1BackSeekBlue;
    private int mTab1CurBackBlue;
    private int mTab1CurBackBlue_backup;
    private SeekBar mTab1FontSeekRed;
    private int mTab1CurFontRed;
    private int mTab1CurFontRed_backup;
    private SeekBar mTab1FontSeekGreen;
    private int mTab1CurFontGreen;
    private int mTab1CurFontGreen_backup;
    private SeekBar mTab1FontSeekBlue;
    private int mTab1CurFontBlue;
    private int mTab1CurFontBlue_backup;
    private SeekBar mTab2BackSeekRed;
    private int mTab2CurBackRed;
    private int mTab2CurBackRed_backup;
    private SeekBar mTab2BackSeekGreen;
    private int mTab2CurBackGreen;
    private int mTab2CurBackGreen_backup;
    private SeekBar mTab2BackSeekBlue;
    private int mTab2CurBackBlue;
    private int mTab2CurBackBlue_backup;
    private SeekBar mTab2FontSeekRed;
    private int mTab2CurFontRed;
    private int mTab2CurFontRed_backup;
    private SeekBar mTab2FontSeekGreen;
    private int mTab2CurFontGreen;
    private int mTab2CurFontGreen_backup;
    private SeekBar mTab2FontSeekBlue;
    private int mTab2CurFontBlue;
    private int mTab2CurFontBlue_backup;
    private TextView mTab1BackTxtRed;
    private TextView mTab1BackTxtGreen;
    private TextView mTab1BackTxtBlue;
    private TextView mTab1FontTxtRed;
    private TextView mTab1FontTxtGreen;
    private TextView mTab1FontTxtBlue;
    private TextView mTab2BackTxtRed;
    private TextView mTab2BackTxtGreen;
    private TextView mTab2BackTxtBlue;
    private TextView mTab2FontTxtRed;
    private TextView mTab2FontTxtGreen;
    private TextView mTab2FontTxtBlue;

    private TextView mPreviewTxtTitle;
    private TextView mPreviewTxtContext;
    private LinearLayout mPreviewMainLayout;
    private FrameLayout mPreviewTitleLayout;
    private FrameLayout mPreviewContextLayout;

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;

    public MemoWidgetConfigureActivity() {
        super();
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constant.APP_WIDGET_PREFERENCE, MODE_PRIVATE).edit();
        prefs.remove(Constant.APP_WIDGET_PREFERENCE + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.memo_widget_configure);

        TabHost tabHost = findViewById(R.id.widget_config_tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1).setIndicator(getString(R.string.widget_config_customize_tap1_title));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2).setIndicator(getString(R.string.widget_config_customize_tap2_title));
        tabHost.addTab(spec2);

        mTab1BackSeekRed = findViewById(R.id.widget_config_tab1_back_seekRed);
        mTab1BackSeekGreen = findViewById(R.id.widget_config_tab1_back_seekGreen);
        mTab1BackSeekBlue = findViewById(R.id.widget_config_tab1_back_seekBlue);
        mTab1BackTxtRed = findViewById(R.id.widget_config_tab1_back_txtRed);
        mTab1BackTxtGreen = findViewById(R.id.widget_config_tab1_back_txtGreen);
        mTab1BackTxtBlue = findViewById(R.id.widget_config_tab1_back_txtBlue);
        mTab1FontSeekRed = findViewById(R.id.widget_config_tab1_font_seekRed);
        mTab1FontSeekGreen = findViewById(R.id.widget_config_tab1_font_seekGreen);
        mTab1FontSeekBlue = findViewById(R.id.widget_config_tab1_font_seekBlue);
        mTab1FontTxtRed = findViewById(R.id.widget_config_tab1_font_txtRed);
        mTab1FontTxtGreen = findViewById(R.id.widget_config_tab1_font_txtGreen);
        mTab1FontTxtBlue = findViewById(R.id.widget_config_tab1_font_txtBlue);

        mTab2BackSeekRed = findViewById(R.id.widget_config_tab2_back_seekRed);
        mTab2BackSeekGreen = findViewById(R.id.widget_config_tab2_back_seekGreen);
        mTab2BackSeekBlue = findViewById(R.id.widget_config_tab2_back_seekBlue);
        mTab2BackTxtRed = findViewById(R.id.widget_config_tab2_back_txtRed);
        mTab2BackTxtGreen = findViewById(R.id.widget_config_tab2_back_txtGreen);
        mTab2BackTxtBlue = findViewById(R.id.widget_config_tab2_back_txtBlue);
        mTab2FontSeekRed = findViewById(R.id.widget_config_tab2_font_seekRed);
        mTab2FontSeekGreen = findViewById(R.id.widget_config_tab2_font_seekGreen);
        mTab2FontSeekBlue = findViewById(R.id.widget_config_tab2_font_seekBlue);
        mTab2FontTxtRed = findViewById(R.id.widget_config_tab2_font_txtRed);
        mTab2FontTxtGreen = findViewById(R.id.widget_config_tab2_font_txtGreen);
        mTab2FontTxtBlue = findViewById(R.id.widget_config_tab2_font_txtBlue);

        mPreviewMainLayout = findViewById(R.id.widget_config_preview_mainlayout);
        mPreviewTitleLayout = findViewById(R.id.widget_config_preview_titlelayout);
        mPreviewContextLayout = findViewById(R.id.widget_config_preview_contextlayout);

        mPreviewTxtTitle = findViewById(R.id.widget_config_preview_memodate);
        mPreviewTxtContext = findViewById(R.id.widget_config_preview_memocontext);

        mSharedPref = getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + mAppWidgetId, MODE_PRIVATE);
        mTab1CurBackRed = mSharedPref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_RED, Constant.WIDGET_TITLE_BACK_COLOR_RED_DEFAULT);
        mTab1CurBackGreen = mSharedPref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_GREEN, Constant.WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT);
        mTab1CurBackBlue = mSharedPref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_BLUE, Constant.WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT);
        mTab1CurFontRed = mSharedPref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_RED, Constant.WIDGET_TITLE_FONT_COLOR_RED_DEFAULT);
        mTab1CurFontGreen = mSharedPref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_GREEN, Constant.WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT);
        mTab1CurFontBlue = mSharedPref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_BLUE, Constant.WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT);
        mTab2CurBackRed = mSharedPref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_RED, Constant.WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT);
        mTab2CurBackGreen = mSharedPref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN, Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT);
        mTab2CurBackBlue = mSharedPref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE, Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT);
        mTab2CurFontRed = mSharedPref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_RED, Constant.WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT);
        mTab2CurFontGreen = mSharedPref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN, Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT);
        mTab2CurFontBlue = mSharedPref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE, Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT);
        mTab1CurBackRed_backup = mTab1CurBackRed;
        mTab1CurBackGreen_backup = mTab1CurBackGreen;
        mTab1CurBackBlue_backup = mTab1CurBackBlue;
        mTab1CurFontRed_backup = mTab1CurFontRed;
        mTab1CurFontGreen_backup = mTab1CurFontGreen;
        mTab1CurFontBlue_backup = mTab1CurFontBlue;
        mTab2CurBackRed_backup = mTab2CurBackRed;
        mTab2CurBackGreen_backup = mTab2CurBackGreen;
        mTab2CurBackBlue_backup = mTab2CurBackBlue;
        mTab2CurFontRed_backup = mTab2CurFontRed;
        mTab2CurFontGreen_backup = mTab2CurFontGreen;
        mTab2CurFontBlue_backup = mTab2CurFontBlue;

        mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
        mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
        mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
        mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
        mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));


        mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                switch (id) {
                    case R.id.widget_config_tab1_back_seekRed:
                        mTab1BackTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                        break;
                    case R.id.widget_config_tab1_back_seekGreen:
                        mTab1BackTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                        break;
                    case R.id.widget_config_tab1_back_seekBlue:
                        mTab1BackTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                        break;
                    case R.id.widget_config_tab1_font_seekRed:
                        mTab1FontTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                        break;
                    case R.id.widget_config_tab1_font_seekGreen:
                        mTab1FontTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                        break;
                    case R.id.widget_config_tab1_font_seekBlue:
                        mTab1FontTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                        break;
                    case R.id.widget_config_tab2_back_seekRed:
                        mTab2BackTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                        break;
                    case R.id.widget_config_tab2_back_seekGreen:
                        mTab2BackTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                        break;
                    case R.id.widget_config_tab2_back_seekBlue:
                        mTab2BackTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                        break;
                    case R.id.widget_config_tab2_font_seekRed:
                        mTab2FontTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                        break;
                    case R.id.widget_config_tab2_font_seekGreen:
                        mTab2FontTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                        break;
                    case R.id.widget_config_tab2_font_seekBlue:
                        mTab2FontTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int id = seekBar.getId();
                switch (id) {
                    case R.id.widget_config_tab1_back_seekRed:
                        mTab1CurBackRed = seekBar.getProgress();
                        mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        break;
                    case R.id.widget_config_tab1_back_seekGreen:
                        mTab1CurBackGreen = seekBar.getProgress();
                        mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        break;
                    case R.id.widget_config_tab1_back_seekBlue:
                        mTab1CurBackBlue = seekBar.getProgress();
                        mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                        break;
                    case R.id.widget_config_tab1_font_seekRed:
                        mTab1CurFontRed = seekBar.getProgress();
                        mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
                        break;
                    case R.id.widget_config_tab1_font_seekGreen:
                        mTab1CurFontGreen = seekBar.getProgress();
                        mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
                        break;
                    case R.id.widget_config_tab1_font_seekBlue:
                        mTab1CurFontBlue = seekBar.getProgress();
                        mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
                        break;
                    case R.id.widget_config_tab2_back_seekRed:
                        mTab2CurBackRed = seekBar.getProgress();
                        mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
                        break;
                    case R.id.widget_config_tab2_back_seekGreen:
                        mTab2CurBackGreen = seekBar.getProgress();
                        mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
                        break;
                    case R.id.widget_config_tab2_back_seekBlue:
                        mTab2CurBackBlue = seekBar.getProgress();
                        mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
                        break;
                    case R.id.widget_config_tab2_font_seekRed:
                        mTab2CurFontRed = seekBar.getProgress();
                        mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));
                        break;
                    case R.id.widget_config_tab2_font_seekGreen:
                        mTab2CurFontGreen = seekBar.getProgress();
                        mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));
                        break;
                    case R.id.widget_config_tab2_font_seekBlue:
                        mTab2CurFontBlue = seekBar.getProgress();
                        mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));
                        break;
                }
            }
        };

        mTab1BackSeekRed.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab1BackSeekGreen.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab1BackSeekBlue.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab1FontSeekRed.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab1FontSeekGreen.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab1FontSeekBlue.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2BackSeekRed.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2BackSeekGreen.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2BackSeekBlue.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2FontSeekRed.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2FontSeekGreen.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTab2FontSeekBlue.setOnSeekBarChangeListener(mSeekBarChangeListener);

        mTab1BackSeekRed.setProgress(mTab1CurBackRed);
        mTab1BackSeekGreen.setProgress(mTab1CurBackGreen);
        mTab1BackSeekBlue.setProgress(mTab1CurBackBlue);
        mTab1FontSeekRed.setProgress(mTab1CurFontRed);
        mTab1FontSeekGreen.setProgress(mTab1CurFontGreen);
        mTab1FontSeekBlue.setProgress(mTab1CurFontBlue);
        mTab2BackSeekRed.setProgress(mTab2CurBackRed);
        mTab2BackSeekGreen.setProgress(mTab2CurBackGreen);
        mTab2BackSeekBlue.setProgress(mTab2CurBackBlue);
        mTab2FontSeekRed.setProgress(mTab2CurFontRed);
        mTab2FontSeekGreen.setProgress(mTab2CurFontGreen);
        mTab2FontSeekBlue.setProgress(mTab2CurFontBlue);

        // Find the widget id from the intent.

    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.widget_config_btnReset:
                mTab1CurBackRed = Constant.WIDGET_TITLE_BACK_COLOR_RED_DEFAULT;
                mTab1CurBackGreen = Constant.WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT;
                mTab1CurBackBlue = Constant.WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT;
                mTab1CurFontRed = Constant.WIDGET_TITLE_FONT_COLOR_RED_DEFAULT;
                mTab1CurFontGreen = Constant.WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT;
                mTab1CurFontBlue = Constant.WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT;
                mTab2CurBackRed = Constant.WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT;
                mTab2CurBackGreen = Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT;
                mTab2CurBackBlue = Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT;
                mTab2CurFontRed = Constant.WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT;
                mTab2CurFontGreen = Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT;
                mTab2CurFontBlue = Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT;
                mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
                mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
                mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));
                mTab1BackSeekRed.setProgress(mTab1CurBackRed);
                mTab1BackSeekGreen.setProgress(mTab1CurBackGreen);
                mTab1BackSeekBlue.setProgress(mTab1CurBackBlue);
                mTab1FontSeekRed.setProgress(mTab1CurFontRed);
                mTab1FontSeekGreen.setProgress(mTab1CurFontGreen);
                mTab1FontSeekBlue.setProgress(mTab1CurFontBlue);
                mTab2BackSeekRed.setProgress(mTab2CurBackRed);
                mTab2BackSeekGreen.setProgress(mTab2CurBackGreen);
                mTab2BackSeekBlue.setProgress(mTab2CurBackBlue);
                mTab2FontSeekRed.setProgress(mTab2CurFontRed);
                mTab2FontSeekGreen.setProgress(mTab2CurFontGreen);
                mTab2FontSeekBlue.setProgress(mTab2CurFontBlue);
                break;
            case R.id.widget_config_btnAdd:
                SharedPreferences.Editor mSharedPrefEditor = mSharedPref.edit();
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_RED, mTab1CurBackRed);
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_GREEN, mTab1CurBackGreen);
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_BLUE, mTab1CurBackBlue);
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_RED, mTab1CurFontRed);
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_GREEN, mTab1CurFontGreen);
                mSharedPrefEditor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_BLUE, mTab1CurFontBlue);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_RED, mTab2CurBackRed);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN, mTab2CurBackGreen);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE, mTab2CurBackBlue);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_RED, mTab2CurFontRed);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN, mTab2CurFontGreen);
                mSharedPrefEditor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE, mTab2CurFontBlue);
                mSharedPrefEditor.putInt(Constant.WIDGET_ID, mAppWidgetId);
                mSharedPrefEditor.apply();
                final Context context = MemoWidgetConfigureActivity.this;

                // When the button is clicked, store the string locally

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                MemoWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                break;
            case R.id.widget_config_btnLast:
                mTab1CurBackRed = mTab1CurBackRed_backup;
                mTab1CurBackGreen = mTab1CurBackGreen_backup;
                mTab1CurBackBlue = mTab1CurBackBlue_backup;
                mTab1CurFontRed = mTab1CurFontRed_backup;
                mTab1CurFontGreen = mTab1CurFontGreen_backup;
                mTab1CurFontBlue = mTab1CurFontBlue_backup;
                mTab2CurBackRed = mTab2CurBackRed_backup;
                mTab2CurBackGreen = mTab2CurBackGreen_backup;
                mTab2CurBackBlue = mTab2CurBackBlue_backup;
                mTab2CurFontRed = mTab2CurFontRed_backup;
                mTab2CurFontGreen = mTab2CurFontGreen_backup;
                mTab2CurFontBlue = mTab2CurFontBlue_backup;
                mPreviewMainLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                mPreviewTitleLayout.setBackgroundColor(Color.rgb(mTab1CurBackRed, mTab1CurBackGreen, mTab1CurBackBlue));
                mPreviewTxtTitle.setTextColor(Color.rgb(mTab1CurFontRed, mTab1CurFontGreen, mTab1CurFontBlue));
                mPreviewContextLayout.setBackgroundColor(Color.rgb(mTab2CurBackRed, mTab2CurBackGreen, mTab2CurBackBlue));
                mPreviewTxtContext.setTextColor(Color.rgb(mTab2CurFontRed, mTab2CurFontGreen, mTab2CurFontBlue));
                mTab1BackSeekRed.setProgress(mTab1CurBackRed);
                mTab1BackSeekGreen.setProgress(mTab1CurBackGreen);
                mTab1BackSeekBlue.setProgress(mTab1CurBackBlue);
                mTab1FontSeekRed.setProgress(mTab1CurFontRed);
                mTab1FontSeekGreen.setProgress(mTab1CurFontGreen);
                mTab1FontSeekBlue.setProgress(mTab1CurFontBlue);
                mTab2BackSeekRed.setProgress(mTab2CurBackRed);
                mTab2BackSeekGreen.setProgress(mTab2CurBackGreen);
                mTab2BackSeekBlue.setProgress(mTab2CurBackBlue);
                mTab2FontSeekRed.setProgress(mTab2CurFontRed);
                mTab2FontSeekGreen.setProgress(mTab2CurFontGreen);
                mTab2FontSeekBlue.setProgress(mTab2CurFontBlue);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPref = null;
        mTab1BackSeekRed = null;
        mTab1BackSeekGreen = null;
        mTab1BackSeekBlue = null;
        mTab1FontSeekRed = null;
        mTab1FontSeekGreen = null;
        mTab1FontSeekBlue = null;
        mTab2BackSeekRed = null;
        mTab2BackSeekGreen = null;
        mTab2BackSeekBlue = null;
        mTab2FontSeekRed = null;
        mTab2FontSeekGreen = null;
        mTab2FontSeekBlue = null;
        mTab1BackTxtRed = null;
        mTab1BackTxtGreen = null;
        mTab1BackTxtBlue = null;
        mTab1FontTxtRed = null;
        mTab1FontTxtGreen = null;
        mTab1FontTxtBlue = null;
        mTab2BackTxtRed = null;
        mTab2BackTxtGreen = null;
        mTab2BackTxtBlue = null;
        mTab2FontTxtRed = null;
        mTab2FontTxtGreen = null;
        mTab2FontTxtBlue = null;
        mPreviewTxtTitle = null;
        mPreviewTxtContext = null;
        mPreviewMainLayout = null;
        mPreviewTitleLayout = null;
        mPreviewContextLayout = null;
        mSeekBarChangeListener = null;
    }

}

