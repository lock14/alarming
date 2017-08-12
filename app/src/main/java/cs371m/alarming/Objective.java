package cs371m.alarming;

/**
 * Created by Brian on 7/30/2017.
 */

public enum Objective {
    MATH,
    TIC_TAC_TOE,
    SWIPE,
    TYPING,
    FALLING_SHAPES,
    NONE;

    static Objective getObjective(int id) {
        return Objective.values()[id];
    }
}
