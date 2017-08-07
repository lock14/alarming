package cs371m.alarming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Brian on 7/27/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AlarmReceiver", "context: " + context);
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int alarmId = intent.getIntExtra("ALARM_ID", 0);
        Log.d("AlarmReceiver", "alarmId: " + alarmId);
        intent2.putExtra(context.getString(R.string.intent_alarm_id), alarmId);
        intent2.putExtra(context.getString(R.string.intent_started_by_alarm_key), true);
        //startService(context, intent2);
        context.startActivity(intent2);
    }
}
