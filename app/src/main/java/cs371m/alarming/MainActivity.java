package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int EDIT_ALARM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            }
        }
    }
}
