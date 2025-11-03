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
            views.setTextViewText(R.id.nifty_price, "Loading...");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        context.startService(new Intent(context, NiftyWidgetService.class));
    }
}
