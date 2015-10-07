package sk.svb.sms_todo_list.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLogger {

	public static final boolean enable = TestVersion.isTestVersion();

    public static final String FILE_DEFAULT = "default_log.txt";
    public static final String FILE_ALARM_SYNC = "alarm_log.txt";
    public static final String TAG = MyLogger.class.getName();
	
	
	public static final String[] ALL_FILES = {FILE_DEFAULT, FILE_ALARM_SYNC};
	
	
	/**
	 * zapisuje logy do suboru vo formate<br> 
	 * yyyy.MM.dd_HH.mm.ss nami vlozeny text content \n
	 * @param context
	 * @param logFileName nazov suboru - staticka premenna v MyLogger triede
	 * @param content obsah
	 * 	 
	 */
	public static void addLog(Context context, String logFileName, String content){
		if (context == null)
			return;
		if (!isLogerEnable(logFileName))
			return;
		
		if (enable){
			String timeNow = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss", Locale.US).
			format(new Date(System.currentTimeMillis()));
			
			
			(new File(context.getExternalFilesDir(null) + "/log", "")).mkdirs();
			File file = new File(context.getExternalFilesDir(null) + "/log", logFileName); 
			BufferedWriter out;
			try {			
				out = new BufferedWriter(new FileWriter(file, true));
				out.write(timeNow + " " + content);
                out.newLine();
                out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}else{
			Log.d(TAG, "addLog - logger is offline");
		}
	}
	
	/**
	 * kontrola ci je konkretne logovanie zapnute
	 */
	private static boolean isLogerEnable(String logerType) {
		return logerType != null;
	}

	public static void removeLogFile(Context context, String fileName){
		if (context == null)
			return;
		if (enable){
			File file = new File(context.getExternalFilesDir(null) + "/log", fileName);
			file.delete();		
		}else{
			Log.d(TAG, "removeLogFile - logger is offline");
		}
	}
	
	public static void removeAllLogs(Context context){
		if (context == null)
			return;
		if (enable){
			for(int i=0; i< ALL_FILES.length; i++){
				removeLogFile(context, ALL_FILES[i]);	
			}		
			(new File(context.getFilesDir() + "/log", "")).delete();
		}else{
			Log.d(TAG, "removeAllLogs - logger is offline");
		}
	}
}
