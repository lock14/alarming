package cs371m.alarming;

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
    private int objectiveDifficulty;
    private String alarmDescription;
    private String recordingFileName;
    boolean repeating;
    boolean enabled;
    private Uri ringtoneUri;

    public boolean isRepeating() {
        return repeating;
    }

    public Alarm setRepeating(boolean repeating) {
        this.repeating = repeating;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Alarm() {
        this.hour = 0;
        this.minute = 0;
        this.objectiveCode = 0;
        this.objectiveDifficulty = DifficultyLevel.MEDIUM.ordinal();
        this.alarmDescription = "";
        this.recordingFileName = "";
        this.repeating = false;
        this.enabled = true;
        this.ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    }

    public int getHour() {
        return hour;
    }

    public Alarm setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public Alarm setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public int getObjectiveCode() {
        return objectiveCode;
    }

    public Alarm setObjectiveCode(int objectiveCode) {
        this.objectiveCode = objectiveCode;
        return this;
    }

    public int getObjectiveDifficulty() {
        return objectiveDifficulty;
    }

    public Alarm setObjectiveDifficulty(int objectiveDifficulty) {
        this.objectiveDifficulty = objectiveDifficulty;
        return this;
    }

    public String getAlarmDescription() {
        return alarmDescription;
    }

    public Alarm setAlarmDescription(String alarmDescription) {
        this.alarmDescription = alarmDescription;
        return this;
    }

    public String getRecordingFileName() {
        return recordingFileName;
    }

    public Alarm setRecordingFileName(String recordingFileName) {
        this.recordingFileName = recordingFileName;
        return this;
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

    public Alarm setRingtoneUri(Uri ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
        return this;
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
