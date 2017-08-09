package cs371m.alarming;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import cs371.record_sound_logic.RecordLogic;
import cs371.record_sound_logic.SoundFileManager;
import cs371.record_sound_logic.SoundLogic;


public class EditRecording extends AppCompatActivity {
    private ArrayList<String> mData = new ArrayList<>();
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
    private String mAlarmRecordingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);
        setupRecordingInfrastructure();
        Intent intent = getIntent();
        if (intent != null) {
            String recordingFileName = intent.getStringExtra(getString(R.string.intent_recording_key));
            if (!TextUtils.isEmpty(recordingFileName)) {
                mRecordingListAdapter.setAlarm(recordingFileName);
            }
        }
    }

    private void generateDummyData() {
        for (int i = 0; i < 5; ++i) {
            mData.add(String.valueOf(i));
        }
    }

    private void generateRecordingData() {
        Collections.addAll(mData, mSoundFileManager.getSoundFileList());
    }

    @Override
    public void onBackPressed() {
        if (!mRecordOnStart) {
            stopRecording(mRecordButton);
        }
        Intent result = new Intent();
        String recordingFileName = mRecordingListAdapter.mAlarmSoundFileName; //mSoundFileManager.getAlarmRecordingName();
        mRecordingListAdapter.mAlarmSoundFileName = null;
        if (recordingFileName != null) {
            System.out.println("passing back recording from EditRecording activity: " + recordingFileName);
            result.putExtra(getString(R.string.intent_recording_key), recordingFileName);
        }
        setResult(Activity.RESULT_OK, result);
        finish();
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
        mSoundLogic.stopPlaying();
        mRecordingListAdapter.setAllButtonsToPlay();
        mRecordingListAdapter.notifyDataSetChanged();
        recordButton.setBackgroundResource(R.drawable.record_stop);
        mRecordLogic.startRecordingIntoTemporarySoundFile();
        mPlayRecording.setEnabled(true);
        mSaveRecording.setEnabled(true);
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
        mRecordingListAdapter = new RecordingListAdapter(this,
                R.layout.recording_list_row, mData, (Button) findViewById(R.id.play_recording), mSoundLogic, mRecordLogic, mSoundFileManager);
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
                if (mSoundFileManager.hasTemporaryRecording()) {
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
                } else {
                    Toast.makeText(mEditRecording, "You must record, before playing.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSaveRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(mRecordButton);
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
                    mRecordingListAdapter.mIsPlaying.put(recordingTitleInput, false);
                    mRecordingListAdapter.mIsSetToAlarm.put(recordingTitleInput, false);
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
