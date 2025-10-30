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
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NiftyWidgetService extends Service {

    private static final String API_KEY = "d9aa302093a44e03abc43dd755e4b77a";
    private static final String SYMBOL = "NIFTY50ADD";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fetchDataAndUpdateWidget();
        scheduleNextUpdate();
        return START_STICKY;
    }

    private void fetchDataAndUpdateWidget() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.twelvedata.com/time_series?symbol=" + SYMBOL + "&interval=1min&apikey=" + API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JSONObject json = new JSONObject(content.toString());
                if (json.has("values")) {
                    JSONObject latestData = json.getJSONArray("values").getJSONObject(0);
                    String price = latestData.getString("close");
                    String open = latestData.getString("open");
                    String close = latestData.getString("close");

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NiftyWidgetProvider.class));

                    for (int appWidgetId : appWidgetIds) {
                        RemoteViews views = new RemoteViews(getPackageName(), R.layout.nifty_widget_layout);
                        views.setTextViewText(R.id.nifty_price, price);
                        views.setTextViewText(R.id.nifty_open, "Open: " + open);
                        views.setTextViewText(R.id.nifty_close, "Close: " + close);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void scheduleNextUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NiftyWidgetService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, pendingIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
