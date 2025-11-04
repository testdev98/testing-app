package com.niftywidgetapp.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
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


    private static final String TAG = "NiftyWidgetService";

    private void fetchDataAndUpdateWidget() {
        new Thread(() -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NiftyWidgetProvider.class));

            try {
                Log.d(TAG, "Fetching Nifty 50 data...");
                String content = NiftyDataFetcher.INSTANCE.getNiftyData();
                Log.d(TAG, "API Response: " + content);
                JSONObject json = new JSONObject(content);
                JSONArray data = json.getJSONArray("data");
                boolean nifty50Found = false;
                for (int i = 0; i < data.length(); i++) {
                    JSONObject index = data.getJSONObject(i);
                    if (index.getString("indexSymbol").equals("NIFTY 50")) {
                        String price = index.getString("last");
                        String open = index.getString("open");
                        String high = index.getString("high");
                        String low = index.getString("low");
                        String close = index.getString("previousClose");
                        String change = index.getString("variation");
                        String percentChange = index.getString("percentChange");

                        for (int appWidgetId : appWidgetIds) {
                            RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
                            views.setTextViewText(R.id.nifty_price, price);
                            views.setTextViewText(R.id.nifty_open, "Open: " + open);
                            views.setTextViewText(R.id.nifty_high, "High: " + high);
                            views.setTextViewText(R.id.nifty_low, "Low: " + low);
                            views.setTextViewText(R.id.nifty_close, "Close: " + close);
                            views.setTextViewText(R.id.nifty_change, change + " (" + percentChange + "%)");
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                        nifty50Found = true;
                        break;
                    }
                }
                if (!nifty50Found) {
                    Log.e(TAG, "NIFTY 50 data not found in API response.");
                    updateWidgetWithError(appWidgetManager, appWidgetIds);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching or parsing data", e);
                updateWidgetWithError(appWidgetManager, appWidgetIds);
            }
        }).start();
    }

    private void updateWidgetWithError(AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
            views.setTextViewText(R.id.nifty_price, "Error");
            views.setTextViewText(R.id.nifty_open, "");
            views.setTextViewText(R.id.nifty_high, "");
            views.setTextViewText(R.id.nifty_low, "");
            views.setTextViewText(R.id.nifty_close, "");
            views.setTextViewText(R.id.nifty_change, "");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMarketOpen()) {
                fetchDataAndUpdateWidget();
                handler.postDelayed(this, 3000);
            } else {
                scheduleNextUpdate();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String CHANNEL_ID = "NiftyWidgetServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Nifty Widget Service",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Nifty Widget")
                .setContentText("Fetching live Nifty 50 data.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(1, notification);

        handler.post(updateRunnable);
        return START_STICKY;
    }

    private void scheduleNextUpdate() {
        long nextMarketOpenTime = getNextMarketOpenTime();
        long currentTime = System.currentTimeMillis();
        long delay = nextMarketOpenTime - currentTime;
        if (delay < 0) {
            delay = 0;
        }
        handler.postDelayed(updateRunnable, delay);
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

        return calendar.getTimeInMillis();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
