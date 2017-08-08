package cs371m.alarming;

import java.util.Random;

/**
 * Created by nano on 8/6/17.
 */

public class FallingShapes {
    Shape[][] mShapesGrid;
    int mSideLength;
    int mShapeSize;
    Random mRand;

    FallingShapes(int sideLength, int shapeSize) {
        mSideLength = sideLength;
        mShapeSize = shapeSize;
        mShapesGrid = new Shape[mSideLength][mSideLength];
        mRand = new Random();
        //populateShapesGrid();
        populateShapesGridWithEmptyShapes();
    }

    public Shape[][] getShapesGrid() {
        return mShapesGrid;
    }

    public void printShapesGrid() {
        for (int i = 0; i < mSideLength; ++i) {
            for (int j = 0; j < mSideLength; ++j) {
                mShapesGrid[i][j].printSelfAsChar();
            }
            System.out.println();
        }
    }


    public void fallNTimes(int n) {
        clearFirstLevelWithNewShapes();
        try {
            Thread.sleep(250);
        } catch (InterruptedException interruptedException) {
            System.out.println("could not sleep in fallNTimes in class FallingShapes");
        }
        for (int i = 0; i < n; ++i) {
            fallOneLevel();
            try {
                Thread.sleep(250);
            } catch (InterruptedException interruptedException) {
                System.out.println("could not sleep in fallNTimes in class FallingShapes");
            }
        }

    }

    public void fallOneLevel() {
        for (int i = (mSideLength - 2); i >= 0; --i) {
            for (int j = (mSideLength - 1); j >= 0; --j) {
                mShapesGrid[i + 1][j] = mShapesGrid[i][j].cloneSelf();
            }
        }
        clearFirstLevelWithNewShapes();
    }

    private void clearFirstLeveWithEmptyShapes() {
        for (int i = 0; i < mSideLength; ++i) {
            mShapesGrid[0][i] = new EmptyShape();
        }
    }

    public void clearFirstLevelWithNewShapes() {
        for (int i = 0; i < mSideLength; ++i) {
            if (drawShape()) {
                mShapesGrid[0][i] = generateRandomShape();
            } else {
                mShapesGrid[0][i] = new EmptyShape();
            }
        }
    }

    private void populateShapesGridWithEmptyShapes() {
        for (int i = 0; i < mSideLength; ++i) {
            for (int j = 0; j < mSideLength; ++j) {
                mShapesGrid[i][j] = new EmptyShape();
            }
        }
    }

    private void populateShapesGrid() {
        for (int i = 0; i < mSideLength; ++i) {
            for (int j = 0; j < mSideLength; ++j) {
                if (drawShape()) {
                    mShapesGrid[i][j] = generateRandomShape();
                } else {
                    mShapesGrid[i][j] = new EmptyShape();
                }
            }
        }
    }

    private Shape generateRandomShape() {
        int shapeInt = mRand.nextInt(3);

        if (shapeInt == 0) {
            return new Square(mShapeSize);
        } else if (shapeInt == 1) {
            return new Triangle(mShapeSize);
        }
        return new Circle(mShapeSize);

    }

    private boolean drawShape() {
        int shapeChance = mRand.nextInt(4);
        return (0 == shapeChance);
    }
}
