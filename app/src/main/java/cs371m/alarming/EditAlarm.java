package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditAlarm extends AppCompatActivity {
    private static final int EDIT_RECORDING = 0;
    private static final int EDIT_OBJECTIVE = 1;
    private int objectiveCode;
    private String recordingFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        objectiveCode = 0; // default to math objective?
        recordingFileName = "";
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
                TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
                Intent result = new Intent();
                result.putExtra(getString(R.string.intent_hour_key), timePicker.getCurrentHour());
                result.putExtra(getString(R.string.intent_minute_key), timePicker.getCurrentMinute());
                result.putExtra(getString(R.string.intent_objective_key), objectiveCode);
                result.putExtra(getString(R.string.intent_recording_key), recordingFileName);
                EditText editText = (EditText) findViewById(R.id.alarm_description_edit_txt);
                result.putExtra(getString(R.string.intent_description_key),
                        editText.getText().toString());
                setResult(Activity.RESULT_OK, result);
                finish();
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
                }
            } else if (requestCode == EDIT_OBJECTIVE) {
                objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
            }
        }
    }

    public void editRecording(View view) {
        Intent intent = new Intent(this, EditRecording.class);
        startActivityForResult(intent, EDIT_RECORDING);
    }

    public void editObjective(View view) {
        Intent intent = new Intent(this, EditObjective.class);
        startActivityForResult(intent, EDIT_OBJECTIVE);
    }
}
