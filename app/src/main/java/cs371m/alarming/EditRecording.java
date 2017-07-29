package cs371m.alarming;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;


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

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_recording);
//        ListView lv = (ListView) findViewById(R.id.recording_list);
//
//        // Instanciating an array list (you don't need to do this,
//        // you already have yours).
//        List<String> your_array_list = new ArrayList<String>();
//        your_array_list.add("foo");
//        your_array_list.add("bar");
//
//        // This is the array adapter, it takes the context of the activity as a
//        // first parameter, the type of list view as a second parameter and your
//        // array as a third parameter.
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                your_array_list );
//
//        lv.setAdapter(arrayAdapter);
//
//    }

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
