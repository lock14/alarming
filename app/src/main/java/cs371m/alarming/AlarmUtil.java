package cs371m.alarming;


import android.util.Log;

import java.util.Calendar;

public class AlarmUtil {

    public static String alarmText(int hour, int minute) {
        return zeroPad(covertHour(hour)) +
                ":" +
                zeroPad(minute) +
                ((hour < 12) ? " AM" : " PM");
    }
    public static String zeroPad(int n) {
        StringBuilder s = new StringBuilder();
        if (n < 10) {
            s.append("0");
        }
        return s.append(n).toString();
    }

    public static int covertHour(int hour) {
        if (hour > 12) {
            return hour % 12;
        } else if (hour == 0) {
            return 12;
        }
        return hour;
    }

    public static String getTimeDiffMessage(Alarm alarm) {
        Calendar currentTime = Calendar.getInstance();
        long diff_millis = alarm.getCalendar().getTimeInMillis() - currentTime.getTimeInMillis();
        long days = (((diff_millis / 1000) / 60) / 60) / 24;
        long hours = ((diff_millis / 1000) / 60) / 60;
        hours -= (days * 24);
        long minutes = (diff_millis / 1000) / 60;
        minutes -= ((days * 24 * 60) + (hours * 60));
        minutes++;
        if (minutes == 60) {
            minutes = 0;
            hours++;
        }
        Log.d("AlarmUtils", "diffMillis: " + diff_millis);
        Log.d("AlarmUtils", "days: " + days);
        Log.d("AlarmUtils", "hours: " + hours);
        Log.d("AlarmUtils", "minutes: " + minutes);
        StringBuilder message = new StringBuilder("Alarm set for");
        if (days > 0) {
            message.append(" ").append(days).append(" day");
        }
        if (days > 1) {
            message.append("s");
        }
        if (hours > 0) {
            if (message.charAt(message.length() - 1) != 'r') {
                message.append(",");
            }
            message.append(" ").append(hours).append(" hour");
        }
        if (hours > 1) {
            message.append("s");
        }
        if (minutes > 0) {
            if (message.charAt(message.length() - 1) != 'r') {
                message.append(",");
            }
            message.append(" ").append(minutes).append(" minute");
        }
        if (minutes > 1) {
            message.append("s");
        }
        return message.append(" from now.").toString();
    }
}
