package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class EditRecording extends AppCompatActivity {
    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);
        ListView lv = (ListView) findViewById(R.id.recording_list);
        generateDummyData();
        RecordingListAdapter recordingListAdapter = new RecordingListAdapter(this, R.layout.recording_list_row, data);
        lv.setAdapter(recordingListAdapter);

    }
    

    private void generateDummyData() {
        for (int i = 0; i < 5; ++i) {
            data.add(String.valueOf(i));
        }
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
                Intent result = new Intent();
                /**
                 * populate result with data to send back
                 * e.g. result.putExtra(String key, ? data);
                 */
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
