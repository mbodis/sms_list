package sk.svb.sms_todo_list.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.activity.MainActivity;
import sk.svb.sms_todo_list.db_models.DbNoteModel;
import sk.svb.sms_todo_list.logic.Note;

/**
 * Created by mbodis on 9/20/15.
 */
public class RemindNoteNotification {

    public static final int NOTIF_CODE_TODAYS_NOTE = 111;

    public static void showMeTodaysNotes(Context ctx) {

        // get all todays notes
        List<Note> list = DbNoteModel.getTodaysNotes();
        StringBuilder message = new StringBuilder();
        for (Note mNote : list) {
            if (message.length() > 0){
                message.append("\n");
            }
            message.append(mNote.getMessage());
        }

        // menu_trash_bin notif intent
        Intent notifIntent = new Intent(ctx, MainActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // notif pending intent
        PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        Notification.Builder b = new Notification.Builder(ctx);
        Notification n = b
                .setContentTitle(
                        ctx.getString(R.string.notif_title_today_notes))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
                .setAutoCancel(true).getNotification();

        // show notification
        NotificationManager notificationManager = (NotificationManager) ctx
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_CODE_TODAYS_NOTE, n);

    }
}
