package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class SwipeObjective extends Activity {
    private SwipePatternView patternView;
    private List<PatternView.Cell> pattern;
    private Random random;
    private int completionCount;
    private int numOfDots;
    private Handler handler;
    private Runnable startAnimation = new Runnable() {
        @Override
        public void run() {
            patternView.setPattern(PatternView.DisplayMode.Animate, pattern);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_objective);
        patternView = findViewById(R.id.swipe_pattern_view);
        patternView.setHitFactor((float) 0.4);
        random = new Random();
        completionCount = 3;
        handler = new Handler();
        Intent intent = getIntent();
        int difficultyCode = DifficultyLevel.MEDIUM.ordinal();
        if (intent != null) {
            boolean demoMode = intent.getBooleanExtra(getString(R.string.objective_demo_mode), false);
            if (demoMode) {
                TextView demoText = findViewById(R.id.swipe_demo_txt);
                demoText.setVisibility(View.VISIBLE);
            }
            difficultyCode = intent.getIntExtra(getString(R.string.objective_difficulty), difficultyCode);
        }
        setDifficultyParams(difficultyCode);
        pattern = PatternUtils.bytesToPattern(generateRandomPattern());
        Log.d("SwipeObjective", "pattern: " + pattern);
        patternView.setPattern(PatternView.DisplayMode.Animate, pattern);
        patternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                handler.removeCallbacks(startAnimation);
            }

            @Override
            public void onPatternCleared() {
                patternView.setPattern(PatternView.DisplayMode.Animate, pattern);
            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {}

            @Override
            public void onPatternDetected(List<PatternView.Cell> userPattern) {
                if (pattern != userPattern && patternCorrect(userPattern)) {
                    completionCount--;
                    if (completionCount == 0) {
                        Intent result = new Intent();
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    } else {
                        pattern = PatternUtils.bytesToPattern(generateRandomPattern());
                        String message = "Correct! " + completionCount + "more win";
                        if (completionCount > 1) {
                            message += "s";
                        }
                        message += " to disable.";
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        patternView.setPattern(PatternView.DisplayMode.Animate, pattern);
                    }
                } else {
                    patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                    handler.postDelayed(startAnimation, 1000);
                }
            }
        });
    }

    private boolean patternCorrect(List<PatternView.Cell> userPattern) {
        return Arrays.equals(PatternUtils.patternToBytes(pattern),
                PatternUtils.patternToBytes(userPattern));
    }

    private byte[] generateRandomPattern() {
        byte[] bytes = new byte[numOfDots];
        List<Integer> choices = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Integer last = choices.get(random.nextInt(choices.size()));
        choices.remove(last);
        bytes[0] = (byte) last.intValue();
        for (int i = 1; i < bytes.length; i++) {
            Integer val = choices.get(random.nextInt(choices.size()));
            int loopCount = 0;
            while (loopCount < 50 && deleteriousChoice(last, val)) { // at most do 50 iterations
                val = choices.get(random.nextInt(choices.size()));
                ++loopCount;
            }
            choices.remove(val);
            bytes[i] = (byte) val.intValue();
            last = val;
        }
        return bytes;
    }

    private boolean deleteriousChoice(int last, int val) {
        return (val == 0 && last == 2) || (val == 2 && last == 0)
                || (val == 3 && last == 5) || (val == 5 && last == 3)
                || (val == 6 && last == 8) || (val == 8 && last == 6)
                || (val == 0 && last == 6) || (val == 6 && last == 0)
                || (val == 1 && last == 7) || (val == 7 && last == 1)
                || (val == 2 && last == 8) || (val == 8 && last == 2)
                || (val == 0 && last == 8) || (val == 8 && last == 0)
                || (val == 2 && last == 6) || (val == 6 && last == 2);
    }

    public void setDifficultyParams(int diffultyCode) {
        DifficultyLevel objDifficulty = DifficultyLevel.getDiffulty(diffultyCode);
        numOfDots = random.nextInt(2);
        switch (objDifficulty) {
            case EASY:
                numOfDots += 4;
                break;
            case MEDIUM:
                numOfDots += 6;
                break;
            case HARD:
                numOfDots += 8;
                break;
            default:
                numOfDots += 4;
                break;
        }
    }
}
