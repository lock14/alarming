package cs371m.alarming;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by nano on 8/7/17.
 */

public class FallingShapesObjective extends AppCompatActivity {
    FallingShapesBoard mFallingShapesBoard;
    FallingShapes mFallingShapes;
    private int mNFalls;
    private TextView mCountShapesTitle;
    private TextView mShapesCounter;
    private Button mMinus;
    private Button mPlus;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_falling_shapes_objective);
        mFallingShapesBoard = (FallingShapesBoard) findViewById(R.id.falling_shapes_board);
        mFallingShapes = mFallingShapesBoard.getFallingShapes();
        mNFalls = 50;
        setupGUIComponents();
        executeFallingShapes();
    }

    private void executeFallingShapes() {
        FallingShapesTaskBundle fallingShapesTaskBundle = new FallingShapesTaskBundle();
        fallingShapesTaskBundle.mFallingShapesBoard = mFallingShapesBoard;
        fallingShapesTaskBundle.mFallingsShapes = mFallingShapes;
        fallingShapesTaskBundle.mNumberOfFalls = 0;
        fallingShapesTaskBundle.mNumSquares = 0;
        fallingShapesTaskBundle.mNumTriangles = 0;
        fallingShapesTaskBundle.mNumCircles = 0;
        fallingShapesTaskBundle.mNFalls = mNFalls;
        fallingShapesTaskBundle.mFallingShapesObjective = this;
        for (int i = 0; i < mNFalls + 1 + mFallingShapes.mSideLength; ++i) {
            FallingShapesTask fallingShapesTask = new FallingShapesTask();
            fallingShapesTask.execute(fallingShapesTaskBundle);
        }

    }

    private void setupGUIComponents() {
        mCountShapesTitle = (TextView) findViewById(R.id.count_shapes_title);
        mShapesCounter = (TextView) findViewById(R.id.shapes_counter);
        mMinus = (Button) findViewById(R.id.minus);
        mPlus = (Button) findViewById(R.id.plus);
    }
}
