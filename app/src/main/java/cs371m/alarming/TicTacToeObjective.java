package cs371m.alarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TicTacToeObjective extends Activity {
    private TicTacToeObjective mTicTacToeObjective;
    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private TextView mDemoView;
    private boolean mGameOver = false;
    private boolean mDemoMode;
    private int mNumWins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tic_tac_toe);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mTicTacToeObjective = this;
        mInfoTextView = (TextView) findViewById(R.id.information);
        Button mRetryButton = (Button) findViewById(R.id.retry_tictactoe);
        mRetryButton.setOnClickListener(new RetryButtonClickListener());
        Intent intent = getIntent();
        mDemoMode =  intent.getBooleanExtra(getString(R.string.objective_demo_mode), false);
        int difficultyCode = DifficultyLevel.MEDIUM.ordinal();
        difficultyCode = intent.getIntExtra(getString(R.string.objective_difficulty), difficultyCode);
        mDemoView = (TextView) findViewById(R.id.demo_tictactoe);
        if (mDemoMode) {
            mDemoView.setVisibility(TextView.VISIBLE);
        }
        mNumWins = 0;
        mGame = new TicTacToeGame();
        mGame.setDifficultyLevel(DifficultyLevel.getDiffulty(difficultyCode));
        startNewGame();
    }
    private void startNewGame() {
        mGame.clearBoard();
        for (int i = 0; i < mBoardButtons.length; ++i) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        mGameOver = false;
        mInfoTextView.setText(R.string.tic_tac_toe_go_first_msg);
    }

    private void endGame() {
        for (Button button : mBoardButtons) {
            button.setEnabled(false);
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.xPieceColor));
        else
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.oPieceColor));
    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.computer_turn);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0) {
                    mInfoTextView.setText(R.string.human_turn);
                }
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true;
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver = true;
                    ++mNumWins;
                    if (mNumWins > 2) {
                        Intent result = new Intent();
                        mNumWins = 0;
                        setResult(Activity.RESULT_OK, result);
                        finish();

                    } else {
                        Toast.makeText(mTicTacToeObjective, "Need "+ (3 - mNumWins) +" objective wins to disable.", Toast.LENGTH_SHORT).show();
                        startNewGame();
                    }
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mGameOver = true;
                }
                if (mGameOver) {
                    endGame();
                }
            }
        }
    }

    private class RetryButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            startNewGame();
        }
    }
}
