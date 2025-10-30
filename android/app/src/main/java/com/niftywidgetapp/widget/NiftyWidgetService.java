package com.niftywidgetapp.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.RemoteViews;
import com.niftywidgetapp.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.Calendar;
import java.util.TimeZone;

public class NiftyWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fetchDataAndUpdateWidget();
        scheduleNextUpdate();
        return START_STICKY;
    }

    private void fetchDataAndUpdateWidget() {
        new Thread(() -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NiftyWidgetProvider.class));

            try {
                Document doc = Jsoup.connect("https://www.nseindia.com/")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .get();
                Elements priceElement = doc.select("#tab1_container > div.clearfix.nifty_tab_content > div:nth-child(1) > p > span.tb_val");
                Elements openElement = doc.select("div.symbol_open h3");
                Elements highElement = doc.select("div.symbol_high h3");
                Elements lowElement = doc.select("div.symbol_low h3");
                Elements closeElement = doc.select("p.symbol_Pclose > span");

                String price = priceElement.first().text();
                String open = openElement.first().text();
                String high = highElement.first().text();
                String low = lowElement.first().text();
                String close = closeElement.first().text();

                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
                    views.setTextViewText(R.id.nifty_price, price);
                    views.setTextViewText(R.id.nifty_open, "Open: " + open);
                    views.setTextViewText(R.id.nifty_high, "High: " + high);
                    views.setTextViewText(R.id.nifty_low, "Low: " + low);
                    views.setTextViewText(R.id.nifty_close, "Close: " + close);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            } catch (Exception e) {
                e.printStackTrace();
                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
                    views.setTextViewText(R.id.nifty_price, "Error");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        }).start();
    }

    private void scheduleNextUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NiftyWidgetService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long triggerAtMillis;
        if (isMarketOpen()) {
            triggerAtMillis = SystemClock.elapsedRealtime() + 5000;
        } else {
            triggerAtMillis = getNextMarketOpenTime();
        }

        alarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtMillis, pendingIntent);
    }

    private boolean isMarketOpen() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hour > 9 || (hour == 9 && minute >= 15)) && (hour < 15 || (hour == 15 && minute < 30));
    }

    private long getNextMarketOpenTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (dayOfWeek == Calendar.FRIDAY && hour >= 15) {
            calendar.add(Calendar.DATE, 3);
        } else if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DATE, 2);
        } else if (hour >= 15) {
            calendar.add(Calendar.DATE, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - System.currentTimeMillis() + SystemClock.elapsedRealtime();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
