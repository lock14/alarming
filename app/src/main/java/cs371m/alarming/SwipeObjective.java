package cs371m.alarming;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class SwipeObjective extends Activity {
    private PatternView patternView;
    private List<PatternView.Cell> pattern;
    private Random random;
    private int completionCount;
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
        patternView = (PatternView) findViewById(R.id.swipe_pattern_view);
        random = new Random();
        completionCount = 3;
        handler = new Handler();
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
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> userPattern) {
                if (patternCorrect(userPattern)) {
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
        byte[] bytes = new byte[random.nextInt(5) + 4];
        Set<Integer> used = new HashSet<>();
        int last = (byte) random.nextInt(9);
        used.add(last);
        bytes[0] = (byte) last;
        for (int i = 1; i < bytes.length; i++) {
            int val = random.nextInt(9);
            while (used.contains(val) || deleteriousChoice(last, val)) {
                val = random.nextInt(9);
            }
            bytes[i] = (byte) val;
            used.add(val);
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

}
