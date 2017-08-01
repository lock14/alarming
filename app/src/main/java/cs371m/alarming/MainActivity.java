package cs371m.alarming;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
    private int objectiveCode;
    private String alarmDescription;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        pendingIntent = null;
        ActivityCompat.requestPermissions(this, permissions,
                                          REQUEST_RECORD_AUDIO_PERMISSION);

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
        editor.putInt(getString(R.string.shared_pref_objective_key), objectiveCode);
        editor.putString(getString(R.string.shared_pref_description_key), alarmDescription);
        editor.apply();
    }

    private void loadStateFromPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        hour = sharedPreferences.getInt(getString(R.string.shared_pref_hour_key), -1);
        minute = sharedPreferences.getInt(getString(R.string.shared_pref_minute_key), -1);
        objectiveCode = sharedPreferences.getInt(getString(R.string.shared_pref_objective_key), 0);
        alarmDescription = sharedPreferences
                .getString(getString(R.string.shared_pref_description_key), "");
        if (hour != -1 && minute != -1) {
            enableAlarmText(hour, minute);
        }
        if (!TextUtils.isEmpty(alarmDescription)) {
            enableAlarmDescription(alarmDescription);
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
                objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
                alarmDescription = intent.getStringExtra(getString(R.string.intent_description_key));
                if (alarmDescription == null) {
                    alarmDescription = "";
                }

                if (hour != -1 && minute != -1) {
                    enableAlarmText(hour, minute);
                    setAlarm(hour, minute);
                }
                if (TextUtils.isEmpty(alarmDescription)) {
                    disableAlarmDescription();
                } else {
                    enableAlarmDescription(alarmDescription);
                }
            } else if (requestCode == OBJECTIVE) {
                ringtone.stop();
                Button disableButton = (Button) findViewById(R.id.disable_alarm);
                disableButton.setVisibility(View.INVISIBLE);
                TextView alarmText = (TextView) findViewById(R.id.alarm_text);
                alarmText.setVisibility(View.INVISIBLE);
                TextView description = (TextView) findViewById(R.id.alarm_description_txt);
                description.setVisibility(View.INVISIBLE);
                // reset hour and minute
                hour = -1;
                minute = -1;
                objectiveCode = 0;
                alarmDescription = "";
            }
        }
    }

    private void enableAlarmText(int hour, int minute) {
        TextView alarmText = (TextView) findViewById(R.id.alarm_text);
        alarmText.setText(AlarmUtil.alarmText(hour, minute));
        alarmText.setVisibility(View.VISIBLE);
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
        Objective objective = Objective.getObjective(objectiveCode);
        Intent intent = null;
        switch (objective) {
            case MATH:
                intent = new Intent(this, MathObjective.class);
                break;
            case TIC_TAC_TOE:
                // insert code here
                break;
            case TYPING:
                // insert code here
                break;
            case SWIPE:
                // insert code here
                break;
            case COUNTING:
                // insert code here
                break;
        }
        startActivityForResult(intent, OBJECTIVE);
    }

    public void enableAlarmDescription(String alarmDescription) {
        TextView textView = (TextView) findViewById(R.id.alarm_description_txt);
        textView.setText(alarmDescription);
        textView.setVisibility(View.VISIBLE);
    }

    public  void disableAlarmDescription() {
        TextView textView = (TextView) findViewById(R.id.alarm_description_txt);
        textView.setText("");
        textView.setVisibility(View.INVISIBLE);
    }
}
