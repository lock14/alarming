package cs371m.alarming;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

public class TypingObjective extends Activity {
    private static final String TAG = "TypingObjective";
    private final static String FILE_NAME = "pride.txt";
    private final static int CHAR_LIMIT = 175;

    EditText userText;
    TextView compText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing_objective);
        Intent intent = getIntent();
        boolean demoMode = intent.getBooleanExtra(getString(R.string.objective_demo_mode), false);
        userText = (EditText) findViewById(R.id.typing_objective_user_text);
        compText = (TextView) findViewById(R.id.typing_objective_comp_text);
        String text = "ERROR";
        try {
            text = loadText();
        } catch(IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not find file " + FILE_NAME);
        }
        compText.setText(text);

        if (demoMode) {
            TextView completionTextView = (TextView) findViewById(R.id.typing_objective_completions);
            completionTextView.setText(R.string.demo_string);
        }
        userText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        userText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        userText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitAnswer();
                    return true;
                }
                return false;
            }
        });
    }

    // Retrieves the block of text for the user to copy
    private String loadText() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.pride);
        int charCount = is.available();
        Random rn = new Random();
        int fileIndex = rn.nextInt(charCount - CHAR_LIMIT + 1);
        is.skip(fileIndex);
        Scanner scanner = new Scanner(is, "UTF_8");
        StringBuilder result = new StringBuilder();
        Boolean isSentence = false;
        Boolean loop = true;
        while (loop) {
            String temp = scanner.next();
            if (isSentence) {
                if (result.length() + temp.length() < CHAR_LIMIT) {
                    result.append(temp);
                    result.append(" ");
                } else {
                    result.deleteCharAt(result.length()-1);
                    loop = false;
                }
            } else {
                if (temp.contains(".") || temp.contains("?") || temp.contains("!"))
                    isSentence = true;
            }
        }
        return result.toString();
    }

    public void submitAnswer() {
        String comp = String.valueOf(userText.getText());
        String user =String.valueOf(compText.getText());
        if (comp.equals(user)) {
            Intent result = new Intent();
            setResult(Activity.RESULT_OK, result);
            finish();
        } else {
            String message = "Incorrect Submission";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


}
