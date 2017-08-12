package cs371m.alarming;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class EditObjective extends AppCompatActivity {
    private int objectiveCode;
    List<View> imageViews;

    // Objective difficulty level options
//    public enum DifficultyLevel {Easy, Medium, Hard}
    private DifficultyLevel objDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_objective);
        imageViews = new ArrayList<>();
        ImageView mathImageView = (ImageView) findViewById(R.id.math_img_view);
        ImageView ticTactToeImageView = (ImageView) findViewById(R.id.tic_tac_toe_img_view);
        ImageView typingImageView = (ImageView) findViewById(R.id.typing_img_view);
        ImageView swipeImageView = (ImageView) findViewById((R.id.swipe_img_view));
        ImageView fallingShapesImageView = (ImageView) findViewById(R.id.falling_shapes_img_view);
        ImageView noneImageView = (ImageView) findViewById(R.id.none_img_view);
        imageViews.add(mathImageView);
        imageViews.add(ticTactToeImageView);
        imageViews.add(typingImageView);
        imageViews.add(swipeImageView);
        imageViews.add(fallingShapesImageView);
        imageViews.add(noneImageView);
        objectiveCode = Objective.NONE.ordinal();
        Intent intent = getIntent();
        if (intent != null) {
            objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
        }
        selectObjective(imageViews.get(objectiveCode));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater(); inflater.inflate(R.menu.menu_difficulty, menu); return true;
    }


    //NEED TO FINISH METHOD
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.easy:
                // Store in persistent data
                return true;
            case R.id.medium:
                // Store in persistent data
                return true;
            case R.id.hard:
                // Store in persistent data
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra(getString(R.string.intent_objective_key), objectiveCode);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    public void selectObjective(View view) {
        int paddingDp = paddingInDP(2);
        view.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        objectiveCode = getObjective(view.getId()).ordinal();
        for (View otherView : imageViews) {
            if (!otherView.equals(view)) {
                paddingDp = paddingInDP(0);
                otherView.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            }
        }
    }

    private Objective getObjective(int viewId) {
        if (viewId == R.id.math_img_view) {
            return Objective.MATH;
        } else if (viewId == R.id.tic_tac_toe_img_view) {
            return Objective.TIC_TAC_TOE;
        } else if (viewId == R.id.typing_img_view) {
            return  Objective.TYPING;
        } else if (viewId == R.id.swipe_img_view) {
            return Objective.SWIPE;
        } else if (viewId == R.id.falling_shapes_img_view) {
            return Objective.FALLING_SHAPES;
        } else if (viewId == R.id.none_img_view) {
            return Objective.NONE;
        } else {
            throw new IllegalStateException("Non Existent View ID: " + viewId);
        }
    }

    public void mathObjective(View view) {
        Intent intent = new Intent(this, MathObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivity(intent);
    }

    public void ticTacToeObjective(View view) {
        Intent intent = new Intent(this, TicTacToeObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivity(intent);
    }

    public void typingObjective(View view) {
        Intent intent = new Intent(this, TypingObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivity(intent);
    }

    public void swipeObjective(View view) {
        Intent intent = new Intent(this, SwipeObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivity(intent);
    }

    public void fallingShapesObjective(View view) {
        Intent intent = new Intent(this, FallingShapesObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivity(intent);
    }

    public int paddingInDP(int paddingDP) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (paddingDP * scale + 0.5f);
    }
}
