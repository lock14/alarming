package cs371.record_sound_logic;

/**
 * Created by nano on 7/25/17.
 */

import android.content.ContextWrapper;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cs371m.alarming.R;

public class RecordLogic {
    MediaRecorder mRecorder;
    String mSoundFileDirectory;
    ContextWrapper mContext;
    private String LOG_TAG = "RecordLogic";

    public RecordLogic(ContextWrapper context, String soundFileDirectory) {
        this.mContext = context;
        this.mSoundFileDirectory = soundFileDirectory;
    }

    public void startRecordingIntoFile(String outputSoundFileName) {
        System.out.println("startRecordingIntoFile: " + outputSoundFileName);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(prependDirectoryToFileName(outputSoundFileName));
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void startRecordingIntoTemporarySoundFile() {
        startRecordingIntoFile(mContext.getString(R.string.temporary_sound_file_name));
    }

    public void saveTemporarySoundFileAs(String newSoundFileName) {
        String temporarySoundFileName = mContext.getString(R.string.temporary_sound_file_name);
        File temporarySoundFile = new File(prependDirectoryToFileName(temporarySoundFileName));
        File newSoundFile = new File(prependDirectoryToFileName(newSoundFileName));

        if (!temporarySoundFile.exists()) {
            Log.d(LOG_TAG, "Cannot save temporary file as " + newSoundFileName + " because there" +
                    "is no temporary file");
        }
            try {
                FileChannel src = new FileInputStream(temporarySoundFile).getChannel();
                FileChannel dest = new FileOutputStream(newSoundFile).getChannel();
                dest.transferFrom(src, 0, src.size());
                Log.d(LOG_TAG, "Bytes transfered from temporarySoundFile to "+ newSoundFileName);
            } catch (IOException ioException) {
                Log.d(LOG_TAG, "Could not transfer temporary sound file into " + newSoundFileName);
            }


    }


    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private String prependDirectoryToFileName(String fileName) {
        return this.mSoundFileDirectory + "/" + fileName;
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
