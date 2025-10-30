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
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nifty_widget_layout);
            views.setTextViewText(R.id.nifty_price, "12345.67");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, NiftyWidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, NiftyWidgetService.class));
    }
}
