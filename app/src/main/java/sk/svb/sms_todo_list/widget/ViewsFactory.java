package sk.svb.sms_todo_list.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.db_models.DbNoteModel;
import sk.svb.sms_todo_list.logic.Note;

/**
 * Created by mbodis on 9/15/15.
 */
public class ViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private final String TAG = ViewsFactory.class.getName();

    private Context ctx = null;
    private static List<Note> items;
    private int appWidgetId;

    public ViewsFactory(Context ctx, Intent intent) {
        Log.d(TAG, "ViewsFactory Create");

        this.ctx = ctx;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        items = DbNoteModel.getNotes(ctx, false);
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return(items.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctx.getPackageName(), R.layout.widget_row);

        // note
        if (items.get(position).adapterType == Note.TYPE_NOTE){

            row.setViewVisibility(R.id.text3, View.GONE);
            row.setViewVisibility(R.id.ll, View.VISIBLE);

            row.setTextViewText(android.R.id.text1, items.get(position).getMessage());
            row.setViewVisibility(android.R.id.text1, View.VISIBLE);

            if (items.get(position).getTitle() != null && items.get(position).getTitle().length() > 0){
                row.setTextViewText(android.R.id.text2, items.get(position).getTitle());
                row.setViewVisibility(android.R.id.text2, View.VISIBLE);
            }else{
                row.setViewVisibility(android.R.id.text2, View.GONE);
            }

            Intent itemIntent = new Intent();
            Bundle extras=new Bundle();
            extras.putLong("id", items.get(position).getId());
            itemIntent.putExtras(extras);

            row.setOnClickFillInIntent(android.R.id.text1, itemIntent);

        // adapter divider
        }else if (items.get(position).adapterType == Note.TYPE_ADAPTER_TITLE){
            row.setViewVisibility(R.id.ll, View.GONE);
            row.setViewVisibility(R.id.text3, View.VISIBLE);
            row.setTextViewText(R.id.text3, items.get(position).getTitle());
        }

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }
}
