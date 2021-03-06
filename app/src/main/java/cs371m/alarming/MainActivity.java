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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cs371.record_sound_logic.SoundLogic;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final int EDIT_ALARM = 0;
    private static final int OBJECTIVE = 1;
    private AlarmManager alarmManager;
    private List<Alarm> alarms;
    private SoundLogic soundLogic;
    private AlarmListAdapter alarmListAdapter;
    private Ringtone ringtone;
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
                getString(R.string.sound_file_directory));
        alarms = new ArrayList<>();
        currentAlarm = null;
        ActivityCompat.requestPermissions(this, permissions,
                                          REQUEST_RECORD_AUDIO_PERMISSION);
        ringtone = null;
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
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer()).create();
        String alarmsJson = gson.toJson(alarms);
        Log.d(TAG, "JSON: " + alarmsJson);
        editor.putString(getString(R.string.shared_pref_alarms), alarmsJson);
        editor.apply();
    }

    private void loadStateFromPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String alarmsJson = sharedPreferences.getString(getString(R.string.shared_pref_alarms), "[]");
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriDeserializer()).create();
        alarms = gson.fromJson(alarmsJson, new TypeToken<List<Alarm>>(){}.getType());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        boolean startedByAlarm =
                intent.getBooleanExtra(getString(R.string.intent_started_by_alarm_key), false);
        if (startedByAlarm) {
            if (currentAlarm != null) {
                // an existing alarm is playing, stop it
                stopCurrentAlarm();
            }
            int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id), 0);
            currentAlarm = getAlarmById(alarmId);
            ringtone = RingtoneManager.getRingtone(this, currentAlarm.getRingtoneUri());
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
        intent.putExtra(getString(R.string.intent_ringtone_uri_key), alarm.getRingtoneUri());
        startActivityForResult(intent, EDIT_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_ALARM) {
                int hour = intent.getIntExtra(getString(R.string.intent_hour_key), -1);
                int minute = intent.getIntExtra(getString(R.string.intent_minute_key), -1);
                int objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
                int objectiveDifficulty = intent.getIntExtra(getString(R.string.intent_objective_difficulty), 0);
                String alarmDescription = intent.getStringExtra(getString(R.string.intent_description_key));
                String recordingFileName = intent.getStringExtra(getString(R.string.intent_recording_key));
                boolean repeat = intent.getBooleanExtra(getString(R.string.intent_repeat_key), false);
                boolean editMode = intent.getBooleanExtra(getString(R.string.edit_alarm_edit_mode), false);
                Uri ringToneUri = intent.getParcelableExtra(getString(R.string.intent_ringtone_uri_key));
                if (editMode) {
                    int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id), -1);
                    Alarm alarm = getAlarmById(alarmId);
                    if (alarm != null) {
                        boolean timeChanged = timeChanged(alarm, hour, minute);
                        alarm.setHour(hour)
                             .setMinute(minute)
                             .setObjectiveCode(objectiveCode)
                             .setObjectiveDifficulty(objectiveDifficulty)
                             .setAlarmDescription(alarmDescription)
                             .setRecordingFileName(recordingFileName)
                             .setRepeating(repeat)
                             .setRingtoneUri(ringToneUri);
                        if (alarm.isEnabled() && timeChanged) {
                            setAlarm(alarm, true);
                        }
                        Collections.sort(alarms);
                        alarmListAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (hour != -1 && minute != -1) {
                        Alarm alarm = new Alarm()
                                .setHour(hour)
                                .setMinute(minute)
                                .setObjectiveCode(objectiveCode)
                                .setObjectiveDifficulty(objectiveDifficulty)
                                .setAlarmDescription(alarmDescription)
                                .setRecordingFileName(recordingFileName)
                                .setRepeating(repeat)
                                .setRingtoneUri(ringToneUri);
                        addAlarm(alarm);
                        setAlarm(alarm, true);
                    }
                }
            } else if (requestCode == OBJECTIVE) {
                stopCurrentAlarm();
            }
        }
    }

    private boolean timeChanged(Alarm alarm, int hour, int minute) {
        return (alarm.getHour() != hour) || (alarm.getMinute() != minute);
    }

    private void stopCurrentAlarm() {
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
        Collections.sort(alarms);
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
                intent = new Intent(this, TypingObjective.class);
                break;
            case SWIPE:
                intent = new Intent(this, SwipeObjective.class);
                break;
            case FALLING_SHAPES:
                intent = new Intent(this, FallingShapesObjective.class);
                break;
            default:
                stopCurrentAlarm();
                return;
        }
        intent.putExtra(getString(R.string.objective_difficulty), currentAlarm.getObjectiveDifficulty());
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
                convertView.setTag(holder);
            } else {
                holder = (AlarmViewHolder) convertView.getTag();
            }
            holder.alarmText.setText(AlarmUtil.alarmText(alarm.getHour(), alarm.getMinute()));
            holder.descriptionText.setText(alarm.getAlarmDescription());
            holder.repeatChkBox.setChecked(alarm.isRepeating());
            holder.enabledSwitch.setOnCheckedChangeListener(null);
            holder.enabledSwitch.setChecked(alarm.isEnabled());
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
            holder.repeatChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    alarm.setRepeating(checked);
                }
            });
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

    public class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }
}
