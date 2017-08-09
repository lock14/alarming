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
        if (id == MATH.ordinal()) {
            return MATH;
        } else if (id == TIC_TAC_TOE.ordinal()) {
            return TIC_TAC_TOE;
        } else if (id == TYPING.ordinal()) {
            return TYPING;
        } else if (id == SWIPE.ordinal()) {
            return SWIPE;
        } else if(id == FALLING_SHAPES.ordinal()) {
            return FALLING_SHAPES;
        } else if (id == NONE.ordinal()) {
            return NONE;
        } else {
            throw new IllegalArgumentException("Illegal Objective ID");
        }
    }
}
