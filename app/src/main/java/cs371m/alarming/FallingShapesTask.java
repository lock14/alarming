package cs371m.alarming;

import android.os.AsyncTask;

/**
 * Created by nano on 8/8/17.
 */

public class FallingShapesTask extends AsyncTask<FallingShapesTaskBundle, Void, FallingShapesTaskBundle> {
    @Override
    public FallingShapesTaskBundle doInBackground(FallingShapesTaskBundle ... fallingShapesTaskBundles) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException interruptedException) {
            System.out.println("thread did not sleep in FallingShapesTask");
            }
        return fallingShapesTaskBundles[0];
    }

    @Override
    public void onPostExecute(FallingShapesTaskBundle fallingShapesTaskBundle) {
        fallingShapesTaskBundle.mFallingShapesBoard.invalidate();
        fallingShapesTaskBundle.mFallingsShapes.fallOneLevel();
    }
}
