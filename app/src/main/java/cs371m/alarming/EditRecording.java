package cs371m.alarming;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String recordingFileName = mRecordingListAdapter.mSetFileName; //mSoundFileManager.getAlarmRecordingName();
        if (recordingFileName != null) {
            Log.d("Edit Recording", "passing back recording from EditRecording activity: " + recordingFileName);
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
        mRecordingListAdapter = new RecordingListAdapter(this, mData);
        mRecordingList.setAdapter(mRecordingListAdapter);
        if (!mData.isEmpty()) {
            enableSelectRecordingText();
        }
    }

    private void enableSelectRecordingText() {
        View lineSeparator = (View) findViewById(R.id.select_line_separator);
        TextView selectRecordingTextView = (TextView) findViewById(R.id.select_recording_text);
        selectRecordingTextView.setVisibility(View.VISIBLE);
        lineSeparator.setVisibility(View.VISIBLE);
    }

    private void disableSelectRecordingText() {
        View lineSeparator = (View) findViewById(R.id.select_line_separator);
        TextView selectRecordingTextView = (TextView) findViewById(R.id.select_recording_text);
        selectRecordingTextView.setVisibility(View.INVISIBLE);
        lineSeparator.setVisibility(View.INVISIBLE);
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
                final Button button = (Button) v;
                String buttonText = String.valueOf(button.getText());
                if (mSoundFileManager.hasTemporaryRecording()) {
                    if (buttonText.equals(getString(R.string.play))) {
                        mSoundLogic.stopPlaying();
                        if (mRecordingListAdapter.mPlayingButton != null) {
                            mRecordingListAdapter.mPlayingButton.setText(getString(R.string.play));
                            mRecordingListAdapter.mPlayingButton = null;
                        }
                        mPlayRecording.setText(getString(R.string.stop));
                        mSoundLogic.playCurrentSound(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                button.setText(getString(R.string.play));
                            }
                        });
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
                } else if (recordingTitleInput.length() > 10) {
                    Toast.makeText(mEditRecording, "Length of recording title too long, keep it at " +
                            "10 characters or less.", Toast.LENGTH_SHORT).show();
                }

                else {
                    mSoundFileManager.saveTemporarySoundFileAs(recordingTitleInput);
                    mRecordingTitleInput.setText("");
                    mRecordingListAdapter.add(recordingTitleInput);
                    enableSelectRecordingText();
                    mRecordingListAdapter.notifyDataSetChanged();
                    mPlayRecording.setEnabled(false);
                    mSaveRecording.setEnabled(false);
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

    private class RecordingListAdapter extends ArrayAdapter<String> {
        private String mSetFileName;
        private Button mPlayingButton;
        private Button mSetButton;

        public RecordingListAdapter(@NonNull Context context, @NonNull List<String> objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RecordingViewHolder holder = null;
            final String fileName = getItem(position);
            if (convertView == null) {
                holder = new RecordingViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.recording_list_row, parent, false);
                holder.mRecordingName = convertView.findViewById(R.id.recording_name);
                holder.mPlayButton = convertView.findViewById(R.id.play_button);
                holder.mSetRecording = convertView.findViewById(R.id.set_recording);
                holder.mDeleteRecording = convertView.findViewById(R.id.delete_recording);
                convertView.setTag(holder);
            } else {
                holder = (RecordingViewHolder) convertView.getTag();
            }
            final RecordingViewHolder finalHolder = holder;
            holder.mRecordingName.setText(fileName);
            holder.mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (String.valueOf(finalHolder.mPlayButton.getText())
                            .equals(getContext().getString(R.string.play))) {
                        mSoundLogic.stopPlaying();
                        mPlayRecording.setText(getContext().getString(R.string.play));
                        if (mPlayingButton != null) {
                            mPlayingButton.setText(getContext().getString(R.string.play));
                        }
                        mPlayingButton = finalHolder.mPlayButton;
                        finalHolder.mPlayButton.setText(getContext().getString(R.string.stop));
                        mSoundLogic.playSoundByFileName(fileName, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mPlayingButton = null;
                                finalHolder.mPlayButton.setText(getContext().getString(R.string.play));
                            }
                        });
                    } else {
                        mPlayingButton = null;
                        mSoundLogic.stopPlaying();
                        finalHolder.mPlayButton.setText(getContext().getString(R.string.play));
                    }
                }
            });
            if (mSetFileName != null && mSetFileName.equals(fileName)) {
                holder.mSetRecording.setText(getContext().getString(R.string.unset));
            }
            holder.mSetRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (String.valueOf(finalHolder.mSetRecording.getText())
                            .equals(getContext().getString(R.string.set))) {
                        if (mSetButton != null) {
                            mSetButton.setText(getContext().getString(R.string.set));
                        }
                        mSetButton = finalHolder.mSetRecording;
                        mSetFileName = getItem(position);
                        finalHolder.mSetRecording.setText(getContext().getString(R.string.unset));
                    } else {
                        mSetButton = null;
                        mSetFileName = null;
                        finalHolder.mSetRecording.setText(getContext().getString(R.string.set));
                    }
                }
            });
            holder.mDeleteRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSoundFileManager.removeSoundFileByName(fileName);
                    if (mSetFileName != null && mSetFileName.equals(fileName)) {
                        mSetFileName = null;
                    }
                    if (mPlayingButton == finalHolder.mPlayButton) {
                        mPlayingButton.setText(getContext().getString(R.string.play));
                        mSoundLogic.stopPlaying();
                    }
                    mData.remove(fileName);
                    if (mData.isEmpty()) {
                        disableSelectRecordingText();
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        void setAlarm(String fileName) {
            mSetFileName = fileName;
            notifyDataSetChanged();
        }
    }
}
