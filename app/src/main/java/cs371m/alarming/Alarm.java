package cs371m.alarming;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Brian on 8/4/2017.
 */

public class Alarm implements Comparable<Alarm>, Serializable {
    private int hour;
    private int minute;
    private int objectiveCode;
    private String alarmDescription;
    private String recordingFileName;
    boolean repeating;
    boolean enabled;
    private Uri ringtoneUri;

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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
        this(hour, minute, objectiveCode, alarmDescription, recordingFileName, false);
    }

    public Alarm(int hour, int minute, int objectiveCode, String alarmDescription, String recordingFileName, boolean repeating) {
        this(hour, minute, objectiveCode, alarmDescription, recordingFileName, repeating, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
    }

    public Alarm(int hour, int minute, int objectiveCode, String alarmDescription, String recordingFileName, boolean repeating, Uri ringtoneUri) {
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
        this.repeating = repeating;
        this.enabled = true;
        this.ringtoneUri = ringtoneUri;
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

    public void setRingtoneUri(Uri ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public Uri getRingtoneUri() {

        return ringtoneUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        return hour == alarm.hour && minute == alarm.minute && objectiveCode == alarm.objectiveCode
                && (!TextUtils.isEmpty(alarmDescription) ? alarmDescription.equals(alarm.alarmDescription)
                : alarm.alarmDescription == null && (!TextUtils.isEmpty(recordingFileName) ?
                recordingFileName.equals(alarm.recordingFileName) : alarm.recordingFileName == null));

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

    @Override
    public int compareTo(@NonNull Alarm other) {
        if (this.hour != other.hour) {
            return this.hour - other.hour;
        } else {
            return this.minute - other.minute;
        }
    }
}
