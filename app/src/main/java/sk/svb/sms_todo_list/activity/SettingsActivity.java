package sk.svb.sms_todo_list.activity;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TimePicker;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.dialog.LicencesFragmentDialog;
import sk.svb.sms_todo_list.receiver.AlarmReceiver;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getName();
    public static final String SEPARATOR = ",";

    private TimePickerDialog mTimePickerDialog;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private  Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.d(TAG, "stringValue: " + stringValue );

            if (preference instanceof ListPreference) {
                Log.d(TAG, "instanceof ListPreference");

                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                String text = (index >= 0 ? listPreference.getEntries()[index] : null).toString();

                if(preference.getKey().equals(getString(R.string.pref_key_text_find))){
                    preference.setSummary(getString(R.string.pref_summary_text_find) + "\n\n" + text);
                }else{
                    preference.setSummary(text);
                }
            } else {
                Log.d(TAG, "OTHER instanceof ListPreference");

                // For all other preferences, set the summary to the value's
                // simple string representation.
                if (preference.getKey().equals(getString(R.string.pref_key_sms_keyword))){
                    preference.setSummary(getString(R.string.pref_summary_sms_keyword) + " " + stringValue);
                }else{
                    preference.setSummary(stringValue);
                }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_sms_keyword)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_text_find)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notif_time)));


        Preference p0 = findPreference(getString(R.string.pref_key_notification));
        p0.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "onPreferenceClick NOTIFICATION toggle");
                // setup alarm
                AlarmReceiver.setAlarm(getApplicationContext());
                AlarmReceiver.debugLogAlarm(getApplicationContext(), -1,
                        "Settings activity pref_key_notification");
                return false;
            }
        });

        Preference p1 = findPreference(getString(R.string.pref_key_notif_time));
        p1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String time = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");

                int hr = 17;
                int min = 0;
                if (time != null && time.contains(":")){
                    String[] timeArr = time.split(":");
                    hr = Integer.parseInt(timeArr[0]);
                    min = Integer.parseInt(timeArr[1]);
                }
                mTimePickerDialog = new TimePickerDialog(SettingsActivity.this, timePickerListener,
                        hr, min, false);
                mTimePickerDialog.show();
                return false;
            }
        });
        String time = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getString(getString(R.string.pref_key_notif_time), "");
        findPreference(getString(R.string.pref_key_notif_time)).setSummary(time);


        Preference p2 = findPreference(getString(R.string.pref_key_licences));
        p2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment newFragment = LicencesFragmentDialog
                        .newInstance();
                newFragment.show(getFragmentManager(), LicencesFragmentDialog.TAG);
                return false;
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String newTime = String.valueOf(hourOfDay > 9 ? hourOfDay : "0" + hourOfDay)
                    + ":" +String.valueOf(minute>9 ? minute : "0"+minute);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            sp.edit().putString(getString(R.string.pref_key_notif_time), newTime).commit();
            findPreference(getString(R.string.pref_key_notif_time)).setSummary(newTime);

            // setup alarm
            AlarmReceiver.setAlarm(getApplicationContext());
            AlarmReceiver.debugLogAlarm(getApplicationContext(), -1,
                    "Settings activity pref_key_notif_time");
        }

    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private  void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

}
