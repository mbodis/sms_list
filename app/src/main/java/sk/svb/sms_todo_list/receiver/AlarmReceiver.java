package sk.svb.sms_todo_list.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.notification.RemindNoteNotification;

/**
 * alarm manager wakes up device at specific time and shows notification
 *
 * @author mbodis
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();

    public static final String ALARM_ACTION = "sk.svb.sms_todo_list.receiver.AlarmReceiver.action";
    public final static int RC_INTENT = 1800;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (intent != null && intent.getAction() != null) {
            debugLogAlarm(ctx, -1, "onReceive");
            RemindNoteNotification.showMeTodaysNotes(ctx);
        }

    }

    /**
     * set alarm if setting are set so
     * set time to hour:minute by settings
     * set if there is today's note
     */
    public static void setAlarm(Context ctx) {

        cancelAlarm(ctx); // not needed ?

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        // if sync is enable
        if (sp.getBoolean(ctx.getString(R.string.pref_key_notification), false)) {

            String time = sp.getString(ctx.getString(R.string.pref_key_notif_time), null);
            if (time != null) {

                Log.d(TAG, "time: " + time);

                String[] dayArr = time.split(":");
                Calendar day = Calendar.getInstance();
                day.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dayArr[0]));
                day.set(Calendar.MINUTE, Integer.parseInt(dayArr[1]));

                AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC_WAKEUP, day.getTimeInMillis(), getPendingIntent(ctx));

                debugLogAlarm(ctx, day.getTimeInMillis(), "setAlarm");
            }
        }
    }

    /**
     * cancel sync service alarm
     */
    public static void cancelAlarm(Context ctx) {
        AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(getPendingIntent(ctx));

        debugLogAlarm(ctx, -1, "cancelAlarm");
    }

    /**
     * return sync pendingIntent for alarm
     */
    private static PendingIntent getPendingIntent(Context ctx) {
        Intent intent = new Intent(ALARM_ACTION);
        return PendingIntent.getBroadcast(ctx, RC_INTENT, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /*
     * debug only: logging states into log file
     */
    public static void debugLogAlarm(Context ctx, long startMilis, String msg) {
        Log.d(TAG, msg + "startMilis: " + startMilis);
        // MyLogger.addLog(ctx, MyLogger.FILE_ALARM_SYNC, msg + "startMilis: " + startMilis);
    }

}
