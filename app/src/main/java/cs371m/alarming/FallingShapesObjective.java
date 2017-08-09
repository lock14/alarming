package cs371m.alarming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nano on 8/7/17.
 */

public class FallingShapesObjective extends AppCompatActivity {
    FallingShapesBoard mFallingShapesBoard;
    FallingShapes mFallingShapes;
    FallingShapesTaskBundle mFallingShapesTaskBundle;
    FallingShapesObjective mFallingShapesObjective;
    Shape mShapeToCount;
    private int mNFalls;
    private int mNumberOfWins;
    private boolean mDemo;
    public boolean mExecutingFallingShapes;
    private TextView mCountShapesTitle;
    private TextView mShapesCounter;
    private Button mMinus;
    private Button mPlus;
    private Button mStart;
    private Button mSubmit;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_falling_shapes_objective);
        mFallingShapesBoard = (FallingShapesBoard) findViewById(R.id.falling_shapes_board);
        mFallingShapes = mFallingShapesBoard.getFallingShapes();
        mNFalls = 50;
        mNumberOfWins = 0;
        Intent intent = getIntent();
        mDemo = intent.getBooleanExtra(getString(R.string.objective_demo_mode), false);
        mFallingShapesObjective = this;
        setupGUIComponents();
        addOnClickListeners();
//        executeFallingShapes();
    }

    private void executeFallingShapes() {
        mExecutingFallingShapes = true;
        mShapeToCount = mFallingShapes.generateRandomShape();
        String shapeString = "";
        if (mShapeToCount.getShapeType() == ShapeType.SQUARE) {
            shapeString = "square";
        } else if (mShapeToCount.getShapeType() == ShapeType.TRIANGLE) {
            shapeString = "triangle";
        } else {
            shapeString = "circle";
        }
        mCountShapesTitle.setText("count the " + shapeString +"s!!!");
        mFallingShapesTaskBundle = new FallingShapesTaskBundle();
        mFallingShapesTaskBundle.mFallingShapesBoard = mFallingShapesBoard;
        mFallingShapesTaskBundle.mFallingsShapes = mFallingShapes;
        mFallingShapesTaskBundle.mNumberOfFalls = 0;
        mFallingShapesTaskBundle.mNumSquares = 0;
        mFallingShapesTaskBundle.mNumTriangles = 0;
        mFallingShapesTaskBundle.mNumCircles = 0;
        mFallingShapesTaskBundle.mNFalls = mNFalls;
        mFallingShapesTaskBundle.mFallingShapesObjective = this;
        for (int i = 0; i < mNFalls + 1 + mFallingShapes.mSideLength; ++i) {
            FallingShapesTask fallingShapesTask = new FallingShapesTask();
            fallingShapesTask.execute(mFallingShapesTaskBundle);
        }
    }

    private void setupGUIComponents() {
        mCountShapesTitle = (TextView) findViewById(R.id.count_shapes_title);
        mShapesCounter = (TextView) findViewById(R.id.shapes_counter);
        mMinus = (Button) findViewById(R.id.minus);
        mPlus = (Button) findViewById(R.id.plus);
        mStart = (Button) findViewById(R.id.start_falling_shapes);
        mSubmit = (Button) findViewById(R.id.submit_falling_shapes_count);
    }

    private void addOnClickListeners() {
        mMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringCounter = String.valueOf(mShapesCounter.getText());
                    int intCounter = Integer.valueOf(stringCounter);
                    if (intCounter > 0){
                        --intCounter;
                        stringCounter = String.valueOf(intCounter);
                        mShapesCounter.setText(stringCounter);
                    }
        }
        });

        mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringCounter = String.valueOf(mShapesCounter.getText());
                int intCounter = Integer.valueOf(stringCounter);
                ++intCounter;
                stringCounter = String.valueOf(intCounter);
                mShapesCounter.setText(stringCounter);
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExecutingFallingShapes) {
                    executeFallingShapes();
                } else {
                    Toast.makeText(mFallingShapesObjective, "Wait for the shapes to stop falling", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mExecutingFallingShapes && mFallingShapesTaskBundle != null && mShapeToCount != null) {
                    String stringCounter = String.valueOf(mShapesCounter.getText());
                    int intCounter = Integer.valueOf(stringCounter);
                    int numShapes = -1;
                    if (mShapeToCount.getShapeType() == ShapeType.SQUARE) {
                        numShapes = mFallingShapesTaskBundle.mNumCircles;
                    } else if (mShapeToCount.getShapeType() == ShapeType.TRIANGLE) {
                        numShapes = mFallingShapesTaskBundle.mNumTriangles;
                    } else {
                        numShapes = mFallingShapesTaskBundle.mNumCircles;
                    }

                    if (intCounter == numShapes) {
                        Toast.makeText(mFallingShapesObjective, "Correct", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mFallingShapesObjective, "Incorrect", Toast.LENGTH_SHORT).show();
                    }

                } else if (!mExecutingFallingShapes){
                    Toast.makeText(mFallingShapesObjective, "Wait for the shapes to stop falling.", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(mFallingShapesObjective, "Must start the game at least once.", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
