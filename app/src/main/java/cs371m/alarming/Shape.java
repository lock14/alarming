package cs371m.alarming;

/**
 * Created by nano on 8/6/17.
 */

public abstract class Shape {
    abstract ShapeType getShapeType();
    abstract int getImportantLength();
    abstract void printSelfAsChar();
    abstract Shape cloneSelf();
}
