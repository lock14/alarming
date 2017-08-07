package cs371m.alarming;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import me.zhanghai.android.patternlock.PatternView;

/**
 * Created by Brian on 8/6/2017.
 */

public class SwipePatternView extends PatternView {
    private static final String TAG = "SwipePatternView";
    private OnPatternListener patternListener;
    private Handler handler;
    private ArrayList<Cell> pattern;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (patternListener != null) {
                patternListener.onPatternCleared();
            }
        }
    };

    public SwipePatternView(Context context) {
        this(context, null);
    }

    public SwipePatternView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.patternViewStyle);
    }

    public SwipePatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler();
        Class<?> clazz = getClass().getSuperclass();
        try {
            Field patternField = clazz.getDeclaredField("mPattern");
            patternField.setAccessible(true);

            pattern = (ArrayList<Cell>) patternField.get(this);
            Log.d(TAG, "successfully retrieved mPattern field");
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "Could not access mPattern field");
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Could not access mPattern field");
        } catch (SecurityException e) {
            Log.w(TAG, "Could not access mPattern field");
        }
    }

    @Override
    public void setOnPatternListener(OnPatternListener listener) {
        super.setOnPatternListener(listener);
        patternListener = listener;
    }

    public void setHitFactor(float hitFactor) {
        Class<?> clazz = getClass().getSuperclass();
        try {
            Field hitFactorField = clazz.getDeclaredField("mHitFactor");
            hitFactorField.setAccessible(true);
            hitFactorField.setFloat(this, hitFactor);
            Log.d(TAG, "updated mHitFactor to 0.4");
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "Could not update mHitFactor. using default value.");
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Could not update mHitFactor. using default value.");
        } catch (SecurityException e) {
            Log.w(TAG, "Could not update mHitFactor. using default value.");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handler.removeCallbacks(runnable);
        boolean return_val = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && pattern != null && pattern.isEmpty()) {
            handler.postDelayed(runnable, 1000);
        }
        return return_val;
    }
}
