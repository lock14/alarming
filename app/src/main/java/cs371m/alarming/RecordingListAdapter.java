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

import static cs371m.alarming.R.string.play;


/**
 * Created by nano on 7/29/17.
 */
//Toast.makeText(getContext(), "Button was clicked for list item", Toast.LENGTH_SHORT).show();
public class RecordingListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mLayout;
    private Button mRecordingPlayButton;
    private Map<String, Boolean> mIsRecording;

    RecordingListAdapter(Context context, int resource, List<String> objects, Button recordingPlayButton) {
        super(context, resource, objects);
        mContext = context;
        mLayout = resource;
        mIsRecording = new HashMap<String, Boolean>();
        setupIsRecording(objects);
        mRecordingPlayButton = recordingPlayButton;
    }

    private void setupIsRecording(List<String> data) {
        for (String string : data) {
            mIsRecording.put(string, false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RecordingViewHolder mainRecordingViewHolder = null;
        System.out.println("----------------------------------------------------------------");
        System.out.println("getting view");
        System.out.println("working on " + getItem(position));
        if(convertView == null) {
            System.out.println("setting up convertView");
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            RecordingViewHolder recordingViewHolder = new RecordingViewHolder();
            recordingViewHolder.recordingName = (TextView) convertView.findViewById(R.id.recording_name);
            recordingViewHolder.playButton = (Button) convertView.findViewById(R.id.play_button);
            recordingViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button playButton = (Button) v;
                    if (String.valueOf(playButton.getText()).equals(mContext.getString(R.string.play))) {
                        System.out.println("click play");
                        setAllButtonsToPlay();
                        mIsRecording.put(getItem(position), true);
                        //playButton.setText(R.string.stop);
                        notifyDataSetChanged();
                    } else {
                        System.out.println("click stop");
                        mIsRecording.put(getItem(position), false);
                        //playButton.setText(R.string.play);
                        notifyDataSetChanged();
                        // at this point stop playing any sound
                    }
//                    Toast.makeText(getContext(), "Button was clicked for list item " + position + "\n"
//                            + String.valueOf(playButton.getText()) + "\n" +
//                                    mContext.getString(R.string.play)
//                            , Toast.LENGTH_SHORT).show();

                }
            });
            recordingViewHolder.recordingName.setText(getItem(position));
            //setButtonText(recordingViewHolder.playButton, position);
            //recordingViewHolder.playButton.callOnClick();
            recordingViewHolder.playButton.setText(R.string.play);
            convertView.setTag(recordingViewHolder);
        } else {
            System.out.println("changing convertView");
            mainRecordingViewHolder = (RecordingViewHolder) convertView.getTag();
            mainRecordingViewHolder.recordingName.setText(getItem(position));
            mainRecordingViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button playButton = (Button) v;
                    if (String.valueOf(playButton.getText()).equals(mContext.getString(R.string.play))) {
                        System.out.println("click play");
                        setAllButtonsToPlay();
                        mIsRecording.put(getItem(position), true);
                        //playButton.setText(R.string.stop);
                        notifyDataSetChanged();
                    } else {
                        System.out.println("click stop");
                        mIsRecording.put(getItem(position), false);
                        //playButton.setText(R.string.play);
                        notifyDataSetChanged();
                        // at this point stop playing any sound
                    }
//                    Toast.makeText(getContext(), "Button was clicked for list item " + position + "\n"
//                            + String.valueOf(playButton.getText()) + "\n" +
//                                    mContext.getString(R.string.play)
//                            , Toast.LENGTH_SHORT).show();

                }
            });
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

    private void setAllButtonsToPlay() {
        for (String key : mIsRecording.keySet()) {
            mIsRecording.put(key, false);
        }
        // at this point stop any sound
        //notifyDataSetChanged();
    }

}
