package cs371m.alarming;

/**
 * Created by nano on 8/6/17.
 */

public class Square extends Shape {
    private int mWidth;

    public Square(int width) {
        mWidth = width;
    }

    public ShapeType getShapeType() {
        return ShapeType.SQUARE;
    }

    public int getImportantLength() {
        return mWidth;
    }

    public void printSelfAsChar() {
        System.out.print("S");
    }

    public Shape cloneSelf() {
        return new Square(mWidth);
    }

}
