package cs371m.alarming;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EditAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
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
