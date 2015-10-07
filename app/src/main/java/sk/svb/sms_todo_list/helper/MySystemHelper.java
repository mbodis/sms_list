package sk.svb.sms_todo_list.helper;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mbodis on 9/19/15.
 */
public class MySystemHelper {

    public static String getRidOfDiacritics(String input){
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static String [] getRidOfDiacritics(String[] input){
        for (int i=0; i<input.length; i++) {
            input[i] = getRidOfDiacritics(input[i]);
        }

        return input;
    }

    public static String[] toLowerCase(String [] arr, Locale l){
        for (int i=0; i<arr.length; i++) {
            arr[i] = arr[i].toLowerCase(l);
        }

        return arr;
    }

    public static int diffDatesInDays(String date1, String date2){

        String[] day1Arr = date1.split("-");
        Calendar day1 = Calendar.getInstance();
        day1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day1Arr[2]));
        day1.set(Calendar.MONTH, Integer.parseInt(day1Arr[1]) - 1); // 0-11 so 1 less
        day1.set(Calendar.YEAR, Integer.parseInt(day1Arr[0]));

        String[] day2Arr = date2.split("-");
        Calendar day2 = Calendar.getInstance();
        day2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day2Arr[2]));
        day2.set(Calendar.MONTH, Integer.parseInt(day2Arr[1]) - 1); // 0-11 so 1 less
        day2.set(Calendar.YEAR, Integer.parseInt(day2Arr[0]));

        long diff = Math.abs(day2.getTimeInMillis() - day1.getTimeInMillis()); //result in m

        return (int)(diff / (24 * 60 * 60 * 1000));
    }

    /**
     * read file form raw folder
     */
    public static String loadFile (Resources res, int id) {
        String result = "";
        try{
            String UTF = "utf8";
            //	int BUFFER_SIZE = 8192;
            String str;
            StringBuffer sb = new StringBuffer();

            InputStream is = res.openRawResource(id);
            InputStreamReader in = new InputStreamReader(is, UTF);
            BufferedReader reader = new BufferedReader(in);

            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            result = sb.toString();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
