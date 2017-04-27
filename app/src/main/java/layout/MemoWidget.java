package layout;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import com.eskeptor.openTextViewer.Constant;
import com.eskeptor.openTextViewer.MemoActivity;
import com.eskeptor.openTextViewer.R;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MemoWidgetConfigureActivity MemoWidgetConfigureActivity}
 */
public class MemoWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        //SharedPreferences.Editor editor = pref.edit();

        int titleBackRed = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_RED, Constant.WIDGET_TITLE_BACK_COLOR_RED_DEFAULT);
        int titleBackGreen = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_GREEN, Constant.WIDGET_TITLE_BACK_COLOR_GREEN_DEFAULT);
        int titleBackBlue = pref.getInt(Constant.WIDGET_TITLE_BACK_COLOR_BLUE, Constant.WIDGET_TITLE_BACK_COLOR_BLUE_DEFAULT);
        int titleFontRed = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_RED, Constant.WIDGET_TITLE_FONT_COLOR_RED_DEFAULT);
        int titleFontGreen = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_GREEN, Constant.WIDGET_TITLE_FONT_COLOR_GREEN_DEFAULT);
        int titleFontBlue = pref.getInt(Constant.WIDGET_TITLE_FONT_COLOR_BLUE, Constant.WIDGET_TITLE_FONT_COLOR_BLUE_DEFAULT);
        int contextFontRed = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_RED, Constant.WIDGET_CONTEXT_FONT_COLOR_RED_DEFAULT);
        int contextFontGreen = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN, Constant.WIDGET_CONTEXT_FONT_COLOR_GREEN_DEFAULT);
        int contextFontBlue = pref.getInt(Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE, Constant.WIDGET_CONTEXT_FONT_COLOR_BLUE_DEFAULT);
        int contextBackRed = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_RED, Constant.WIDGET_CONTEXT_BACK_COLOR_RED_DEFAULT);
        int contextBackGreen = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN, Constant.WIDGET_CONTEXT_BACK_COLOR_GREEN_DEFAULT);
        int contextBackBlue = pref.getInt(Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE, Constant.WIDGET_CONTEXT_BACK_COLOR_BLUE_DEFAULT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memo_widget);

        views.setTextColor(R.id.widget_title, Color.rgb(titleFontRed, titleFontGreen, titleFontBlue));
        views.setInt(R.id.widget_title_layout, "setBackgroundColor", Color.rgb(titleBackRed, titleBackGreen, titleBackBlue));
        views.setTextColor(R.id.widget_context, Color.rgb(contextFontRed, contextFontGreen, contextFontBlue));
        views.setInt(R.id.widget_mainLayout, "setBackgroundColor", Color.rgb(contextBackRed, contextBackGreen, contextBackBlue));

        File file = new File(Constant.APP_WIDGET_URL);
        if(!file.exists())
        {
            file.mkdir();
        }

        Intent intent = new Intent(context, MemoActivity.class);
        intent.putExtra(Constant.INTENT_EXTRA_MEMO_TYPE, Constant.MEMO_TYPE_OPEN_INTERNAL);
        intent.putExtra(Constant.INTENT_EXTRA_MEMO_ISWIDGET, true);
        intent.putExtra(Constant.INTENT_EXTRA_MEMO_OPEN_FOLDERURL, Constant.APP_WIDGET_URL);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
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

