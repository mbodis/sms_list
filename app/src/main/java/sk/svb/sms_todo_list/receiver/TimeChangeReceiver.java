package sk.svb.sms_todo_list.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sk.svb.sms_todo_list.db_models.DbNoteModel;

/**
 * if time/date has changed - update sync alarm and canceling previous alarm
 * 
 * @author mbodis
 *
 */
public class TimeChangeReceiver extends BroadcastReceiver {

	private static final String TAG = TimeChangeReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {

		setupAlarmSyncOnTimeChanged(context, intent);
	}

	/*
	 * restart alarm
	 */
	private void setupAlarmSyncOnTimeChanged(Context context, Intent intent) {
		if (intent != null
				&& intent.getAction() != null
				&& (intent.getAction().equals(Intent.ACTION_TIME_CHANGED) || intent
						.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED))) {

			if (DbNoteModel.isNoteWithTodaysDate()){

				// starting sset alarm
				AlarmReceiver.setAlarm(context);
				AlarmReceiver.debugLogAlarm(context, -1, "OnTimeHasChanged");
			}
		}
	}

}
