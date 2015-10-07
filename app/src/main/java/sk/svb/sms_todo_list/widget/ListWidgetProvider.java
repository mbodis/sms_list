package sk.svb.sms_todo_list.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.activity.EditNote;
import sk.svb.sms_todo_list.activity.MainActivity;

/**
 * Created by mbodis on 9/15/15.
 */
public class ListWidgetProvider extends AppWidgetProvider {

    public static String TAG = ListWidgetProvider.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context ctx, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate ");

        for (int i=0; i<appWidgetIds.length; i++) {
            Log.d(TAG, "id: " + appWidgetIds[i]);

            RemoteViews widget = new RemoteViews(ctx.getPackageName(), R.layout.widget);

            // buttons
            widget.setOnClickPendingIntent(R.id.add_new_note, getNewNotePendingIntent(ctx));
            widget.setOnClickPendingIntent(R.id.list_notes, showAllNotesPendingIntent(ctx));

            // listview with adapter
            widget.setRemoteAdapter(appWidgetIds[i], R.id.words, getListIntent(ctx, appWidgetIds[i]));
            widget.setPendingIntentTemplate(R.id.words, getListClickPendingIntent(ctx));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.words);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(ctx, appWidgetManager, appWidgetIds);
    }

    private Intent getListIntent(Context ctx, int appWidgetId){
        Intent svcIntent = new Intent(ctx, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        return svcIntent;
    }

    private PendingIntent getNewNotePendingIntent(Context ctx){
        Intent mIntent = new Intent(ctx, EditNote.class);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("new", true);
        mIntent.putExtras(mBundle);

        return PendingIntent.getActivity(ctx, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent showAllNotesPendingIntent(Context ctx){
        Intent mIntent = new Intent(ctx, MainActivity.class);

        return PendingIntent.getActivity(ctx, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getListClickPendingIntent(Context ctx){
        Intent clickIntent = new Intent(ctx, EditNote.class);
        PendingIntent clickPI = PendingIntent
                .getActivity(ctx, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        return clickPI;
    }
}
