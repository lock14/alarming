package cs371m.alarming;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Brian on 8/4/2017.
 */

public class Alarm implements Serializable {
    private int hour;
    private int minute;
    private int objectiveCode;
    private String alarmDescription;
    private String recordingFileName;

    public Alarm() {
        hour = -1;
        minute = -1;
        objectiveCode = -1;
    }

    public Alarm(int hour, int minute, int objectiveCode) {
        this(hour, minute, objectiveCode, null);
    }

    public Alarm(int hour, int minute, int objectiveCode, String alarmDescription) {
        this(hour, minute, objectiveCode, alarmDescription, null);
    }

    public Alarm(int hour, int minute, int objectiveCode, String alarmDescription, String recordingFileName) {
        this.hour = hour;
        this.minute = minute;
        this.objectiveCode = objectiveCode;
        this.alarmDescription = alarmDescription;
        this.recordingFileName = recordingFileName;
        if (this.alarmDescription == null) {
            this.alarmDescription = "";
        }
        if (this.recordingFileName == null) {
            this.recordingFileName = "";
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getObjectiveCode() {
        return objectiveCode;
    }

    public void setObjectiveCode(int objectiveCode) {
        this.objectiveCode = objectiveCode;
    }

    public String getAlarmDescription() {
        return alarmDescription;
    }

    public void setAlarmDescription(String alarmDescription) {
        this.alarmDescription = alarmDescription;
    }

    public String getRecordingFileName() {
        return recordingFileName;
    }

    public void setRecordingFileName(String recordingFileName) {
        this.recordingFileName = recordingFileName;
    }

    public Calendar getCalendar() {
        Calendar curentTime = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, getHour());
        alarmTime.set(Calendar.MINUTE, getMinute());
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
        if (curentTime.after(alarmTime)) {
            alarmTime.add(Calendar.DATE, 1);
        }
        return alarmTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (hour != alarm.hour) return false;
        if (minute != alarm.minute) return false;
        if (objectiveCode != alarm.objectiveCode) return false;
        if (!TextUtils.isEmpty(alarmDescription) ? !alarmDescription.equals(alarm.alarmDescription) : alarm.alarmDescription != null)
            return false;
        return !TextUtils.isEmpty(recordingFileName) ? recordingFileName.equals(alarm.recordingFileName) : alarm.recordingFileName == null;

    }

    @Override
    public int hashCode() {
        int result = hour;
        result = 31 * result + minute;
        result = 31 * result + objectiveCode;
        result = 31 * result + (!TextUtils.isEmpty(alarmDescription) ? alarmDescription.hashCode() : 0);
        result = 31 * result + (!TextUtils.isEmpty(recordingFileName) ? recordingFileName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", objectiveCode=" + objectiveCode +
                ", alarmDescription='" + alarmDescription + '\'' +
                ", recordingFileName='" + recordingFileName + '\'' +
                '}';
    }
}
