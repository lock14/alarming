package cs371.record_sound_logic;

/**
 * Created by nano on 7/26/17.
 */

import android.content.ContextWrapper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cs371m.alarming.R;

public class SoundFileManager {
    String mSoundFileDirectory;
    String mAlarmFileDirectory;
    static final String LOG_TAG = "SoundFileManager";
    ContextWrapper mContext;

    public SoundFileManager(ContextWrapper context, String soundFileDirectory) {
        this.mContext = context;
        mSoundFileDirectory = setupSoundFileDirectoryEnviroment(soundFileDirectory);
        mAlarmFileDirectory = setupAlarmFileDirectory();
    }

    public void addSoundFileByName(String soundFileName) {
        // assuming soundFileName has an absolute path
        File originalSoundFile = new File(soundFileName);
        File newSoundFile = new File(prependDirectoryToFileName(soundFileName));

        try {
            FileChannel src = new FileInputStream(originalSoundFile).getChannel();
            FileChannel dest = new FileOutputStream(newSoundFile).getChannel();
            dest.transferFrom(src, 0, src.size());
            Log.d(LOG_TAG, "Added new sound file: " + soundFileName);
        } catch (IOException ioException) {
            Log.d(LOG_TAG, "could not add new sound file: " + soundFileName);
        }
    }

    public void removeSoundFileByName(String soundFileName) {
        File soundFile = new File(prependDirectoryToFileName(soundFileName));
        if (soundFile.exists()) {
            soundFile.delete();
            Log.d(LOG_TAG, "sound file " + soundFileName + " deleted");
        } else {
            Log.d(LOG_TAG, "sound file " + soundFileName + "does not exist");
        }
    }

    public void removeAllSoundFiles() {
        File soundFileDirectory = new File(mSoundFileDirectory);
        if (soundFileDirectory.exists() && soundFileDirectory.isDirectory()) {
            File[] soundFiles = soundFileDirectory.listFiles();
            for (int i = 0; i < soundFiles.length; ++i) {
                soundFiles[i].delete();
            }
            Log.d(LOG_TAG, "all sound files deleted in directory " + mSoundFileDirectory);
        } else {
            Log.d(LOG_TAG, "sound directory doesn't exist or we have the wrong directory name");
        }
    }

    public void removeSoundFilesInAlarmDirectory() {
        File soundFileDirectory = new File(mAlarmFileDirectory);
        if (soundFileDirectory.exists() && soundFileDirectory.isDirectory()) {
            File[] soundFiles = soundFileDirectory.listFiles();
            for (int i = 0; i < soundFiles.length; ++i) {
                soundFiles[i].delete();
            }
            Log.d(LOG_TAG, "all sound files deleted in directory " + mSoundFileDirectory);
        } else {
            Log.d(LOG_TAG, "sound directory doesn't exist or we have the wrong directory name");
        }
    }

    private String setupSoundFileDirectoryEnviroment(String soundFileDirectory) {
        String newSoundFileDirectory = mContext.getFilesDir().getAbsolutePath() + "/" + soundFileDirectory;
        File soundDirectoryFile = new File(newSoundFileDirectory);

        if (!soundDirectoryFile.exists()) {
            try {
                soundDirectoryFile.mkdir();
                Log.d(LOG_TAG, "directory: " + newSoundFileDirectory + " created");
            } catch (SecurityException securityException) {
                Log.d(LOG_TAG, "soundFileDirectory was not created correctly");
            }
        } else {
            Log.d(LOG_TAG, newSoundFileDirectory + " already exists");
        }
        return newSoundFileDirectory;
    }

    private String setupAlarmFileDirectory() {
        String alarmFileDirectoryName = mContext.getFilesDir().getAbsolutePath() + "/" +
                mContext.getString(R.string.alarm_file_directory);
        File alarmFileDirectoryFile = new File(alarmFileDirectoryName);
        if (!alarmFileDirectoryFile.exists()) {
            alarmFileDirectoryFile.mkdir();
            Log.d(LOG_TAG, "created alarm file directory " + alarmFileDirectoryName);
        } else {
            Log.d(LOG_TAG, alarmFileDirectoryName + " already exists");
        }
        return alarmFileDirectoryName;
    }

    public String saveTemporarySoundFileToAlarm() {
        String temporarySoundFileName = prependDirectoryToFileName(mContext.getString(R.string.temporary_sound_file_name));
        File temporarySoundFile = new File(temporarySoundFileName);
        File newAlarmSoundFile = new File(mAlarmFileDirectory + "/" + mContext.getString(R.string.alarm_sound_file));

        if (!temporarySoundFile.exists()) {
            Log.d(LOG_TAG, "Cannot save temporary file as " + newAlarmSoundFile + " because there" +
                    "is no temporary file");
        } else {
            try {
                FileChannel src = new FileInputStream(temporarySoundFile).getChannel();
                FileChannel dest = new FileOutputStream(newAlarmSoundFile).getChannel();
                dest.transferFrom(src, 0, src.size());
                Log.d(LOG_TAG, "Bytes transfered from temporarySoundFile to " + newAlarmSoundFile);
                return newAlarmSoundFile.getName();
            } catch (IOException ioException) {
                Log.d(LOG_TAG, "Could not transfer temporary sound file into " + newAlarmSoundFile);
            }
        }
        return null;
    }

    public String saveTemporarySoundFileToAlarmAs(String alarmSoundFileName) {
        String temporarySoundFileName = prependDirectoryToFileName(mContext.getString(R.string.temporary_sound_file_name));
        File temporarySoundFile = new File(temporarySoundFileName);
        File newAlarmSoundFile = new File(mAlarmFileDirectory + "/" + alarmSoundFileName);

        if (!temporarySoundFile.exists()) {
            Log.d(LOG_TAG, "Cannot save temporary file as " + newAlarmSoundFile + " because there" +
                    "is no temporary file");
        } else {
            try {
                FileChannel src = new FileInputStream(temporarySoundFile).getChannel();
                FileChannel dest = new FileOutputStream(newAlarmSoundFile).getChannel();
                dest.transferFrom(src, 0, src.size());
                Log.d(LOG_TAG, "Bytes transfered from temporarySoundFile to " + newAlarmSoundFile);
                return newAlarmSoundFile.getName();
            } catch (IOException ioException) {
                Log.d(LOG_TAG, "Could not transfer temporary sound file into " + newAlarmSoundFile);
            }
        }
        return null;
    }

    public String saveSoundFileToAlarmFileDirectory(String soundFileName) {
        File soundFile = new File(prependDirectoryToFileName(soundFileName));
        File alarmSoundFile = new File(mAlarmFileDirectory + "/" + soundFileName);
        if (!soundFile.exists()) {
            Log.d(LOG_TAG, "cannot save sound file " + soundFileName + " because it does not exist");
        } else {
            try {
                FileChannel src = new FileInputStream(soundFile).getChannel();
                FileChannel dest = new FileOutputStream(alarmSoundFile).getChannel();
                dest.transferFrom(src, 0, src.size());
                Log.d(LOG_TAG, "Bytes transfered from " + soundFileName + " into alarm file directory");
                return alarmSoundFile.getName();
            } catch (IOException ioException) {
                Log.d(LOG_TAG, "could not copy " + soundFileName +" into soundFile directory");
            }
        }
        return null;
    }

    private String prependDirectoryToFileName(String fileName) {
        return this.mSoundFileDirectory + "/" + fileName;
    }

    public String getSoundFileDirectory() {
        return mSoundFileDirectory;
    }

    public String[] getSoundFileList() {
        File soundFileDirectory = new File(mSoundFileDirectory);
        String[] list = null;
        if (soundFileDirectory.exists() && soundFileDirectory.isDirectory()) {
            list = soundFileDirectory.list();
            Log.d(LOG_TAG, "Returning list of file names");
        } else {
            Log.d(LOG_TAG, "Not returning list of file names.");
        }
        list = removeTemporarySoundFileName(list);
        return list;
    }

    public File[] getSoundFiles() {
        File soundFileDirectory = new File(mSoundFileDirectory);
        File[] soundFiles = null;
        if (soundFileDirectory.exists() && soundFileDirectory.isDirectory()) {
            soundFiles = soundFileDirectory.listFiles();
            Log.d(LOG_TAG, "Returning list of files.");
        } else {
            Log.d(LOG_TAG, "Not returning list of files.");
        }
        return soundFiles;
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

    private String[] removeTemporarySoundFileName(String[] soundFileNames) {
        String[] newSoundFileNames = soundFileNames;
        if (soundFileNames.length > 1) {
            newSoundFileNames = new String[soundFileNames.length - 1];
            int j = 0;
            for (int i = 0; i < soundFileNames.length; ++i) {
                if (!soundFileNames[i].equals(mContext.getString(R.string.temporary_sound_file_name))) {
                    newSoundFileNames[j] = soundFileNames[i];
                    ++j;
                }
            }
            soundFileNames = newSoundFileNames;
        }
        return newSoundFileNames;
    }

    public String getAlarmRecordingName() {
        File alarmFileDirectory = new File(mAlarmFileDirectory);

        if (alarmFileDirectory.isDirectory() && alarmFileDirectory.listFiles().length == 1) {
//            System.out.println("returning file " + alarmFileDirectory.listFiles()[0].getName());
            return alarmFileDirectory.listFiles()[0].getName();
        }
        return null;
    }

}
