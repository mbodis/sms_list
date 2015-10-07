package sk.svb.sms_todo_list.db_models;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.helper.MySystemHelper;
import sk.svb.sms_todo_list.logic.Note;

/**
 * Created by mbodis on 9/14/15.
 */
public class DbNoteModel {

    private static final String TAG = DbNoteModel.class.getName();

    public static List<Note> getNotes(Context ctx, boolean deleted){
        List<Note> res = new ArrayList<Note>();

        int m = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        int d = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) );

        String now = Calendar.getInstance().get(Calendar.YEAR)
                + "-" + (m>9 ? m : "0"+m)
                + "-" + (d>9 ? d : "0"+d );

        boolean today = false, week = false, month = false, other = false;
        List<Note> list = null;
        if (deleted){
            list = Note.find(Note.class, "deleted=?", new String[]{"1"}, "", "date desc", "" );
        }else{
            list = Note.find(Note.class, "deleted=?", new String[]{"0"}, "", "date desc", "" );
        }
        for (Note mNote : list) {

            int diffDays = MySystemHelper.diffDatesInDays(mNote.getDate(), now);
            Log.d(TAG, mNote.getDate() + " " + now + " => " + diffDays);

            // today
            if (!today && diffDays == 0){
                res.add(getAdapterNote(ctx, 0));
                today = true;
            }

            // this week
            if (!week && diffDays > 0 && diffDays <=7){
                res.add(getAdapterNote(ctx, 1));
                week = true;
            }

            // this month
            if (!month && diffDays > 7 && diffDays <=30){
                res.add(getAdapterNote(ctx, 2));
                month = true;
            }

            // other
            if (!other && diffDays >30){
                res.add(getAdapterNote(ctx, 3));
                other = true;
            }

            res.add(mNote);
        }

        return res;
    }

    public static List<Note> getTodaysNotes(){
        List<Note> res = new ArrayList<Note>();

        int m = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        int d = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) );

        String now = Calendar.getInstance().get(Calendar.YEAR)
                + "-" + (m>9 ? m : "0"+m)
                + "-" + (d>9 ? d : "0"+d );

        for (Note mNote: Note.find(Note.class, "", new String[]{}, "", "date desc", "" )) {
            int diffDays = MySystemHelper.diffDatesInDays(mNote.getDate(), now);

            // today
            if (diffDays == 0){
                res.add(mNote);
            }else{
                break;
            }
        }

        return res;
    }

    public static boolean isNoteWithTodaysDate(){

        int m = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        int d = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) );

        String now = Calendar.getInstance().get(Calendar.YEAR)
                + "-" + (m>9 ? m : "0"+m)
                + "-" + (d>9 ? d : "0"+d );

        List<Note> res = Note.find(Note.class, "", new String[]{}, "", "date desc", "" );
        if (res.size() > 0){
            // today
            if (MySystemHelper.diffDatesInDays(res.get(0).getDate(), now) == 0){
                return true;
            }
        }

        return false;
    }

    private static Note getAdapterNote(Context ctx, int type){
        Note mNote = new Note();
        mNote.adapterType = Note.TYPE_ADAPTER_TITLE;
        mNote.setMessage("");
        mNote.setId((long) -1);
        switch (type){
            case 0:
                mNote.setTitle(ctx.getString(R.string.note_today));
                break;
            case 1:
                mNote.setTitle(ctx.getString(R.string.note_last_week));
                break;
            case 2:
                mNote.setTitle(ctx.getString(R.string.note_last_month));
                break;
            case 3:
                mNote.setTitle(ctx.getString(R.string.note_other));
                break;
        }

        return mNote;
    }

    public static String[] getNotesStringArr(Context ctx){

        List<Note> list = DbNoteModel.getNotes(ctx, false);
        String[] arr = new String[list.size()];

        for (int i=0; i<list.size(); i++) {
            arr[i] = list.get(i).getMessage();
        }
        return arr;
    }

    public static void removeAllDeletedNotes(){
        Note.deleteAll(Note.class, "deleted=?", new String[]{"1"});
    }
}
