package cs371m.alarming;

/**
 * Created by Ali on 8/12/17.
 */

public enum DifficultyLevel {
    EASY, MEDIUM, HARD;

    public static DifficultyLevel getDiffulty(int objDifficulty) {
        return DifficultyLevel.values()[objDifficulty];
    }
}
