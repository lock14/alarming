package cs371m.alarming;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cs371.record_sound_logic.SoundLogic;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final int EDIT_ALARM = 0;
    private static final int OBJECTIVE = 1;
    private AlarmManager alarmManager;
    private Ringtone ringtone;
    private List<Alarm> alarms;
    private SoundLogic soundLogic;
    private AlarmListAdapter alarmListAdapter;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Alarm currentAlarm;

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
        soundLogic = new SoundLogic(new ContextWrapper((getApplicationContext())),
                getString(R.string.alarm_file_directory));
        alarms = new ArrayList<>();
        currentAlarm = null;
        ActivityCompat.requestPermissions(this, permissions,
                                          REQUEST_RECORD_AUDIO_PERMISSION);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        loadStateFromPreferences();
        alarmListAdapter = new AlarmListAdapter(this, alarms);
        ListView alarmListView = (ListView) findViewById(R.id.alarm_list);
        alarmListView.setAdapter(alarmListAdapter);
        if (alarms.isEmpty()) {
            disableAlarmListView();
            enableNoAlarmText();
        } else {
            disableNoAlarmText();
            enableAlarmListView();
        }

        Intent intent = getIntent();
        if (intent != null) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String alarmsJson = gson.toJson(alarms);
        Log.d(TAG, "JSON: " + alarmsJson);
        editor.putString(getString(R.string.shared_pref_alarms), alarmsJson);
        editor.apply();
    }

    private void loadStateFromPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String alarmsJson = sharedPreferences.getString(getString(R.string.shared_pref_alarms), "[]");
        Gson gson = new Gson();
        alarms = gson.fromJson(alarmsJson, new TypeToken<List<Alarm>>(){}.getType());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        boolean startedByAlarm =
                intent.getBooleanExtra(getString(R.string.intent_started_by_alarm_key), false);
        if (startedByAlarm) {
            int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id), 0);
            currentAlarm = getAlarmById(alarmId);
            playAlarm();
        }
    }

    private Alarm getAlarmById(int alarmId) {
        // probably won't have many alarms so linear search should be fine
        for (Alarm alarm : alarms) {
            if (alarm.hashCode() == alarmId) {
                return alarm;
            }
        }
        return null;
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

    private void editAlarm(Alarm alarm) {
        Intent intent = new Intent(this, EditAlarm.class);
        intent.putExtra(getString(R.string.edit_alarm_edit_mode), true);
        intent.putExtra(getString(R.string.intent_alarm_id), alarm.hashCode());
        intent.putExtra(getString(R.string.intent_hour_key), alarm.getHour());
        intent.putExtra(getString(R.string.intent_minute_key), alarm.getMinute());
        intent.putExtra(getString(R.string.intent_objective_key), alarm.getObjectiveCode());
        intent.putExtra(getString(R.string.intent_description_key), alarm.getAlarmDescription());
        intent.putExtra(getString(R.string.intent_recording_key), alarm.getRecordingFileName());
        intent.putExtra(getString(R.string.intent_repeat_key), alarm.isRepeating());
        startActivityForResult(intent, EDIT_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_ALARM) {
                int hour = intent.getIntExtra(getString(R.string.intent_hour_key), -1);
                int minute = intent.getIntExtra(getString(R.string.intent_minute_key), -1);
                int objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
                String alarmDescription = intent.getStringExtra(getString(R.string.intent_description_key));
                String recordingFileName = intent.getStringExtra(getString(R.string.intent_recording_key));
                boolean repeat = intent.getBooleanExtra(getString(R.string.intent_repeat_key), false);
                boolean editMode = intent.getBooleanExtra(getString(R.string.edit_alarm_edit_mode), false);
                if (editMode) {
                    int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id), -1);
                    Alarm alarm = getAlarmById(alarmId);
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setObjectiveCode(objectiveCode);
                    alarm.setAlarmDescription(alarmDescription);
                    alarm.setRecordingFileName(recordingFileName);
                    alarm.setRepeating(repeat);
                    if (alarm.isEnabled()) {
                        setAlarm(alarm, true);
                    }
                    alarmListAdapter.notifyDataSetChanged();
                } else {
                    Alarm alarm = new Alarm(hour, minute, objectiveCode, alarmDescription,
                                            recordingFileName, repeat);

                    if (hour != -1 && minute != -1) {
                        addAlarm(alarm);
                        setAlarm(alarm, true);
                    }
                }
            } else if (requestCode == OBJECTIVE) {
                ringtone.stop();
                Button disableButton = (Button) findViewById(R.id.disable_alarm);
                disableButton.setVisibility(View.INVISIBLE);

                if (currentAlarm.isRepeating()) {
                    setAlarm(currentAlarm, false);
                } else {
                    currentAlarm.setEnabled(false);
                }
                disableAlarmsWithSameTime(currentAlarm);
                alarmListAdapter.notifyDataSetChanged();
                currentAlarm = null;
            }
        }
    }

    private void disableAlarmsWithSameTime(Alarm currentAlarm) {
        for (Alarm alarm : alarms) {
            if (alarm != currentAlarm && alarm.getHour() == currentAlarm.getHour()
                    && alarm.getMinute() == currentAlarm.getMinute() && !alarm.isRepeating()) {
                alarm.setEnabled(false);
            }
        }
    }

    private void enableNoAlarmText() {
        // "No Alarm Enabled" code
        TextView alarmMissing = (TextView) findViewById(R.id.alarm_missing);
        alarmMissing.setVisibility(View.VISIBLE);
    }

    private void disableNoAlarmText() {
        // "No Alarm Enabled" code
        TextView alarmMissing = (TextView) findViewById(R.id.alarm_missing);
        alarmMissing.setVisibility(View.INVISIBLE);
    }

    private void enableAlarmListView() {
        ListView alarmListView = (ListView) findViewById(R.id.alarm_list);
        alarmListView.setVisibility(View.VISIBLE);
    }

    private void disableAlarmListView() {
        ListView alarmListView = (ListView) findViewById(R.id.alarm_list);
        alarmListView.setVisibility(View.INVISIBLE);
    }

    private void addAlarm(Alarm alarm) {
        if (alarms.isEmpty()) {
            enableAlarmListView();
            disableNoAlarmText();
        }
        alarms.add(alarm);
        alarmListAdapter.notifyDataSetChanged();
    }

    private void removeAlarm(Alarm alarm) {
        alarms.remove(alarm);
        alarmListAdapter.notifyDataSetChanged();
        if (alarms.isEmpty()) {
            disableAlarmListView();
            enableNoAlarmText();
        }
    }

    private void setAlarm(Alarm alarm, boolean displayToast) {
        PendingIntent pendingIntent = createPendingIntent(alarm);
        Calendar alarmTime = alarm.getCalendar();
        // schedule alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
        if (displayToast) {
            String message = AlarmUtil.getTimeDiffMessage(alarm);
            Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setText(message);
            toast.show();
        }
    }

    private void cancelAlarm(Alarm alarm) {
        PendingIntent pendingIntent = createPendingIntent(alarm);
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent createPendingIntent(Alarm alarm) {
        // set alarm receiver
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(getString(R.string.intent_alarm_id), alarm.hashCode());
        return PendingIntent.getBroadcast(MainActivity.this, alarm.hashCode(), intent, 0);
    }

    private void playAlarm() {
        if (currentAlarm == null) {
            throw new IllegalStateException("current alarm is null!");
        }
        if (TextUtils.isEmpty(currentAlarm.getRecordingFileName())) {
            ringtone.play();
        } else {
            soundLogic.playSoundByFileName(currentAlarm.getRecordingFileName(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ringtone.play();
                }
            });
        }
        Button disableButton = (Button) findViewById(R.id.disable_alarm);
        disableButton.setVisibility(View.VISIBLE);
    }

    public void disableAlarm(View view) {
        if (currentAlarm == null) {
            throw new IllegalStateException("current alarm is null!");
        }
        Objective objective = Objective.getObjective(currentAlarm.getObjectiveCode());
        Intent intent = null;
        switch (objective) {
            case MATH:
                intent = new Intent(this, MathObjective.class);
                break;
            case TIC_TAC_TOE:
                intent = new Intent(this, TicTacToeObjective.class);
                break;
            case TYPING:
                // insert code here
                break;
            case SWIPE:
                intent = new Intent(this, SwipeObjective.class);
                break;
            case COUNTING:
                // insert code here
                break;
        }
        startActivityForResult(intent, OBJECTIVE);
    }

    public class AlarmListAdapter extends ArrayAdapter<Alarm> {

        AlarmListAdapter(Context context, List<Alarm> objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Alarm alarm = alarms.get(position);

            final AlarmViewHolder holder;
            if (convertView == null) {
                holder = new AlarmViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);
                holder.alarmText = convertView.findViewById(R.id.alarm_text);
                holder.descriptionText = convertView.findViewById(R.id.alarm_description_txt);
                holder.repeatChkBox = convertView.findViewById(R.id.repeat_chk_bx);
                holder.enabledSwitch = convertView.findViewById(R.id.enable_switch);
                holder.editAlarm = convertView.findViewById(R.id.edit_alarm_img);
                holder.deleteAlarm = convertView.findViewById(R.id.delete_alarm_img);

                holder.editAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editAlarm(alarm);
                    }
                });

                holder.deleteAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelAlarm(alarm);
                        removeAlarm(alarm);
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (AlarmViewHolder) convertView.getTag();
            }
            holder.alarmText.setText(AlarmUtil.alarmText(alarm.getHour(), alarm.getMinute()));
            holder.descriptionText.setText(alarm.getAlarmDescription());

            holder.repeatChkBox.setChecked(alarm.isRepeating());
            holder.repeatChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    alarm.setRepeating(checked);
                }
            });
            holder.enabledSwitch.setChecked(alarm.isEnabled());
            holder.enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    alarm.setEnabled(checked);
                    holder.alarmText.setEnabled(checked);
                    holder.descriptionText.setEnabled(checked);
                    if (checked) {
                        setAlarm(alarm, true);
                    } else {
                        cancelAlarm(alarm);
                    }
                }
            });
            holder.alarmText.setEnabled(alarm.isEnabled());
            holder.descriptionText.setEnabled(alarm.isEnabled());

            return convertView;
        }
    }

    private class AlarmViewHolder {
        TextView alarmText;
        TextView descriptionText;
        ImageView deleteAlarm;
        ImageView editAlarm;
        CheckBox repeatChkBox;
        Switch enabledSwitch;
    }
}
