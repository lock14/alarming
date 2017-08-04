package cs371m.alarming;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import cs371.record_sound_logic.RecordLogic;
import cs371.record_sound_logic.SoundFileManager;
import cs371.record_sound_logic.SoundLogic;


public class EditRecording extends AppCompatActivity {
    private ArrayList<String> mData = new ArrayList<String>();
    private RecordLogic mRecordLogic;
    private SoundLogic mSoundLogic;
    private SoundFileManager mSoundFileManager;
    private boolean mRecordOnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);

        mRecordLogic = new RecordLogic(new ContextWrapper(getApplicationContext()), getString(R.string.sound_file_directory));
        mSoundLogic = new SoundLogic(new ContextWrapper((getApplicationContext())), getString(R.string.sound_file_directory));
        mSoundFileManager = new SoundFileManager(new ContextWrapper((getApplicationContext())), getString(R.string.sound_file_directory));
        mRecordOnStart = true;
        setOnClickListenersForButtons();
        ListView lv = (ListView) findViewById(R.id.recording_list);
        generateRecordingData();
        RecordingListAdapter recordingListAdapter = new RecordingListAdapter(this,
                R.layout.recording_list_row, mData, (Button) findViewById(R.id.play_recording), mSoundLogic);
        lv.setAdapter(recordingListAdapter);
    }


    private void generateRecordingData() {
        String[] recordingData = mSoundFileManager.getSoundFileList();
        for (String recordingName : recordingData) {
            mData.add(recordingName);
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
                String recordingFileName = mSoundFileManager.saveTemporarySoundFileToAlarm();
                if (recordingFileName != null) {
                    result.putExtra(getString(R.string.intent_recording_key), recordingFileName);
                }
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mRecordOnStart) {
            stopRecording((Button) findViewById(R.id.record_button));
        }
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        mRecordLogic.releaseMediaRecorder();
        mSoundLogic.releaseMediaPlayer();
        super.onStop();
    }

    // record ui logic
    private void startRecording(Button recordButton) {
        mRecordOnStart = false;
        Button recordPlayButton = (Button) findViewById(R.id.play_recording);
        recordPlayButton.setText(getString(R.string.play));
        mSoundLogic.stopPlaying();
        recordButton.setBackgroundResource(R.drawable.record_stop);
        mRecordLogic.startRecordingIntoTemporarySoundFile();
    }

    private void stopRecording(Button recordButton) {
        mRecordOnStart = true;
        recordButton.setBackgroundResource(R.drawable.record_start);
        mRecordLogic.stopRecording();
    }

    private void setOnClickListenersForButtons() {
        // set record listener
        Button recordButton = (Button) findViewById(R.id.record_button);
        Button recordPlayButton = (Button) findViewById(R.id.play_recording);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordOnStart) {
                    startRecording((Button) v);
                } else {
                    stopRecording((Button) v);
                }
            }
        });

        recordPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = String.valueOf(button.getText());
                if (buttonText.equals(getString(R.string.play))) {
                    RecordingTaskBundle recordingTaskBundle =
                            new RecordingTaskBundle();
                    PlayTemporaryRecordingTask playTemporaryRecordingTask = new PlayTemporaryRecordingTask();
                    recordingTaskBundle.mRecordingPlayButton = button;
                    recordingTaskBundle.mSoundLogic = mSoundLogic;
                    button.setText(getString(R.string.stop));
                    playTemporaryRecordingTask.execute(recordingTaskBundle);
                } else {
                    mSoundLogic.stopPlaying();
                    button.setText((getString(R.string.play)));
                }
            }
        });
    }


}
