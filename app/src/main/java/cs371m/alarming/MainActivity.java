package cs371m.alarming;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final int EDIT_ALARM = 0;
    private static final int OBJECTIVE = 1;
    private AlarmManager alarmManager;
    private Ringtone ringtone;
    private PendingIntent pendingIntent;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        pendingIntent = null;

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        loadStateFromPreferences();
        Intent intent = getIntent();
        boolean startedByAlarm =
                intent.getBooleanExtra(getString(R.string.intent_started_by_alarm_key), false);
        if (startedByAlarm) {
            playAlarm();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(getString(R.string.shared_pref_hour_key), hour);
        editor.putInt(getString(R.string.shared_pref_minute_key), minute);
        editor.apply();
    }

    private void loadStateFromPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        hour = sharedPreferences.getInt(getString(R.string.shared_pref_hour_key), -1);
        minute = sharedPreferences.getInt(getString(R.string.shared_pref_minute_key), -1);
        if (hour != -1 && minute != -1) {
            enableAlarmText(hour, minute);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        boolean startedByAlarm =
                intent.getBooleanExtra(getString(R.string.intent_started_by_alarm_key), false);
        if (startedByAlarm) {
            playAlarm();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_alarm:
                Intent intent = new Intent(this, EditAlarm.class);
                startActivityForResult(intent, EDIT_ALARM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_ALARM) {
                hour = intent.getIntExtra(getString(R.string.intent_hour_key), -1);
                minute = intent.getIntExtra(getString(R.string.intent_minute_key), -1);

                if (hour != -1 && minute != -1) {
                    enableAlarmText(hour, minute);
                    setAlarm(hour, minute);
                }
            } else if (requestCode == OBJECTIVE) {
                ringtone.stop();
                Button disableButton = (Button) findViewById(R.id.disable_alarm);
                disableButton.setVisibility(View.INVISIBLE);
                TextView alarmText = (TextView) findViewById(R.id.alarm_text);
                alarmText.setEnabled(false);
                // reset hour and minute
                hour = -1;
                minute = -1;
            }
        }
    }

    private void enableAlarmText(int hour, int minute) {
        TextView alarmText = (TextView) findViewById(R.id.alarm_text);
        alarmText.setText(AlarmUtil.alarmText(hour, minute));
        alarmText.setEnabled(true);
    }

    private void setAlarm(int hour, int minute) {
        // check if we've already scheduled an alarm and if so cancel it
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
        // set calendar to time
        Calendar curentTime = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        if (curentTime.after(alarmTime)) {
            alarmTime.add(Calendar.DATE, 1);
        }

        // set alarm receiver
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // schedule alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
    }

    private void playAlarm() {
        Button disableButton = (Button) findViewById(R.id.disable_alarm);
        disableButton.setVisibility(View.VISIBLE);
        ringtone.play();
    }

    public void cancelAlarm(View view) {
        Intent intent = new Intent(this, MathObjective.class);
        startActivityForResult(intent, OBJECTIVE);
    }
}
