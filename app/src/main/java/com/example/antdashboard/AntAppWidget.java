package com.example.antdashboard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.antdashboard.models.Activity;
import com.example.antdashboard.presenters.ActivityPresenter;
import com.example.antdashboard.repo.TTSBuilder;
import com.example.antdashboard.utils.Tools;
import com.example.antdashboard.views.ActivityContract;
import com.physicaloid.lib.Physicaloid;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import static android.widget.Toast.makeText;

/**
 * Implementation of App Widget functionality.
 */
public class AntAppWidget extends AppWidgetProvider  {


    public static final String START = "start";
    public static final String STOP = "stop";

    private static final String TAG = "AntAppWidget";
    public static ComponentName thisWidget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, DashboardActivity.class);
        thisWidget = new ComponentName(context, AntAppWidget.class);

        Intent serviceIntent = new Intent(context, AntAppWidget.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent startIntent = new Intent(context, AntAppWidget.class);
        startIntent.setAction(START);

        PendingIntent buttonPendingIntent = PendingIntent.getBroadcast(context,
                0, startIntent, 0);


        Intent stopIntent = new Intent(context, AntAppWidget.class);
        stopIntent.setAction(STOP);

        PendingIntent offBtnIntent = PendingIntent.getBroadcast(context,
                0, stopIntent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ant_app_widget);
        views.setOnClickPendingIntent(R.id.switchbtn, buttonPendingIntent);
        views.setOnClickPendingIntent(R.id.offbtn, offBtnIntent);



        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ant_app_widget);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

//        switch (intent.getAction()){
//            case START:
//                views.setViewVisibility(R.id.switchbtn, View.GONE);
//
//                views.setViewVisibility(R.id.offbtn, View.VISIBLE);
//                manager.updateAppWidget(thisWidget, views);
//                DashboardActivity.onClickWrite(null, "1");
//                break;
//            case STOP:
//                views.setViewVisibility(R.id.switchbtn, View.VISIBLE);
//                views.setViewVisibility(R.id.offbtn, View.GONE);
//                manager.updateAppWidget(thisWidget, views);
//                DashboardActivity.onClickWrite(null, "0");
//                break;
//        }


    }

}