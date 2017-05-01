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

    private static final String PREFS_NAME = "layout.MemoWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SeekBar tab1BackSeekRed;    private int tab1CurBackRed;     private int tab1CurBackRed_backup;
    private SeekBar tab1BackSeekGreen;  private int tab1CurBackGreen;   private int tab1CurBackGreen_backup;
    private SeekBar tab1BackSeekBlue;   private int tab1CurBackBlue;    private int tab1CurBackBlue_backup;
    private SeekBar tab1FontSeekRed;    private int tab1CurFontRed;     private int tab1CurFontRed_backup;
    private SeekBar tab1FontSeekGreen;  private int tab1CurFontGreen;   private int tab1CurFontGreen_backup;
    private SeekBar tab1FontSeekBlue;   private int tab1CurFontBlue;    private int tab1CurFontBlue_backup;
    private SeekBar tab2BackSeekRed;    private int tab2CurBackRed;     private int tab2CurBackRed_backup;
    private SeekBar tab2BackSeekGreen;  private int tab2CurBackGreen;   private int tab2CurBackGreen_backup;
    private SeekBar tab2BackSeekBlue;   private int tab2CurBackBlue;    private int tab2CurBackBlue_backup;
    private SeekBar tab2FontSeekRed;    private int tab2CurFontRed;     private int tab2CurFontRed_backup;
    private SeekBar tab2FontSeekGreen;  private int tab2CurFontGreen;   private int tab2CurFontGreen_backup;
    private SeekBar tab2FontSeekBlue;   private int tab2CurFontBlue;    private int tab2CurFontBlue_backup;
    private TextView tab1BackTxtRed;
    private TextView tab1BackTxtGreen;
    private TextView tab1BackTxtBlue;
    private TextView tab1FontTxtRed;
    private TextView tab1FontTxtGreen;
    private TextView tab1FontTxtBlue;
    private TextView tab2BackTxtRed;
    private TextView tab2BackTxtGreen;
    private TextView tab2BackTxtBlue;
    private TextView tab2FontTxtRed;
    private TextView tab2FontTxtGreen;
    private TextView tab2FontTxtBlue;

    private TextView previewTxtTitle;
    private TextView previewTxtContext;
    private LinearLayout previewMainLayout;
    private FrameLayout previewTitleLayout;
    private FrameLayout previewContextLayout;

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;


    /*private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
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
        }
    };*/

    public MemoWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "Test";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        pref = getSharedPreferences(Constant.APP_WIDGET_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();

        setContentView(R.layout.memo_widget_configure);
        //findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        TabHost tabHost = (TabHost)findViewById(R.id.widget_config_tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1).setIndicator(getString(R.string.widget_config_customize_tap1_title));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2).setIndicator(getString(R.string.widget_config_customize_tap2_title));
        tabHost.addTab(spec2);

        tab1BackSeekRed = (SeekBar)findViewById(R.id.widget_config_tab1_back_seekRed);
        tab1BackSeekGreen = (SeekBar)findViewById(R.id.widget_config_tab1_back_seekGreen);
        tab1BackSeekBlue = (SeekBar)findViewById(R.id.widget_config_tab1_back_seekBlue);
        tab1BackTxtRed = (TextView)findViewById(R.id.widget_config_tab1_back_txtRed);
        tab1BackTxtGreen = (TextView)findViewById(R.id.widget_config_tab1_back_txtGreen);
        tab1BackTxtBlue = (TextView)findViewById(R.id.widget_config_tab1_back_txtBlue);
        tab1FontSeekRed = (SeekBar)findViewById(R.id.widget_config_tab1_font_seekRed);
        tab1FontSeekGreen = (SeekBar)findViewById(R.id.widget_config_tab1_font_seekGreen);
        tab1FontSeekBlue = (SeekBar)findViewById(R.id.widget_config_tab1_font_seekBlue);
        tab1FontTxtRed = (TextView)findViewById(R.id.widget_config_tab1_font_txtRed);
        tab1FontTxtGreen = (TextView)findViewById(R.id.widget_config_tab1_font_txtGreen);
        tab1FontTxtBlue = (TextView)findViewById(R.id.widget_config_tab1_font_txtBlue);

        tab2BackSeekRed = (SeekBar)findViewById(R.id.widget_config_tab2_back_seekRed);
        tab2BackSeekGreen = (SeekBar)findViewById(R.id.widget_config_tab2_back_seekGreen);
        tab2BackSeekBlue = (SeekBar)findViewById(R.id.widget_config_tab2_back_seekBlue);
        tab2BackTxtRed = (TextView)findViewById(R.id.widget_config_tab2_back_txtRed);
        tab2BackTxtGreen = (TextView)findViewById(R.id.widget_config_tab2_back_txtGreen);
        tab2BackTxtBlue = (TextView)findViewById(R.id.widget_config_tab2_back_txtBlue);
        tab2FontSeekRed = (SeekBar)findViewById(R.id.widget_config_tab2_font_seekRed);
        tab2FontSeekGreen = (SeekBar)findViewById(R.id.widget_config_tab2_font_seekGreen);
        tab2FontSeekBlue = (SeekBar)findViewById(R.id.widget_config_tab2_font_seekBlue);
        tab2FontTxtRed = (TextView)findViewById(R.id.widget_config_tab2_font_txtRed);
        tab2FontTxtGreen = (TextView)findViewById(R.id.widget_config_tab2_font_txtGreen);
        tab2FontTxtBlue = (TextView)findViewById(R.id.widget_config_tab2_font_txtBlue);

        previewMainLayout = (LinearLayout)findViewById(R.id.widget_config_preview_mainlayout);
        previewTitleLayout = (FrameLayout)findViewById(R.id.widget_config_preview_titlelayout);
        previewContextLayout = (FrameLayout)findViewById(R.id.widget_config_preview_contextlayout);

        previewTxtTitle = (TextView)findViewById(R.id.widget_config_preview_memodate);
        previewTxtContext = (TextView)findViewById(R.id.widget_config_preview_memocontext);

        tab1CurBackRed = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_RED, Constant.WIDGET_TITLE_BACK_COLOR_RED_DEFAULT);
        tab1CurBackGreen = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_GREEN, Constant.WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT);
        tab1CurBackBlue = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_BLUE, Constant.WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT);
        tab1CurFontRed = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_RED, Constant.WIDGET_TITLE_FONT_COLOR_RED_DEFAULT);
        tab1CurFontGreen = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_GREEN, Constant.WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT);
        tab1CurFontBlue = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_BLUE, Constant.WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT);
        tab2CurBackRed = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_RED, Constant.WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT);
        tab2CurBackGreen = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN, Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT);
        tab2CurBackBlue = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE, Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT);
        tab2CurFontRed = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_RED, Constant.WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT);
        tab2CurFontGreen = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN, Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT);
        tab2CurFontBlue = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE, Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT);
        tab1CurBackRed_backup = tab1CurBackRed;
        tab1CurBackGreen_backup = tab1CurBackGreen;
        tab1CurBackBlue_backup = tab1CurBackBlue;
        tab1CurFontRed_backup = tab1CurFontRed;
        tab1CurFontGreen_backup = tab1CurFontGreen;
        tab1CurFontBlue_backup = tab1CurFontBlue;
        tab2CurBackRed_backup = tab2CurBackRed;
        tab2CurBackGreen_backup = tab2CurBackGreen;
        tab2CurBackBlue_backup = tab2CurBackBlue;
        tab2CurFontRed_backup = tab2CurFontRed;
        tab2CurFontGreen_backup = tab2CurFontGreen;
        tab2CurFontBlue_backup = tab2CurFontBlue;

        previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
        previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
        previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
        previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
        previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));

        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar == tab1BackSeekRed)
                {
                    tab1BackTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                }
                if(seekBar == tab1BackSeekGreen)
                {
                    tab1BackTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                }
                if(seekBar == tab1BackSeekBlue)
                {
                    tab1BackTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                }
                if(seekBar == tab1FontSeekRed)
                {
                    tab1FontTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                }
                if(seekBar == tab1FontSeekGreen)
                {
                    tab1FontTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                }
                if(seekBar == tab1FontSeekBlue)
                {
                    tab1FontTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                }
                if(seekBar == tab2BackSeekRed)
                {
                    tab2BackTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                }
                if(seekBar == tab2BackSeekGreen)
                {
                    tab2BackTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                }
                if(seekBar == tab2BackSeekBlue)
                {
                    tab2BackTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                }
                if(seekBar == tab2FontSeekRed)
                {
                    tab2FontTxtRed.setText(String.format(getResources().getString(R.string.paint_txtBrushRed), progress));
                }
                if(seekBar == tab2FontSeekGreen)
                {
                    tab2FontTxtGreen.setText(String.format(getResources().getString(R.string.paint_txtBrushGreen), progress));
                }
                if(seekBar == tab2FontSeekBlue)
                {
                    tab2FontTxtBlue.setText(String.format(getResources().getString(R.string.paint_txtBrushBlue), progress));
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar == tab1BackSeekRed)
                {
                    tab1CurBackRed = seekBar.getProgress();
                    previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                    previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                }
                if(seekBar == tab1BackSeekGreen)
                {
                    tab1CurBackGreen = seekBar.getProgress();
                    previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                    previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                }
                if(seekBar == tab1BackSeekBlue)
                {
                    tab1CurBackBlue = seekBar.getProgress();
                    previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                    previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
                }
                if(seekBar == tab1FontSeekRed)
                {
                    tab1CurFontRed = seekBar.getProgress();
                    previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
                }
                if(seekBar == tab1FontSeekGreen)
                {
                    tab1CurFontGreen = seekBar.getProgress();
                    previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
                }
                if(seekBar == tab1FontSeekBlue)
                {
                    tab1CurFontBlue = seekBar.getProgress();
                    previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
                }
                if(seekBar == tab2BackSeekRed)
                {
                    tab2CurBackRed = seekBar.getProgress();
                    previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
                }
                if(seekBar == tab2BackSeekGreen)
                {
                    tab2CurBackGreen = seekBar.getProgress();
                    previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
                }
                if(seekBar == tab2BackSeekBlue)
                {
                    tab2CurBackBlue = seekBar.getProgress();
                    previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
                }
                if(seekBar == tab2FontSeekRed)
                {
                    tab2CurFontRed = seekBar.getProgress();
                    previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));
                }
                if(seekBar == tab2FontSeekGreen)
                {
                    tab2CurFontGreen = seekBar.getProgress();
                    previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));
                }
                if(seekBar == tab2FontSeekBlue)
                {
                    tab2CurFontBlue = seekBar.getProgress();
                    previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));
                }
            }
        };

        tab1BackSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        tab1BackSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        tab1BackSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);
        tab1FontSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        tab1FontSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        tab1FontSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2BackSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2BackSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2BackSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2FontSeekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2FontSeekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        tab2FontSeekBlue.setOnSeekBarChangeListener(seekBarChangeListener);

        tab1BackSeekRed.setProgress(tab1CurBackRed);
        tab1BackSeekGreen.setProgress(tab1CurBackGreen);
        tab1BackSeekBlue.setProgress(tab1CurBackBlue);
        tab1FontSeekRed.setProgress(tab1CurFontRed);
        tab1FontSeekGreen.setProgress(tab1CurFontGreen);
        tab1FontSeekBlue.setProgress(tab1CurFontBlue);
        tab2BackSeekRed.setProgress(tab2CurBackRed);
        tab2BackSeekGreen.setProgress(tab2CurBackGreen);
        tab2BackSeekBlue.setProgress(tab2CurBackBlue);
        tab2FontSeekRed.setProgress(tab2CurFontRed);
        tab2FontSeekGreen.setProgress(tab2CurFontGreen);
        tab2FontSeekBlue.setProgress(tab2CurFontBlue);

        // Find the widget id from the intent.
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

    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_RED, tab1CurBackRed);
        editor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_GREEN, tab1CurBackGreen);
        editor.putInt(Constant.WIDGET_TITLE_BACK_COLOR_BLUE, tab1CurBackBlue);
        editor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_RED, tab1CurFontRed);
        editor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_GREEN, tab1CurFontGreen);
        editor.putInt(Constant.WIDGET_TITLE_FONT_COLOR_BLUE, tab1CurFontBlue);
        editor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_RED, tab2CurBackRed);
        editor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN, tab2CurBackGreen);
        editor.putInt(Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE, tab2CurBackBlue);
        editor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_RED, tab2CurFontRed);
        editor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN, tab2CurFontGreen);
        editor.putInt(Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE, tab2CurFontBlue);
        editor.commit();
    }

    public void onClick(View v)
    {
        int id = v.getId();
        if(id == R.id.widget_config_btnReset)
        {
            tab1CurBackRed = Constant.WIDGET_TITLE_BACK_COLOR_RED_DEFAULT;
            tab1CurBackGreen = Constant.WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT;
            tab1CurBackBlue = Constant.WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT;
            tab1CurFontRed = Constant.WIDGET_TITLE_FONT_COLOR_RED_DEFAULT;
            tab1CurFontGreen = Constant.WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT;
            tab1CurFontBlue = Constant.WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT;
            tab2CurBackRed = Constant.WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT;
            tab2CurBackGreen = Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT;
            tab2CurBackBlue = Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT;
            tab2CurFontRed = Constant.WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT;
            tab2CurFontGreen = Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT;
            tab2CurFontBlue = Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT;
            previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
            previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
            previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
            previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
            previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));
            tab1BackSeekRed.setProgress(tab1CurBackRed);
            tab1BackSeekGreen.setProgress(tab1CurBackGreen);
            tab1BackSeekBlue.setProgress(tab1CurBackBlue);
            tab1FontSeekRed.setProgress(tab1CurFontRed);
            tab1FontSeekGreen.setProgress(tab1CurFontGreen);
            tab1FontSeekBlue.setProgress(tab1CurFontBlue);
            tab2BackSeekRed.setProgress(tab2CurBackRed);
            tab2BackSeekGreen.setProgress(tab2CurBackGreen);
            tab2BackSeekBlue.setProgress(tab2CurBackBlue);
            tab2FontSeekRed.setProgress(tab2CurFontRed);
            tab2FontSeekGreen.setProgress(tab2CurFontGreen);
            tab2FontSeekBlue.setProgress(tab2CurFontBlue);
        }
        else if(id == R.id.widget_config_btnAdd)
        {
            editor.commit();
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
        }
        else if(id == R.id.widget_config_btnLast)
        {
            tab1CurBackRed = tab1CurBackRed_backup;
            tab1CurBackGreen = tab1CurBackGreen_backup;
            tab1CurBackBlue = tab1CurBackBlue_backup;
            tab1CurFontRed = tab1CurFontRed_backup;
            tab1CurFontGreen = tab1CurFontGreen_backup;
            tab1CurFontBlue = tab1CurFontBlue_backup;
            tab2CurBackRed = tab2CurBackRed_backup;
            tab2CurBackGreen = tab2CurBackGreen_backup;
            tab2CurBackBlue = tab2CurBackBlue_backup;
            tab2CurFontRed = tab2CurFontRed_backup;
            tab2CurFontGreen = tab2CurFontGreen_backup;
            tab2CurFontBlue = tab2CurFontBlue_backup;
            previewMainLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
            previewTitleLayout.setBackgroundColor(Color.rgb(tab1CurBackRed, tab1CurBackGreen, tab1CurBackBlue));
            previewTxtTitle.setTextColor(Color.rgb(tab1CurFontRed, tab1CurFontGreen, tab1CurFontBlue));
            previewContextLayout.setBackgroundColor(Color.rgb(tab2CurBackRed, tab2CurBackGreen, tab2CurBackBlue));
            previewTxtContext.setTextColor(Color.rgb(tab2CurFontRed, tab2CurFontGreen, tab2CurFontBlue));
            tab1BackSeekRed.setProgress(tab1CurBackRed);
            tab1BackSeekGreen.setProgress(tab1CurBackGreen);
            tab1BackSeekBlue.setProgress(tab1CurBackBlue);
            tab1FontSeekRed.setProgress(tab1CurFontRed);
            tab1FontSeekGreen.setProgress(tab1CurFontGreen);
            tab1FontSeekBlue.setProgress(tab1CurFontBlue);
            tab2BackSeekRed.setProgress(tab2CurBackRed);
            tab2BackSeekGreen.setProgress(tab2CurBackGreen);
            tab2BackSeekBlue.setProgress(tab2CurBackBlue);
            tab2FontSeekRed.setProgress(tab2CurFontRed);
            tab2FontSeekGreen.setProgress(tab2CurFontGreen);
            tab2FontSeekBlue.setProgress(tab2CurFontBlue);
        }
    }
}

