package cs371m.alarming;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.View;

/**
 * Created by Brian on 7/27/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        MainActivity.getDisableButton().setVisibility(View.VISIBLE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
    }
}
