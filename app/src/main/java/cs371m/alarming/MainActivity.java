package cs371m.alarming;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private static Button disableButton;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("requesting permissions!");
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
        disableButton = (Button) findViewById(R.id.disable_alarm);
        pendingIntent = null;
        ActivityCompat.requestPermissions(this, permissions,
                                          REQUEST_RECORD_AUDIO_PERMISSION);
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
        if (resultCode == Activity.RESULT_OK && requestCode == EDIT_ALARM) {
            int hour = intent.getIntExtra(getString(R.string.intent_hour_key), -1);
            int minute = intent.getIntExtra(getString(R.string.intent_minute_key), -1);

            if (hour != -1 && minute != -1) {
                TextView alarmText = (TextView) findViewById(R.id.alarm_text);
                alarmText.setText(AlarmUtil.alarmText(hour, minute));
                alarmText.setEnabled(true);
                setAlarm(hour, minute);
            }
        }
    }

    private void setAlarm(int hour, int minute) {
        // set calendar to time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // set alarm receiver
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // schedule alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm(View view) {
        if (alarmManager!= null && pendingIntent != null) {
            Log.d(TAG, "Canceling Alarm");
            alarmManager.cancel(pendingIntent);
        }
    }

    public static Button getDisableButton() {
        return disableButton;
    }
}
