package cs371m.alarming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs371.record_sound_logic.SoundLogic;

import static cs371m.alarming.R.string.play;


/**
 * Created by nano on 7/29/17.
 */
//Toast.makeText(getContext(), "Button was clicked for list item", Toast.LENGTH_SHORT).show();
public class RecordingListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mLayout;
    private Button mRecordingPlayButton;
    public Map<String, Boolean> mIsRecording;
    private SoundLogic mSoundLogic;
    private RecordingListAdapter mRecordingListAdapter;

    RecordingListAdapter(Context context, int resource, List<String> objects, Button recordingPlayButton, SoundLogic soundLogic) {
        super(context, resource, objects);
        mContext = context;
        mLayout = resource;
        mIsRecording = new HashMap<String, Boolean>();
        setupIsRecording(objects);
        mRecordingPlayButton = recordingPlayButton;
        mSoundLogic = soundLogic;
        mRecordingListAdapter = this;
    }

    private void setupIsRecording(List<String> data) {
        for (String string : data) {
            mIsRecording.put(string, false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RecordingViewHolder mainRecordingViewHolder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            RecordingViewHolder recordingViewHolder = new RecordingViewHolder();
            recordingViewHolder.recordingName = (TextView) convertView.findViewById(R.id.recording_name);
            recordingViewHolder.playButton = (Button) convertView.findViewById(R.id.play_button);
            ListPlayListener listPlayListener = new ListPlayListener();
            listPlayListener.mRecordingFileName = getItem(position);
            recordingViewHolder.playButton.setOnClickListener(listPlayListener);
            recordingViewHolder.recordingName.setText(getItem(position));
            recordingViewHolder.playButton.setText(R.string.play);
            convertView.setTag(recordingViewHolder);
        } else {
            mainRecordingViewHolder = (RecordingViewHolder) convertView.getTag();
            mainRecordingViewHolder.recordingName.setText(getItem(position));
            ListPlayListener listPlayListener = new ListPlayListener();
            listPlayListener.mRecordingFileName = getItem(position);
            mainRecordingViewHolder.playButton.setOnClickListener(listPlayListener);
            setButtonText(mainRecordingViewHolder.playButton, position);
        }
        return convertView;
    }

    private void setButtonText(Button button, int position) {
        if (mIsRecording.get(getItem(position))) {
            button.setText(R.string.stop);
        } else {
            button.setText(play);
        }
    }

    public void setAllButtonsToPlay() {
        for (String key : mIsRecording.keySet()) {
            mIsRecording.put(key, false);
        }
        mRecordingPlayButton.setText(R.string.play);
    }

    private class ListPlayListener implements View.OnClickListener {
        private String mRecordingFileName;

        @Override
        public void onClick(View v) {
            Button playButton = (Button) v;
            if (String.valueOf(playButton.getText()).equals(mContext.getString(R.string.play))) {
                setAllButtonsToPlay();
                mSoundLogic.stopPlaying();
                mIsRecording.put(mRecordingFileName, true);
                notifyDataSetChanged();
                        RecordingTaskBundle recordingTaskBundle = new RecordingTaskBundle();
                        recordingTaskBundle.mSoundLogic = mSoundLogic;
                        recordingTaskBundle.mRecordingFileName = mRecordingFileName;
                        recordingTaskBundle.mRecordingListAdapter = mRecordingListAdapter;
                        PlayRecordingTask playRecordingTask = new PlayRecordingTask();
                        playRecordingTask.execute(recordingTaskBundle);
            } else {
                mIsRecording.put(mRecordingFileName, false);
                notifyDataSetChanged();
                        mSoundLogic.stopPlaying();
            }

        }
    }

}
