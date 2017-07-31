package cs371.record_sound_logic;

/**
 * Created by nano on 7/25/17.
 */

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import cs371m.alarming.R;

public class SoundLogic {
    MediaPlayer mPlayer;
    ContextWrapper mContext;
    String mSoundFileDirectory;
    final static String LOG_TAG = "SoundLogic";

    public SoundLogic(ContextWrapper context, String soundFileDirectory) {
        this.mContext = context;
       this.mSoundFileDirectory = soundFileDirectory;
    }

    public void playCurrentSound() {
        String temporarySoundFileName = mContext.getString(R.string.temporary_sound_file_name);
        File temporarySoundFile = new File(prependDirectoryToFileName(temporarySoundFileName));
        if (temporarySoundFile.exists()) {
            playSoundByFileName(temporarySoundFileName);
        } else {
            Log.d(LOG_TAG, "there's no temporarySoundFile to play sound from");
        }
    }

    public String[] getListOfSoundFiles() {
        String[] dummyStrings = new String[10];
        return dummyStrings;
    }


    public void playSoundByFileName(String fileName) {
        // play sound
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(prependDirectoryToFileName(fileName));
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private String prependDirectoryToFileName(String fileName) {
        return this.mSoundFileDirectory + "/" + fileName;
    }

}
