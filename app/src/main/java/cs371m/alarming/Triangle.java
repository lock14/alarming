package cs371m.alarming;

/**
 * Created by nano on 8/6/17.
 */

public class Triangle extends Shape {
    private int mWidth;

    public Triangle(int width) {
        mWidth = width;
    }

    public ShapeType getShapeType() {
        return ShapeType.TRIANGLE;
    }

    public int getImportantLength() {
        return mWidth;
    }

    public void printSelfAsChar() {
        System.out.print("T");
    }

    public Shape cloneSelf() {
        return new Triangle(mWidth);
    }
}
