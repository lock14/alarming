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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    private RecordingListAdapter mRecordingListAdapter;
    private EditRecording mEditRecording;
    private Button mRecordButton;
    private Button mPlayRecording;
    private ListView mRecordingList;
    private EditText mRecordingTitleInput;
    private Button mSaveRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);
        setupRecordingInfrastructure();
    }

    private void generateDummyData() {
        for (int i = 0; i < 5; ++i) {
            mData.add(String.valueOf(i));
        }
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
            stopRecording(mRecordButton);
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
        //mRecordButton.setText(getString(R.string.play));
        mSoundLogic.stopPlaying();
        recordButton.setBackgroundResource(R.drawable.record_stop);
        mRecordLogic.startRecordingIntoTemporarySoundFile();
    }

    private void stopRecording(Button recordButton) {
        mRecordOnStart = true;
        recordButton.setBackgroundResource(R.drawable.record_start);
        mRecordLogic.stopRecording();
    }

    private void setupRecordingInfrastructure() {
        mRecordLogic = new RecordLogic(new ContextWrapper(getApplicationContext()), getString(R.string.sound_file_directory));
        mSoundLogic = new SoundLogic(new ContextWrapper((getApplicationContext())), getString(R.string.sound_file_directory));
        mSoundFileManager = new SoundFileManager(new ContextWrapper((getApplicationContext())), getString(R.string.sound_file_directory));
        mRecordOnStart = true;
        setupGuiComponents();
        setOnClickListenersForButtons();
        generateRecordingData();
        //generateDummyData();
        mRecordingListAdapter = new RecordingListAdapter(this,
                R.layout.recording_list_row, mData, (Button) findViewById(R.id.play_recording), mSoundLogic);
        mRecordingList.setAdapter(mRecordingListAdapter);
    }

    private void setOnClickListenersForButtons() {
        // set record listener
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordOnStart) {
                    startRecording((Button) v);
                } else {
                    stopRecording((Button) v);
                }
            }
        });

        mPlayRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = String.valueOf(button.getText());
                if (buttonText.equals(getString(R.string.play))) {
                    mSoundLogic.stopPlaying();
                    RecordingTaskBundle recordingTaskBundle =
                            new RecordingTaskBundle();
                    PlayTemporaryRecordingTask playTemporaryRecordingTask = new PlayTemporaryRecordingTask();
                    recordingTaskBundle.mRecordingPlayButton = button;
                    recordingTaskBundle.mSoundLogic = mSoundLogic;
                    mRecordingListAdapter.setAllButtonsToPlay();
                    mRecordingListAdapter.notifyDataSetChanged();
                    button.setText(getString(R.string.stop));
                    playTemporaryRecordingTask.execute(recordingTaskBundle);
                } else {
                    mSoundLogic.stopPlaying();
                    button.setText((getString(R.string.play)));
                }
            }
        });

        mSaveRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recordingTitleInput = mRecordingTitleInput.getText().toString();
                if (recordingTitleInput.length() == 0) {
                    Toast.makeText(mEditRecording, "Length of recording title too short, title can have a max of 20 characters",
                            Toast.LENGTH_SHORT).show();
                } else if (recordingTitleInput.length() > 20) {
                    Toast.makeText(mEditRecording, "Length of recording title too long, keep it under " +
                            "20 characters.", Toast.LENGTH_SHORT).show();
                }

                else {
                    mSoundFileManager.saveTemporarySoundFileAs(recordingTitleInput);
                    mRecordingTitleInput.setText("");
                    mRecordingListAdapter.add(recordingTitleInput);
                    mRecordingListAdapter.mIsRecording.put(recordingTitleInput, false);
                    mRecordingListAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void setupGuiComponents() {

        mEditRecording = this;
        mRecordButton = (Button) findViewById(R.id.record_button);
        mPlayRecording = (Button) findViewById(R.id.play_recording);
        mRecordingList = (ListView) findViewById(R.id.recording_list);
        mRecordingTitleInput = (EditText) findViewById(R.id.recording_title_input);
        mSaveRecording = (Button) findViewById(R.id.save_recording);
    }


}
