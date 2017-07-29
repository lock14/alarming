package cs371m.alarming;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
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
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setAlarm(View view) {
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getFragmentManager(), "TimePicker");
    }

    public void editRecording(View view) {
        Intent intent = new Intent(this, EditRecording.class);
        startActivity(intent);
    }

    public void editObjective(View view) {
        Intent intent = new Intent(this, EditObjective.class);
        startActivity(intent);
    }
}
