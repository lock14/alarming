package cs371m.alarming;

import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by nano on 7/29/17.
 */

public class RecordingListAdapter extends ArrayAdapter<String> {
    private int layout;
    RecordingListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        layout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecordingViewHolder mainRecordingViewHolder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            RecordingViewHolder recordingViewHolder = new RecordingViewHolder();
            recordingViewHolder.recordingName = (TextView) convertView.findViewById(R.id.recording_name);
            recordingViewHolder.playButton = (Button) convertView.findViewById(R.id.play_button);
            recordingViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button was clicked for list item", Toast.LENGTH_SHORT).show();
                }
            });
            recordingViewHolder.recordingName.setText(getItem(position));
            convertView.setTag(recordingViewHolder);
        } else {
            mainRecordingViewHolder = (RecordingViewHolder) convertView.getTag();
            mainRecordingViewHolder.recordingName.setText(getItem(position));
        }
        return convertView;
    }

}
