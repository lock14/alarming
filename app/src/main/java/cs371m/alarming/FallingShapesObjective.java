package cs371m.alarming;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nano on 8/7/17.
 */

public class FallingShapesObjective extends AppCompatActivity {
    FallingShapesBoard mFallingShapesBoard;
    FallingShapes mFallingShapes;
    Canvas mCanvas;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_falling_shapes_objective);
        mFallingShapesBoard = (FallingShapesBoard) findViewById(R.id.falling_shapes_board);
        mFallingShapes = mFallingShapesBoard.getFallingShapes();
        mFallingShapes.clearFirstLevelWithNewShapes();
        FallingShapesTaskBundle fallingShapesTaskBundle = new FallingShapesTaskBundle();
        fallingShapesTaskBundle.mFallingShapesBoard = mFallingShapesBoard;
        fallingShapesTaskBundle.mFallingsShapes = mFallingShapes;

        for (int i = 0; i < 100; ++i) {
            FallingShapesTask fallingShapesTask = new FallingShapesTask();
            fallingShapesTask.execute(fallingShapesTaskBundle);
        }
    }
}
