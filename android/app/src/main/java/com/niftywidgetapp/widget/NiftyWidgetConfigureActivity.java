package com.niftywidgetapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.niftywidgetapp.R;

public class NiftyWidgetConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nifty_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Button lightButton = findViewById(R.id.button_light);
        lightButton.setOnClickListener(v -> selectTheme("light"));

        Button darkButton = findViewById(R.id.button_dark);
        darkButton.setOnClickListener(v -> selectTheme("dark"));

        Button blueButton = findViewById(R.id.button_blue);
        blueButton.setOnClickListener(v -> selectTheme("blue"));
    }

    private void selectTheme(String theme) {
        SharedPreferences.Editor prefs = getSharedPreferences("widget_themes", MODE_PRIVATE).edit();
        prefs.putString("theme_" + appWidgetId, theme);
        prefs.apply();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        NiftyWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);

        finish();
    }
}
