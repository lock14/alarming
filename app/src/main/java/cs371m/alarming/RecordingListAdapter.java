package cs371m.alarming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs371.record_sound_logic.RecordLogic;
import cs371.record_sound_logic.SoundFileManager;
import cs371.record_sound_logic.SoundLogic;

import static cs371m.alarming.PlayTemporaryRecordingTask.LOG_TAG;
import static cs371m.alarming.R.string.play;


/**
 * Created by nano on 7/29/17.
 */
//Toast.makeText(getContext(), "Button was clicked for list item", Toast.LENGTH_SHORT).show();
public class RecordingListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mLayout;
    private Button mRecordingPlayButton;
    public Map<String, Boolean> mIsPlaying;
    public Map<String, Boolean> mIsSetToAlarm;
    private SoundLogic mSoundLogic;
    private SoundFileManager mSoundFileManager;
    private RecordingListAdapter mRecordingListAdapter;
    public String mAlarmSoundFileName = null;

    RecordingListAdapter(Context context, int resource, List<String> objects, Button recordingPlayButton, SoundLogic soundLogic,
                         RecordLogic recordLogic, SoundFileManager soundFileManager) {
        super(context, resource, objects);
        mContext = context;
        mLayout = resource;
        mIsPlaying = new HashMap<>();
        mIsSetToAlarm = new HashMap<>();
        mRecordingPlayButton = recordingPlayButton;
        mSoundLogic = soundLogic;
        RecordLogic mRecordLogic = recordLogic;
        mSoundFileManager = soundFileManager;
        mRecordingListAdapter = this;
        setupMaps(objects);
    }

    private void setupMaps(List<String> data) {
//        String alarmRecordingName = mSoundFileManager.getAlarmRecordingName();
        for (String string : data) {
            mIsPlaying.put(string, false);
            if (mAlarmSoundFileName != null && string.equals(mAlarmSoundFileName)) {
                mIsSetToAlarm.put(string, true);
            } else {
                mIsSetToAlarm.put(string, false);
            }

        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RecordingViewHolder mainRecordingViewHolder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            RecordingViewHolder recordingViewHolder = new RecordingViewHolder();
            recordingViewHolder.mRecordingName = convertView.findViewById(R.id.recording_name);
            recordingViewHolder.mPlayButton = convertView.findViewById(R.id.play_button);
            recordingViewHolder.mSetRecording = convertView.findViewById(R.id.set_recording);
            recordingViewHolder.mDeleteRecording = convertView.findViewById(R.id.delete_recording);
            ListPlayListener listPlayListener = new ListPlayListener();
            ListSetListener listSetListener = new ListSetListener();
            ListDeleteListener listDeleteListener = new ListDeleteListener();
            listPlayListener.mRecordingFileName = getItem(position);
            listSetListener.mRecordingFileName = getItem(position);
            listDeleteListener.mRecordingFileName = getItem(position);
            recordingViewHolder.mPlayButton.setOnClickListener(listPlayListener);
            recordingViewHolder.mRecordingName.setText(getItem(position));
            recordingViewHolder.mSetRecording.setOnClickListener(listSetListener);
            recordingViewHolder.mDeleteRecording.setOnClickListener(listDeleteListener);
            setSetButtonText(recordingViewHolder.mSetRecording, position);
            convertView.setTag(recordingViewHolder);
        } else {
            mainRecordingViewHolder = (RecordingViewHolder) convertView.getTag();
            mainRecordingViewHolder.mRecordingName.setText(getItem(position));
            ListPlayListener listPlayListener = new ListPlayListener();
            ListSetListener listSetListener = new ListSetListener();
            ListDeleteListener listDeleteListener = new ListDeleteListener();
            listSetListener.mRecordingFileName = getItem(position);
            listDeleteListener.mRecordingFileName = getItem(position);
            listPlayListener.mRecordingFileName = getItem(position);
            mainRecordingViewHolder.mPlayButton.setOnClickListener(listPlayListener);
            mainRecordingViewHolder.mSetRecording.setOnClickListener(listSetListener);
            mainRecordingViewHolder.mDeleteRecording.setOnClickListener(listDeleteListener);
            setPlayButtonText(mainRecordingViewHolder.mPlayButton, position);
            setSetButtonText(mainRecordingViewHolder.mSetRecording, position);
        }
        return convertView;
    }

    private void setPlayButtonText(Button button, int position) {
        if (mIsPlaying.get(getItem(position))) {
            button.setText(R.string.stop);
        } else {
            button.setText(play);
        }
    }

    private void setSetButtonText(Button button, int position) {
        if (mIsSetToAlarm.get(getItem(position))) {
            button.setText(R.string.unset);
        } else {
            button.setText(R.string.set);
        }
    }

    public void setAllButtonsToPlay() {
        for (String key : mIsPlaying.keySet()) {
            mIsPlaying.put(key, false);
        }
        mRecordingPlayButton.setText(R.string.play);
    }

    public void disableAllRecordings() {
        for (String key : mIsSetToAlarm.keySet()) {
            mIsSetToAlarm.put(key, false);
        }
    }

    private class ListPlayListener implements View.OnClickListener {
        private String mRecordingFileName;

        @Override
        public void onClick(View v) {
            Button playButton = (Button) v;
            if (String.valueOf(playButton.getText()).equals(mContext.getString(R.string.play))) {
                setAllButtonsToPlay();
                mSoundLogic.stopPlaying();
                mIsPlaying.put(mRecordingFileName, true);
                notifyDataSetChanged();
                        RecordingTaskBundle recordingTaskBundle = new RecordingTaskBundle();
                        recordingTaskBundle.mSoundLogic = mSoundLogic;
                        recordingTaskBundle.mRecordingFileName = mRecordingFileName;
                        recordingTaskBundle.mRecordingListAdapter = mRecordingListAdapter;
                        PlayRecordingTask playRecordingTask = new PlayRecordingTask();
                        playRecordingTask.execute(recordingTaskBundle);
            } else {
                mIsPlaying.put(mRecordingFileName, false);
                notifyDataSetChanged();
                        mSoundLogic.stopPlaying();
            }

        }
    }

    private class ListSetListener implements View.OnClickListener {
        private String mRecordingFileName;

        @Override
        public void onClick(View v) {
            Button setRecording = (Button) v;
            if (!mIsSetToAlarm.get(mRecordingFileName) &&
                    String.valueOf(setRecording.getText()).equals(mContext.getString(R.string.set
            ))) {
                mSoundFileManager.removeSoundFilesInAlarmDirectory();
                mSoundFileManager.saveSoundFileToAlarmFileDirectory(mRecordingFileName);
                mAlarmSoundFileName = mRecordingFileName;
                disableAllRecordings();
                mIsSetToAlarm.put(mRecordingFileName, true);
                mRecordingListAdapter.notifyDataSetChanged();
            } else if (mIsSetToAlarm.get(mRecordingFileName) &&
                    String.valueOf(setRecording.getText()).equals(mContext.getString(R.string.unset))) {
                mSoundFileManager.removeSoundFilesInAlarmDirectory();
                disableAllRecordings();
                mIsSetToAlarm.put(mRecordingFileName, false);
                mRecordingListAdapter.notifyDataSetChanged();
            } else {
                Log.d(LOG_TAG, "logic error in onClick ListSetListener");
            }


        }
    }

    private class ListDeleteListener implements View.OnClickListener {
        private String mRecordingFileName;

        @Override
        public void onClick(View v) {
            mSoundFileManager.removeSoundFileByName(mRecordingFileName);
//            if (mRecordingFileName.equals(mSoundFileManager.getAlarmRecordingName())) {
//                mSoundFileManager.removeSoundFilesInAlarmDirectory();
//            }
            if (mAlarmSoundFileName != null && mRecordingFileName.equals(mAlarmSoundFileName)) {
                mAlarmSoundFileName = null;
            }
            mIsSetToAlarm.remove(mRecordingFileName);
            mIsPlaying.remove(mRecordingFileName);
            mRecordingListAdapter.remove(mRecordingFileName);
            mRecordingListAdapter.notifyDataSetChanged();
        }
    }

}
