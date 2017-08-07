package cs371m.alarming;

/**
 * Created by nano on 8/6/17.
 */

public class EmptyShape extends Shape {
    public ShapeType getShapeType() {
        return ShapeType.EMPTY_SHAPE;
    }
    public int getImportantLength() {
        return 0;
    }
    public void printSelfAsChar() {
        System.out.print("*");
    }

    public Shape cloneSelf() {
        return new EmptyShape();
    }
}
