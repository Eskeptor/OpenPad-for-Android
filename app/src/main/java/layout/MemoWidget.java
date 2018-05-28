package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;
import com.eskeptor.openTextViewer.Constant;
import com.eskeptor.openTextViewer.MemoActivity;
import com.eskeptor.openTextViewer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.TestLog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MemoWidgetConfigureActivity MemoWidgetConfigureActivity}
 */
public class MemoWidget extends AppWidgetProvider {
    public static final String WIDGET_TITLE_FONT_COLOR_RED = "widget_title_font_color_r_Pref";
    public static final String WIDGET_TITLE_FONT_COLOR_GREEN = "widget_title_font_color_g_Pref";
    public static final String WIDGET_TITLE_FONT_COLOR_BLUE = "widget_title_font_color_b_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_RED = "widget_title_back_color_r_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_GREEN = "widget_title_back_color_g_Pref";
    public static final String WIDGET_TITLE_BACK_COLOR_BLUE = "widget_title_back_color_b_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_RED = "widget_context_font_color_r_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_GREEN = "widget_context_font_color_g_Pref";
    public static final String WIDGET_CONTEXT_FONT_COLOR_BLUE = "widget_context_font_color_b_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_RED = "widget_context_back_color_r_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_GREEN = "widget_context_back_color_g_Pref";
    public static final String WIDGET_CONTEXT_BACK_COLOR_BLUE = "widget_context_back_color_b_Pref";
    public static final String WIDGET_FILE_URL = "widget_file_url";
    public static final String WIDGET_ID = "widget_id";
    public static final int WIDGET_MAX_LINE = 20;

    public static final int WIDGET_TITLE_FONT_COLOR_RED_DEFAULT = 1;
    public static final int WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT = 1;
    public static final int WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT = 1;
    public static final int WIDGET_TITLE_BACK_COLOR_RED_DEFAULT = 239;
    public static final int WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT = 239;
    public static final int WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT = 239;
    public static final int WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT = 20;
    public static final int WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT = 255;
    public static final int WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT = 255;
    public static final int WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT = 255;


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_WIDGET_PREFERENCE + appWidgetId, MODE_PRIVATE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memo_widget);

        File file = new File(Constant.APP_WIDGET_URL);
        if (!file.exists()) {
            if (file.mkdir()) {
                TestLog.Tag("MemoWidget(update)").Logging(TestLog.LogType.DEBUG, "Make directory - Widget Folder");
            }
        }
        String fileURL = pref.getString(WIDGET_FILE_URL, null);

        Intent intent = new Intent(context, MemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, true);
        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, Constant.APP_WIDGET_URL);
        intent.putExtra(Constant.INTENT_EXTRA_WIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_mainLayout, pendingIntent);

        int titleBackRed = pref.getInt(WIDGET_TITLE_BACK_COLOR_RED, WIDGET_TITLE_BACK_COLOR_RED_DEFAULT);
        int titleBackGreen = pref.getInt(WIDGET_TITLE_BACK_COLOR_GREEN, WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT);
        int titleBackBlue = pref.getInt(WIDGET_TITLE_BACK_COLOR_BLUE, WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT);
        int titleFontRed = pref.getInt(WIDGET_TITLE_FONT_COLOR_RED, WIDGET_TITLE_FONT_COLOR_RED_DEFAULT);
        int titleFontGreen = pref.getInt(WIDGET_TITLE_FONT_COLOR_GREEN, WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT);
        int titleFontBlue = pref.getInt(WIDGET_TITLE_FONT_COLOR_BLUE, WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT);
        int contextFontRed = pref.getInt(WIDGET_CONTEXT_FONT_COLOR_RED, WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT);
        int contextFontGreen = pref.getInt(WIDGET_CONTEXT_FONT_COLOR_GREEN, WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT);
        int contextFontBlue = pref.getInt(WIDGET_CONTEXT_FONT_COLOR_BLUE, WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT);
        int contextBackRed = pref.getInt(WIDGET_CONTEXT_BACK_COLOR_RED, WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT);
        int contextBackGreen = pref.getInt(WIDGET_CONTEXT_BACK_COLOR_GREEN, WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT);
        int contextBackBlue = pref.getInt(WIDGET_CONTEXT_BACK_COLOR_BLUE, WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT);

        views.setTextColor(R.id.widget_title, Color.rgb(titleFontRed, titleFontGreen, titleFontBlue));
        views.setInt(R.id.widget_title_layout, "setBackgroundColor", Color.rgb(titleBackRed, titleBackGreen, titleBackBlue));
        views.setTextColor(R.id.widget_context, Color.rgb(contextFontRed, contextFontGreen, contextFontBlue));
        views.setInt(R.id.widget_mainLayout, "setBackgroundColor", Color.rgb(contextBackRed, contextBackGreen, contextBackBlue));

        if (fileURL != null) {
            FileReader fr = null;
            BufferedReader br = null;
            int currentLine = 0;
            final String currentCountry = Locale.getDefault().getDisplayCountry();
            String line;
            String title = "";
            String contents = "";
            try {
                fr = new FileReader(fileURL);
                br = new BufferedReader(fr);

                if (currentCountry.equals(Locale.KOREA.getDisplayCountry()))
                    title = new SimpleDateFormat(Constant.DATE_FORMAT_WIDGET_KOREA, Locale.KOREA).format(new Date(new File(fileURL).lastModified()));
                else if (currentCountry.equals(Locale.UK.getDisplayCountry()))
                    title = new SimpleDateFormat(Constant.DATE_FORMAT_WIDGET_UK, Locale.UK).format(new Date(new File(fileURL).lastModified()));
                else
                    title = new SimpleDateFormat(Constant.DATE_FORMAT_WIDGET_USA, Locale.US).format(new Date(new File(fileURL).lastModified()));
                while (currentLine < WIDGET_MAX_LINE) {
                    if ((line = br.readLine()) != null) {
                        contents += line + "\n";
                        currentLine++;
                    } else
                        break;
                }
            } catch (Exception e) {
                TestLog.Tag("MemoWidget(update)").Logging(TestLog.LogType.ERROR, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        TestLog.Tag("MemoWidget(update)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception e) {
                        TestLog.Tag("MemoWidget(update)").Logging(TestLog.LogType.ERROR, e.getMessage());
                    }
                }
            }
            views.setTextViewText(R.id.widget_title, title);
            views.setTextViewText(R.id.widget_context, contents);
        }



        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MemoWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}