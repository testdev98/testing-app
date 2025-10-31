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
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                URL url = new URL("https://www.nseindia.com/api/allIndices");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JSONObject json = new JSONObject(content.toString());
                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject index = data.getJSONObject(i);
                    if (index.getString("indexSymbol").equals("NIFTY 50")) {
                        String price = index.getString("last");
                        String open = index.getString("open");
                        String high = index.getString("high");
                        String low = index.getString("low");
                        String close = index.getString("previousClose");

                        for (int appWidgetId : appWidgetIds) {
                            RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
                            views.setTextViewText(R.id.nifty_price, price);
                            views.setTextViewText(R.id.nifty_open, "Open: " + open);
                            views.setTextViewText(R.id.nifty_high, "High: " + high);
                            views.setTextViewText(R.id.nifty_low, "Low: " + low);
                            views.setTextViewText(R.id.nifty_close, "Close: " + close);
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                        break;
                    }
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
