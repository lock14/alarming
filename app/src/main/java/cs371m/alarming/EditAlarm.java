package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditAlarm extends AppCompatActivity {
    private static final int EDIT_RECORDING = 0;
    private static final int EDIT_OBJECTIVE = 1;
    private int objectiveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        objectiveCode = 0; // default to math objective?
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
                /**
                 * EXTRACT RECORDING INFORMATION HERE
                 */
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
