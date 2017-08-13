package cs371m.alarming;

import android.os.AsyncTask;

/**
 * Created by nano on 8/8/17.
 */

public class FallingShapesTask extends AsyncTask<FallingShapesTaskBundle, Void, FallingShapesTaskBundle> {
    private int mThreadSleepTime;

    public FallingShapesTask(int threadSleepTime) {
        mThreadSleepTime = threadSleepTime;
    }

    @Override
    public FallingShapesTaskBundle doInBackground(FallingShapesTaskBundle ... fallingShapesTaskBundles) {
        try {
            Thread.sleep(mThreadSleepTime);
        } catch (InterruptedException interruptedException) {
            System.out.println("thread did not sleep in FallingShapesTask");
            }
        return fallingShapesTaskBundles[0];
    }

    @Override
    public void onPostExecute(FallingShapesTaskBundle fallingShapesTaskBundle) {
        if (fallingShapesTaskBundle.mNumberOfFalls < fallingShapesTaskBundle.mNFalls) {
            fallingShapesTaskBundle.mFallingsShapes.fallOneLevel();
            fallingShapesTaskBundle.mNumSquares += fallingShapesTaskBundle.mFallingsShapes.countSquaresAtBottom();
            fallingShapesTaskBundle.mNumTriangles += fallingShapesTaskBundle.mFallingsShapes.countTrianglesAtBottom();
            fallingShapesTaskBundle.mNumCircles += fallingShapesTaskBundle.mFallingsShapes.countCirclesAtBottom();
            fallingShapesTaskBundle.mFallingShapesBoard.invalidate();
            if ((fallingShapesTaskBundle.mNumberOfFalls + 1) == fallingShapesTaskBundle.mNFalls) {
                fallingShapesTaskBundle.mFallingsShapes.clearFirstLeveWithEmptyShapes();
            }
        } else if (fallingShapesTaskBundle.mNumberOfFalls <
                (fallingShapesTaskBundle.mNFalls + fallingShapesTaskBundle.mFallingsShapes.mSideLength)) {
            fallingShapesTaskBundle.mFallingsShapes.cloneOneLevel();
            fallingShapesTaskBundle.mNumSquares += fallingShapesTaskBundle.mFallingsShapes.countSquaresAtBottom();
            fallingShapesTaskBundle.mNumTriangles += fallingShapesTaskBundle.mFallingsShapes.countTrianglesAtBottom();
            fallingShapesTaskBundle.mNumCircles += fallingShapesTaskBundle.mFallingsShapes.countCirclesAtBottom();
            fallingShapesTaskBundle.mFallingShapesBoard.invalidate();
        } else {
            System.out.println("number of squares: " + fallingShapesTaskBundle.mNumSquares);
            System.out.println("number of triangles: " + fallingShapesTaskBundle.mNumTriangles);
            System.out.println("number of circles: " + fallingShapesTaskBundle.mNumCircles);
            fallingShapesTaskBundle.mFallingShapesObjective.mExecutingFallingShapes = false;
        }
        ++fallingShapesTaskBundle.mNumberOfFalls;

    }
}
