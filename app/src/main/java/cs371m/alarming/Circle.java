package cs371m.alarming;

/**
 * Created by nano on 8/6/17.
 */

public class Circle extends Shape {
    private int mRadius;

    public Circle(int radius) {
        mRadius = radius;
    }

    public ShapeType getShapeType() {
        return ShapeType.CIRCLE;
    }

    public int getImportantLength() {
        return mRadius;
    }

    public void printSelfAsChar() {
        System.out.print("C");
    }

    public Shape cloneSelf() {
        return new Circle(mRadius);
    }
}
