package cs371.record_sound_logic;

/**
 * Created by nano on 7/25/17.
 */

import android.content.ContextWrapper;
import android.media.MediaPlayer;
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
       this.mSoundFileDirectory = createSoundFileDirectoryPath(soundFileDirectory);
    }

    public int playCurrentSound() {
        String temporarySoundFileName = mContext.getString(R.string.temporary_sound_file_name);
        File temporarySoundFile = new File(prependDirectoryToFileName(temporarySoundFileName));
        if (temporarySoundFile.exists()) {
            return playSoundByFileName(temporarySoundFileName);
        } else {
            Log.d(LOG_TAG, "there's no temporarySoundFile to play sound from");
        }
        return 0;
    }

    public String getCurrentSoundName() {
        return prependDirectoryToFileName(mContext.getString(R.string.temporary_sound_file_name));
    }

    public String[] getListOfSoundFiles() {
        String[] dummyStrings = new String[10];
        return dummyStrings;
    }

    public int playSoundByFileName(String fileName) {
        return playSoundByFileName(fileName, null);
    }

    public int playSoundByFileName(String fileName, MediaPlayer.OnCompletionListener listener) {
        // play sound
        mPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mPlayer.setDataSource(prependDirectoryToFileName(fileName));
            mPlayer.prepare();
            if (listener != null) {
                mPlayer.setOnCompletionListener(listener);
            }
            duration = mPlayer.getDuration();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            listener.onCompletion(mPlayer);
        }
        return duration;
    }

    public int playSoundFileByNameRaw(String fileName) {
        mPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            duration = mPlayer.getDuration();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        return duration;
    }

    public void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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

    private String createSoundFileDirectoryPath(String soundFileDirectory) {
        return mContext.getFilesDir().getAbsolutePath() + "/" + soundFileDirectory;
    }

}
