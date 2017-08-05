package cs371m.alarming;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import cs371.record_sound_logic.SoundLogic;

/**
 * Created by nano on 7/31/17.
 */

public class PlayTemporaryRecordingTask extends AsyncTask<RecordingTaskBundle, Void, Button> {
    static String LOG_TAG = "PlyTempRecordingTask";
    protected Button doInBackground(RecordingTaskBundle... recordingTaskBundles) {
        SoundLogic soundLogic = recordingTaskBundles[0].mSoundLogic;
        Button recordingPlayButton = recordingTaskBundles[0].mRecordingPlayButton;
        int duration = soundLogic.playCurrentSound();
        try {
            Thread.sleep(duration);
        } catch (InterruptedException interruptedException) {
            Log.d(LOG_TAG, "play temporary recording did not sleep right");
        }

        return recordingPlayButton;
    }

    protected void onPostExecute(Button playRecordingButton) {
        playRecordingButton.setText(R.string.play);
    }

}
