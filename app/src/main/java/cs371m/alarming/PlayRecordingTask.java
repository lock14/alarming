package cs371m.alarming;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import cs371.record_sound_logic.SoundLogic;

import static cs371m.alarming.PlayTemporaryRecordingTask.LOG_TAG;

/**
 * Created by nano on 8/3/17.
 */

public class PlayRecordingTask extends AsyncTask<RecordingTaskBundle, Void, RecordingTaskReturnBundle> {

    @Override
    protected RecordingTaskReturnBundle doInBackground(RecordingTaskBundle... recordingTaskBundels) {
        RecordingTaskBundle recordingTaskBundle = recordingTaskBundels[0];
        SoundLogic soundLogic = recordingTaskBundle.mSoundLogic;
        Button recordingPlayButton = recordingTaskBundle.mRecordingPlayButton;
        String recordingFileName = recordingTaskBundle.mRecordingFileName;
        RecordingListAdapter recordingListAdapter = recordingTaskBundle.mRecordingListAdapter;
        RecordingTaskReturnBundle recordingTaskReturnBundle = new RecordingTaskReturnBundle();
        recordingTaskReturnBundle.mRecordingListAdapter = recordingListAdapter;
        recordingTaskReturnBundle.mRecordingFileName = recordingFileName;
        int duration = soundLogic.playSoundByFileName(recordingFileName);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException interruptedException) {
            Log.d(LOG_TAG, "play temporary recording did not sleep right");
        }

        return recordingTaskReturnBundle;
    }

    @Override
    protected void onPostExecute(RecordingTaskReturnBundle recordingTaskReturnBundle) {
        String recordingFileName = recordingTaskReturnBundle.mRecordingFileName;
        RecordingListAdapter recordingListAdapter = recordingTaskReturnBundle.mRecordingListAdapter;
        recordingListAdapter.mIsPlaying.put(recordingFileName, false);
        recordingListAdapter.notifyDataSetChanged();
    }
}
