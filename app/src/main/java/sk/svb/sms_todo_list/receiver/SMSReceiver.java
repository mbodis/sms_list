package sk.svb.sms_todo_list.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.activity.SettingsActivity;
import sk.svb.sms_todo_list.db_models.MyServiceHelper;
import sk.svb.sms_todo_list.helper.MySystemHelper;
import sk.svb.sms_todo_list.logic.Note;

/**
 * Created by mbodis on 9/14/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = SMSReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "SMS received.");

        final Bundle bundle = intent.getExtras();
        final Object[] pdusObj = (Object[]) bundle.get("pdus");
        for (int i = 0; i < pdusObj.length; i++) {

            // msg
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
            String oMessage = currentMessage.getDisplayMessageBody();
            String message = oMessage;

            // number
            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
            String senderNum = phoneNumber;

            Log.d(TAG, "message: " + message);

            Locale mLocale = Locale.CANADA; // TODO

            String searchType = getSearchType(context);
            String [] words = getWords(context);
            words = MySystemHelper.getRidOfDiacritics(words);
            words = MySystemHelper.toLowerCase(words, mLocale);

            message = MySystemHelper.getRidOfDiacritics(message.toLowerCase(mLocale));

            if (findWordsInMessage(context, message, words, searchType)) {
                Note mNote = new Note();
                mNote.setMessage(updateMessageByKeyWord(context, oMessage, message, words, searchType));
                int m = (Calendar.getInstance().get(Calendar.MONTH) + 1);
                int d = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                mNote.setDate(Calendar.getInstance().get(Calendar.YEAR)
                        + "-" + (m > 9 ? m : "0" + m)
                        + "-" + (d > 9 ? d : "0" + d));
                mNote.save();
            }
        }
        MyServiceHelper.refreshWidgets(context);

    }

    private String[] getWords(Context ctx) {
        SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
        String prefix = sp.getString(ctx.getString(R.string.pref_key_sms_keyword), ctx.getString(R.string.pref_default_sms_keyword));
        return prefix.split(SettingsActivity.SEPARATOR);
    }

    private String getSearchType(Context ctx){
        SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
        String type = sp.getString(ctx.getString(R.string.pref_key_text_find), ctx.getString(R.string.pref_default_text_find));
        return type;
    }

    private String updateMessageByKeyWord(Context ctx, String origMessage, String message, String[] keyWords, String searchType){

        for (String keyWord : keyWords) {
            if (searchType.equals(ctx.getString(R.string.text_find_prefix))) {
                if (message.startsWith(keyWord)){
                    return origMessage.substring(keyWord.length()).trim();
                }

            } else if (searchType.equals(ctx.getString(R.string.text_find_postfix))) {
                if (message.endsWith(keyWord)){
                    return origMessage.substring(0, message.length() - keyWord.length()).trim();
                }

            } else if (searchType.equals(ctx.getString(R.string.text_find_infix))) {
                return origMessage;
            }
        }

        return message;
    }

    private boolean findWordsInMessage(Context ctx, String message, String[] keyWords, String searchType){

        String[] messageArr =  message.split(" ");

        for (String keyWord : keyWords) {

            if (searchType.equals(ctx.getString(R.string.text_find_prefix))){
                if (message.startsWith(keyWord)){
                    return true;
                }

            }else if (searchType.equals(ctx.getString(R.string.text_find_postfix))){
                if (message.endsWith(keyWord)){
                    return true;
                }

            }else if (searchType.equals(ctx.getString(R.string.text_find_infix))){
                for (int i=0; i< messageArr.length; i++){
                    if (messageArr[i].equals(keyWord)){
                        return true;
                    }
                }

            }
        }

        return false;
    }
}