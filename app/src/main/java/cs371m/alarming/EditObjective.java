package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_objective);
        imageViews = new ArrayList<>();
        ImageView mathImageView = (ImageView) findViewById(R.id.math_img_view);
        ImageView ticTactToeImageView = (ImageView) findViewById(R.id.tic_tac_toe_img_view);
        imageViews.add(mathImageView);
        imageViews.add(ticTactToeImageView);
        objectiveCode = 0;
        Intent intent = getIntent();
        if (intent != null) {
            objectiveCode = intent.getIntExtra(getString(R.string.intent_objective_key), 0);
        }
        selectObjective(imageViews.get(objectiveCode));
    }

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

    }

    public void swipeObjective(View view) {

    }

    public void countingObjective(View view) {

    }

    public int paddingInDP(int paddingDP) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (paddingDP * scale + 0.5f);
    }
}
