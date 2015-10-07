package sk.svb.sms_todo_list.db_models;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import sk.svb.sms_todo_list.widget.ListWidgetProvider;

/**
 * Created by mbodis on 9/15/15.
 */
public class MyServiceHelper {

    public static void refreshWidgets(Context ctx){
        Intent intent = new Intent(ctx, ListWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
//        int[] ids = {R.xml.my_widget};
        int ids[] = AppWidgetManager.getInstance(ctx).getAppWidgetIds(new ComponentName(ctx, ListWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        ctx.sendBroadcast(intent);
    }
}
