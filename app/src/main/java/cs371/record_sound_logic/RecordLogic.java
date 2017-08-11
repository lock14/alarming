package cs371.record_sound_logic;

/**
 * Created by nano on 7/25/17.
 */

import android.content.ContextWrapper;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

import cs371m.alarming.R;

public class RecordLogic {
    MediaRecorder mRecorder;
    String mSoundFileDirectory;
    ContextWrapper mContext;

    public RecordLogic(ContextWrapper context, String soundFileDirectory) {
        this.mContext = context;
        this.mSoundFileDirectory = createSoundFileDirectoryPath(soundFileDirectory);
    }

    public void startRecordingIntoFile(String outputSoundFileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(prependDirectoryToFileName(outputSoundFileName));
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException ioException) {
            String LOG_TAG = "RecordLogic";
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void startRecordingIntoTemporarySoundFile() {
        startRecordingIntoFile(mContext.getString(R.string.temporary_sound_file_name));
    }



    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    private String prependDirectoryToFileName(String fileName) {
        return this.mSoundFileDirectory + "/" + fileName;
    }

    private String createSoundFileDirectoryPath(String soundFileDirectory) {
        return mContext.getFilesDir().getAbsolutePath() + "/" + soundFileDirectory;
    }

    public String getSoundFileDirectory() {
        return mSoundFileDirectory;
    }

    public void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
