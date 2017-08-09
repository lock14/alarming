package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditAlarm extends AppCompatActivity {
    private static final int EDIT_RECORDING = 0;
    private static final int EDIT_OBJECTIVE = 1;
    private static final int EDIT_RINGTONE = 2;
    private int objectiveCode;
    private String recordingFileName;
    private boolean editMode;
    private int alarmId;
    private Uri ringToneUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        objectiveCode = 0; // default to math objective?
        recordingFileName = "";
        editMode = false;
        alarmId = -1;
        ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Intent intent = getIntent();
        if (intent != null) {
            editMode = intent.getBooleanExtra(getString(R.string.edit_alarm_edit_mode), false);
            if (editMode) {
                alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id), -1);
                int hour = intent.getIntExtra(getString(R.string.intent_hour_key), -1);
                int minute = intent.getIntExtra(getString(R.string.intent_minute_key), -1);
                objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
                String alarmDescription = intent.getStringExtra(getString(R.string.intent_description_key));
                recordingFileName = intent.getStringExtra(getString(R.string.intent_recording_key));
                boolean repeat = intent.getBooleanExtra(getString(R.string.intent_repeat_key), false);
                ringToneUri = intent.getParcelableExtra(getString(R.string.intent_ringtone_uri_key));
                TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
                EditText editText = (EditText) findViewById(R.id.alarm_description_edit_txt);
                CheckBox repeatCheckBox = (CheckBox) findViewById(R.id.edit_alarm_repeat_chk_bx);

                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);

                editText.setText(alarmDescription);
                repeatCheckBox.setChecked(repeat);
            }
            setRingToneText();
        }
    }

    private void setRingToneText() {
        String title = RingtoneManager.getRingtone(this, ringToneUri).getTitle(this);
        TextView ringToneNameTextView = (TextView) findViewById(R.id.ringtone_name_txt_bx);
        ringToneNameTextView.setText(modifyTitle(title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if (ringToneUri != null) {
                    TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
                    EditText editText = (EditText) findViewById(R.id.alarm_description_edit_txt);
                    CheckBox repeatCheckBox = (CheckBox) findViewById(R.id.edit_alarm_repeat_chk_bx);
                    if (editText.getText().length() <= 25) {
                        Intent result = new Intent();
                        if (editMode) {
                            result.putExtra(getString(R.string.edit_alarm_edit_mode), true);
                            result.putExtra(getString(R.string.intent_alarm_id), alarmId);
                        }
                        result.putExtra(getString(R.string.intent_hour_key), timePicker.getCurrentHour());
                        result.putExtra(getString(R.string.intent_minute_key), timePicker.getCurrentMinute());
                        result.putExtra(getString(R.string.intent_objective_key), objectiveCode);
                        result.putExtra(getString(R.string.intent_recording_key), recordingFileName);
                        result.putExtra(getString(R.string.intent_description_key),
                                editText.getText().toString());
                        result.putExtra(getString(R.string.intent_repeat_key), repeatCheckBox.isChecked());
                        result.putExtra(getString(R.string.intent_ringtone_uri_key), ringToneUri);
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    } else {
                        Toast.makeText(this, "Description too long, keep at 25 characters or less.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "'None' is not a valid choice for alarm sound.",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_RECORDING) {
                String fileName = intent.getStringExtra(getString(R.string.intent_recording_key));
                if (fileName != null) {
                    recordingFileName = fileName;
                    System.out.println("getting recording file name in EditAlarm activity: " + recordingFileName);
                }
            } else if (requestCode == EDIT_OBJECTIVE) {
                objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
            } else if (requestCode == EDIT_RINGTONE) {
                ringToneUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                setRingToneText();
            }
        }
    }

    public void editRecording(View view) {
        Intent intent = new Intent(this, EditRecording.class);
        intent.putExtra(getString(R.string.intent_recording_key), recordingFileName);
        startActivityForResult(intent, EDIT_RECORDING);
    }

    public void editObjective(View view) {
        Intent intent = new Intent(this, EditObjective.class);
        intent.putExtra(getString(R.string.intent_objective_key), objectiveCode);
        startActivityForResult(intent, EDIT_OBJECTIVE);
    }

    public void editRingTone(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringToneUri);
        startActivityForResult(intent, EDIT_RINGTONE);
    }

    private String modifyTitle(String s) {
        if (s.startsWith("Default")) {
            s = s.replaceFirst("Default ringtone \\(", "");
            s = s.replaceFirst("\\)", "");
        } else if (s.equals("Unknown ringtone")) {
            s = "None";
        }
        return s;
    }
}
