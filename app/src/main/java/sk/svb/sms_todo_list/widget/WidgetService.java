package sk.svb.sms_todo_list.widget;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by mbodis on 9/15/15.
 */
public class WidgetService extends RemoteViewsService {

    private final String TAG = WidgetService.class.getName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d(TAG, "onGetViewFactory");

        return(new ViewsFactory(this.getApplicationContext(),
                intent));
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        onGetViewFactory(intent);
        return super.onBind(intent);
    }
}
