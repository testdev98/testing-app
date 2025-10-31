package com.niftywidgetapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.niftywidgetapp.R;

public class NiftyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        context.startService(new Intent(context, NiftyWidgetService.class));
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String theme = context.getSharedPreferences("widget_themes", Context.MODE_PRIVATE)
                .getString("theme_" + appWidgetId, "light");
        int layoutId = getLayoutForTheme(theme);

        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        views.setTextViewText(R.id.nifty_price, "Loading...");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static int getLayoutForTheme(String theme) {
        switch (theme) {
            case "dark":
                return R.layout.nifty_widget_layout_dark;
            case "blue":
                return R.layout.nifty_widget_layout_blue;
            default:
                return R.layout.nifty_widget_layout;
        }
    }
}
