package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestObjective extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_objective);
    }

    public void completeObjective(View view) {
        Intent result = new Intent();
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
