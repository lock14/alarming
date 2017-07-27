package cs371m.alarming;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Brian on 7/27/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        TextView alarmText = (TextView) getActivity().findViewById(R.id.edit_alarm_text);
        String ampm = (hour < 12)? " AM" : " PM";
        alarmText.setText(zeroPad(covertHour(hour)) + ":" + zeroPad(minute) + ampm);
    }

    private String zeroPad(int n) {
        StringBuilder s = new StringBuilder();
        if (n < 10) {
            s.append("0");
        }
        return s.append(n).toString();
    }

    private int covertHour(int hour) {
        if (hour > 12) {
            return hour %= 12;
        } else if (hour == 0) {
            return 12;
        }
        return hour;
    }
}
