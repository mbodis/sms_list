package sk.svb.sms_todo_list.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sk.svb.sms_todo_list.db_models.DbNoteModel;


public class OnBootReceiver extends BroadcastReceiver {

	private static final String TAG = OnBootReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			if (DbNoteModel.isNoteWithTodaysDate()){
				// starting setup alarm
				AlarmReceiver.setAlarm(context);
				AlarmReceiver.debugLogAlarm(context, -1, "OnBootReceiver");
			}

		}
	}

}