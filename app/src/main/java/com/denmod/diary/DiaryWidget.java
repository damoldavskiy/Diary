package com.denmod.diary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DiaryWidget extends AppWidgetProvider {

    public static final String NOTE = "NOTE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.diary_widget);

        Intent intent = new Intent(context, DiaryWidget.class);
        intent.setAction(NOTE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.fast_note, pendingIntent);

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
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NOTE)) {
            Note note = new Note(context.getResources().getString(R.string.widget_note_default) + " " + Utility.getDate());
            Group group = new Group(context.getResources().getString(R.string.widget_group_default));
            group.add(note);

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra(MainActivity.NOTE, note);
            context.startActivity(activityIntent);
        } else
            super.onReceive(context, intent);
    }
}

