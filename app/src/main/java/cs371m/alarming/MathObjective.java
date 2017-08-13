package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MathObjective extends Activity {
    private int completion_count;
    private int operand1;
    private int operand2;
    private MathFunctor operator;
    private Random random;
    private int operatorDiff;
    private int operandDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_objective);
        completion_count = 3;
        random = new Random();
        Intent intent = getIntent();
        boolean demoMode = intent.getBooleanExtra(getString(R.string.objective_demo_mode), false);
        int difficultyCode = intent.getIntExtra(getString(R.string.objective_difficulty), DifficultyLevel.MEDIUM.ordinal());
        if (demoMode) {
            TextView completionTextView = (TextView) findViewById(R.id.math_objective_completions);
            completionTextView.setText(R.string.demo_string);
        }
        EditText answerText = (EditText) findViewById(R.id.answer_text);
        answerText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitAnswer();
                    return true;
                }
                return false;
            }
        });
        setDifficultyParams(difficultyCode);
        chooseRandomProblem();
        setGuiCompoents();
    }

    public void submitAnswer() {
        EditText answerText = (EditText) findViewById(R.id.answer_text);
        String answerString = String.valueOf(answerText.getText());
        if (!TextUtils.isEmpty(answerString)) {
            int answer = Integer.parseInt(answerString);
            String message = null;
            if (answer == operator.doOperation(operand1, operand2)) {
                --completion_count;
                if (completion_count == 0) {
                    Intent result = new Intent();
                    setResult(Activity.RESULT_OK, result);
                    finish();
                } else {
                    message = "Correct! " + completion_count + " more win";
                    if (completion_count > 1) {
                        message += "s";
                    }
                    message += " to disable.";
                    chooseRandomProblem();
                    setGuiCompoents();
                }
            } else {
                message = "Incorrect. Try Again.";
            }
            answerText.setText("");
            if (completion_count != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    private void setGuiCompoents() {
        TextView operand1Text = (TextView) findViewById(R.id.operand1);
        TextView operand2Text = (TextView) findViewById(R.id.operand2);
        TextView operatorText = (TextView) findViewById(R.id.operator);

        operand1Text.setText(Integer.valueOf(operand1).toString());
        operand2Text.setText(Integer.valueOf(operand2).toString());
        operatorText.setText(operator.toString());
    }

    private void chooseRandomProblem() {
        chooseOperation();
        if (operator instanceof DivideFunctor) {
            // want evenly divisible problem;
            // choose operand2 to be between 1 and 10
            operand2 = random.nextInt(9) + 2;

            // next multiply operand2 by random number between 1 and 10.
            // operand 1 will be the result;
            operand1 = operand2 * (random.nextInt(9) + 2);
        } else if (operator instanceof TimesFunctor){
            operand1 = random.nextInt(9) + 2;
            operand2 = random.nextInt(9) + 2;
        } else {
            operand1 = random.nextInt(operandDiff) + 2;
            operand2 = random.nextInt(operandDiff) + 2;
        }
    }

    private void chooseOperation() {
        switch(random.nextInt(operatorDiff)) {
            case 0:
                operator = new PlusFunctor();
                break;
            case 1:
                operator = new MinusFunctor();
                break;
            case 2:
                operator = new TimesFunctor();
                break;
            case 3:
                operator = new DivideFunctor();
                break;
            default:
                // should never get here
                throw new IllegalStateException("Error in choosing math operation");
        }
    }

    //NEED TO FINISH METHOD
    public void setDifficultyParams(int difficultyCode) {
        DifficultyLevel objDifficulty = DifficultyLevel.getDiffulty(difficultyCode);
        switch (objDifficulty) {
            case EASY:
                operatorDiff = 2;
                operandDiff = 20;
                break;
            case MEDIUM:
                operatorDiff = 4;
                operandDiff = 50;
                break;
            case HARD:
                operatorDiff = 4;
                operandDiff = 99;
                break;
            default:
                operatorDiff = 4;
                operandDiff = 99;
                break;
        }
    }

    // if were allowed Java 8 features then this would not be so icky;

    private interface MathFunctor {
        int doOperation (int m, int n);
    }

    private class PlusFunctor implements MathFunctor {
        @Override
        public int doOperation(int m, int n) {
            return m + n;
        }

        @Override
        public String toString() {
            return getString(R.string.plus_symbol);
        }
    }

    private class MinusFunctor implements MathFunctor {
        @Override
        public int doOperation(int m, int n) {
            return m - n;
        }

        @Override
        public String toString() {
            return getString(R.string.minus_symbol);
        }
    }

    private class TimesFunctor implements MathFunctor {
        @Override
        public int doOperation(int m, int n) {
            return m * n;
        }

        @Override
        public String toString() {
            return getString(R.string.times_symbol);
        }
    }

    private class DivideFunctor implements MathFunctor {
        @Override
        public int doOperation(int m, int n) {
            return m / n;
        }

        @Override
        public String toString() {
            return getString(R.string.division_symbol);
        }
    }
}
