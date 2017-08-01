package cs371m.alarming;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;

/**
 * Created by Brian on 7/27/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AlarmReceiver", "context: " + context);
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.putExtra(context.getString(R.string.intent_started_by_alarm_key), true);
        context.startActivity(intent2);
    }
}
