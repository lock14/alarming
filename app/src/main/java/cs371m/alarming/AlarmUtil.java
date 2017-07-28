package cs371m.alarming;


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
}
