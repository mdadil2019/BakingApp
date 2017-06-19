package com.environer.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeProviderWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
//        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_provider_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

//        Intent intent = new Intent(context,MainActivity.class);
//        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWID GET_ID,appWidgetId);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

//        views.setOnClickPendingIntent(R.id.button,pendingIntent);
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
//        Toast.makeText(context, "Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
//        Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
    }
}

