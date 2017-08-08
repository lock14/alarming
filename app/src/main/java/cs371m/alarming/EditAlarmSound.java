package cs371m.alarming;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditAlarmSound extends AppCompatActivity {
    private RingtoneManager ringtoneManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm_sound);
        ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor ringtoneCursor = ringtoneManager.getCursor();
        ListView alarmListView = (ListView) findViewById(R.id.ringtone_list);
        alarmListView.setAdapter(new RingToneListAdapter(this, new Uri[ringtoneCursor.getCount()]));
    }

    public class RingToneListAdapter extends ArrayAdapter<Uri> {

        RingToneListAdapter(Context context, Uri[] ringtoneUris) {
            super(context, -1, ringtoneUris);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Ringtone ringtone = ringtoneManager.getRingtone(position);
            final RingtoneViewHolder holder;
            if (convertView == null) {
                holder = new RingtoneViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.ringtone_list_item, parent, false);
                holder.ringToneName = convertView.findViewById(R.id.ringtone_name_txt_bx);
                convertView.setTag(holder);
            } else {
                holder = (RingtoneViewHolder) convertView.getTag();
            }
            holder.ringToneName.setText(ringtone.getTitle(EditAlarmSound.this));

            return convertView;
        }
    }

    private class RingtoneViewHolder {
        TextView ringToneName;
    }
}
