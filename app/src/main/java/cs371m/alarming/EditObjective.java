package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class EditObjective extends AppCompatActivity {
    private int objectiveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_objective);
        objectiveCode = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Intent result = new Intent();
                result.putExtra(getString(R.string.intent_objective_key), objectiveCode);
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            objectiveCode = requestCode;
            if(objectiveCode == Objective.MATH.ordinal()) {
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
                    gd.setCornerRadius(5);
                    gd.setStroke(1, 0xFF000000);
                    Button mathButton = (Button) findViewById(R.id.math_objective_btn);
                    mathButton.setBackgroundDrawable(gd);
            }
        }
    }

    public void mathObjective(View view) {
        Intent intent = new Intent(this, MathObjective.class);
        intent.putExtra(getString(R.string.objective_demo_mode), true);
        startActivityForResult(intent, Objective.MATH.ordinal());
    }

    public void ticTacToeObjective(View view) {

    }

    public void typingObjective(View view) {

    }

    public void swipeObjective(View view) {

    }

    public void countingObjective(View view) {

    }
}
