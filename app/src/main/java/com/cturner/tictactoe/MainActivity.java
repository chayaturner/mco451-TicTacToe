package com.cturner.tictactoe;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

enum WinType {NONE, ROW, COLUMN, DIAGONAL}
enum WinTypeDiagonal {UPPER_LEFT_TO_LOWER_RIGHT, LOWER_LEFT_TO_UPPER_RIGHT}

public class MainActivity extends AppCompatActivity {

    // named constants (finals)
    private final int mEMPTY_SPACE = R.drawable.ic_xo_light, mINVALID_ICON_VALUE_FLAG = -99;
    private int mOLD_ICON_X, mOLD_ICON_O, mOLD_ICON_XO;

    // primitives and Strings
    private boolean mTurnX, mPrefUseAutoSave, mGameOver,
            mPrefComputerOpponent, mPrefComputerStarts;
    private String mLastGameResultsMessage, mLastWinner, mLastTurnResults;
    private int[] mWinningSpaces;
    private WinType mWinType;
    private WinTypeDiagonal mWinTypeDiagonal;
    private int mCurrentPosition, mPriorPosition = mINVALID_ICON_VALUE_FLAG;

    // These values are coded here rather than in strings.xml because they are not used elsewhere
    // If these keys might be read in another Activity then the values should instead be put in xml
    // Keys reference in both Java and XML - values stored in strings.xml
    private String mKEY_USE_AUTO_SAVE,
            mKEY_COMPUTER_OPPONENT, mKEY_COMPUTER_STARTS;

    // Keys referenced only in Java - values stored here
    private final String mKEY_PLAYER = "CURRENT_PLAYER";
    private final String mKEY_BOARD = "BOARD";
    private final String mKEY_TINTS = "TINTS";
    private final String mKEY_GAME_OVER = "GAME_OVER";
    private final String mKEY_LAST_TURN_RESULTS = "LAST_TURN_RESULTS";
    private final String mPREFS = "PREFS";
    private final String mKEY_CURRENT_POSITION = "PRIOR_POSITION";
    private final String mKEY_PRIOR_POSITION = "PRIOR_POSITION";
    private final String mKEY_LAST_RESULT = "LAST_RESULT";
    private final String mKEY_ICON_X = "ICON_X";
    private final String mKEY_ICON_O = "ICON_O";
    private final String mKEY_ICON_XO = "ICON_XO";

    // Reference to our custom Adapter used to create and maintain a board in our GridView here
    private CardViewImageAdapter mAdapter;

    // References to various Views
    private TextView mStatusBar;
    private ImageView mImageX, mImageO;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSbGame;
    private View mSbParentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGUI();

        createUnfilledBoard();

        initializeSnackBar();

        initializePreferences();

        // If we are starting a fresh Activity (meaning, not after rotation), then do initial setup
        if (savedInstanceState == null) {
            setupInitialSession();
        }
        // If we're in the middle of a game then onRestoreInstanceState will restore the App's state

    }

    private void initGUI() {
        initializeStatusItems();
        initializeSwipeRefreshLayout();
    }

    private void initializeStatusItems(){
        mImageX = (ImageView) findViewById(R.id.imageX);
        mImageO = (ImageView) findViewById(R.id.imageO);
        mStatusBar = (TextView)findViewById(R.id.textViewStatusBar);
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareForNewGame();
                startNewOrResumeGameState();
            }
        });
    }

    private void prepareForNewGame(){
        resetBoard();

        resetGameAndTurnStatus();

        resetCurrentAndPriorPositions();

        dismissSnackBarIfShown();

    }

    private void dismissSnackBarIfShown() {
        if(mSbGame.isShown()){
            mSbGame.dismiss();
        }
    }

    private void resetCurrentAndPriorPositions() {
        mPriorPosition = mINVALID_ICON_VALUE_FLAG;
        mCurrentPosition = mINVALID_ICON_VALUE_FLAG;
    }

    private void resetGameAndTurnStatus() {
        mGameOver = false;
        mLastTurnResults = "First Turn of the Game";
        setCurrentPlayerToX(true);
    }

    private void setCurrentPlayerToX(boolean newValueOfX) {
        mTurnX = newValueOfX;

        updateTintOfImagesXO();
        updateStatusBarWithCurrentTurn();
    }

    private void updateStatusBarWithCurrentTurn() {
        String currentPlayer = getCurrentPlayer();
        mStatusBar.setText(getString(R.string.current_turn).concat(currentPlayer));
    }

    private void updateTintOfImagesXO() {
        int colorForLetterX = mTurnX ? R.color.color_yes : R.color.color_no;
        int colorForLetterO = mTurnX ? R.color.color_no : R.color.color_yes;

        mImageX.setColorFilter(ContextCompat.getColor(this, colorForLetterX));
        mImageO.setColorFilter(ContextCompat.getColor(this, colorForLetterO));
    }

    private void resetBoard() {
        clearBoard();
        mAdapter.clearAllImageTints();
    }

    private void clearBoard() {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.setImage(i, mEMPTY_SPACE);
        }
    }

    private void startNewOrResumeGameState(){
        // regardless of how we got here (via listener, MenuItem click, etc), turn off animation
        mSwipeRefreshLayout.setRefreshing(false);

        // If the user chooses to have a computer opponent and that the computer should start (X)
        // and it is currently turn x (always first player)
        if (!mGameOver && mPrefComputerOpponent && mPrefComputerStarts & mTurnX) {
            doComputerTurnCycle();
        }

    }

    private void doComputerTurnCycle() {
        //TODO
    }

    private void createUnfilledBoard() {
        final int TOTAL_SPACES = 9;

        mWinningSpaces = new int[(int)Math.sqrt(TOTAL_SPACES)];

        //Create the adapter for later use in the RecyclerView
        mAdapter = new CardViewImageAdapter(
                getApplicationContext(),
                TOTAL_SPACES,
                R.drawable.ic_xo_light);

        // set the listener which will listen to the clicks in the RecyclerView
        mAdapter.setOnItemClickListener(listener);

        // get a reference to the RecyclerView
        RecyclerView board = (RecyclerView) findViewById(R.id.grid_board);
        assert board != null;

        // get a reference to a new LayoutManager for the RecyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setAutoMeasureEnabled(true);

        // set the adapter as the data source (model) for the RecyclerView
        board.setHasFixedSize(true);
        board.setLayoutManager(layoutManager);
        board.setAdapter(mAdapter);
    }

    private void initializePreferences(){

    }

    private void initializeSnackBar(){

    }

    private void setupInitialSession(){

    }

    private String getCurrentPlayer(){
        return mTurnX ? getString(R.string.x) : getString(R.string.o);
    }

    private final CardViewImageAdapter.OIClickListener
            listener = new CardViewImageAdapter.OIClickListener() {
        public void onItemClick(int position, View view) {
            // if the game is already over then there is nothing more to do here
            if (mGameOver) {
                showGameOverSB(true);
            }

            // If the current space is empty and, therefore available and thus a valid space
            else if (isSpaceEmpty(position)) {
                processClickOnValidSpace(position);
            } else {
                showInvalidSpaceSB();
            }
        }
    };

    private boolean isSpaceEmpty(int position) {
        return mAdapter.getItemId(position) == mEMPTY_SPACE;
    }

    private void processClickOnValidSpace(int position) {
        doHumanTurnCycle(position);

        if (mPrefComputerOpponent && !mGameOver)
        {
            if (mPrefComputerStarts && mTurnX || !mPrefComputerStarts && !mTurnX) {
                doComputerTurnCycle();
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        R.string.info_computer_goes_next,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void doHumanTurnCycle(int position) {
        // process this turn/move
        doPlayerTurn(position);

        showTurnStatus(position);

        // check for win and/or full board
        doPostPlayerTurn();
    }

    private void showTurnStatus(final int position) {
        String strPosition = getOneBasedRowAndColumnAt(position);
        showRowAndColumnAt(position, strPosition);
    }

    @NonNull
    private String getOneBasedRowAndColumnAt(int position) {
        int totalSpaces = mAdapter.getItemCount();
        int rowsAndColumns = (int) Math.sqrt(totalSpaces);

        int oneBasePosition = position + 1;
        int row = (oneBasePosition / rowsAndColumns) + 1;
        int col = oneBasePosition % rowsAndColumns;

        if (col == 0) {
            row--;
            col = rowsAndColumns;
        }

        return getString(R.string.row_colon) + row + ", " + getString(
                R.string.column_colon) + col;
    }

    private void showRowAndColumnAt(final int position, String strPosition) {
        String msg = getCurrentPlayer() + getString(R.string.chose) + strPosition + '.';

        // Create SnackBar with status message of this past turn
        mSbGame = Snackbar.make(mSbParentView, mLastTurnResults + "\n" + msg, Snackbar.LENGTH_LONG);
        mLastTurnResults = msg;

        // Allow and setup undo
        mSbGame.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoLastMove(position);
            }
        });

        // Show SnackBar
        mSbGame.show();
    }

    private void undoLastMove(int position) {
        if (!mPrefComputerOpponent) {
            mAdapter.setImage(position, R.drawable.ic_xo_light);
            setCurrentPlayerToX(!mTurnX);
        } else {
            if (mCurrentPosition != mINVALID_ICON_VALUE_FLAG && mPriorPosition != mINVALID_ICON_VALUE_FLAG) {
                mAdapter.setImage(mCurrentPosition, R.drawable.ic_xo_light);
                mAdapter.setImage(mPriorPosition, R.drawable.ic_xo_light);
            } else if (mPriorPosition == mINVALID_ICON_VALUE_FLAG) {
                mSbGame.setText(R.string.error_cannot_undo_this_move).setDuration(Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void showInvalidSpaceSB() {
        Snackbar.make(mSbParentView,
                getString(R.string.error_space_already_taken),
                Snackbar.LENGTH_SHORT).show();
    }

    private void showGameOverSB(boolean gameAlreadyOver) {
        StringBuilder sbText = generateGameOverMessage(gameAlreadyOver);
        mSbGame = Snackbar.make(mSbParentView, sbText, Snackbar.LENGTH_INDEFINITE);
        mSbGame.setAction(R.string.action_newGame, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGameIncludingSRAnimation();
            }
        })
                .show();
    }

    @NonNull
    private StringBuilder generateGameOverMessage(boolean gameAlreadyOver) {
        StringBuilder sbText = new StringBuilder(getString(R.string.info_game_over));
        sbText.append(' ');

        if (gameAlreadyOver) {
            sbText.append(getString(R.string.info_game_already_over));
        } else {
            sbText.append(mLastGameResultsMessage);
        }
        return sbText;
    }

    private void startNewGameIncludingSRAnimation() {
        // start animation
        mSwipeRefreshLayout.setRefreshing(true);
        prepareForNewGame();
        startNewOrResumeGameState();
    }

    /**
     * Switches changes the board's space icon in that space to match the X or O
     * and updates the current and recent positions to match the just-chosen space
     *
     * @param position The position on the board to change to X or O
     */
    private void doPlayerTurn(final int position) {
        // change the icon at that position from empty to either X or O as appropriate
        mAdapter.setImage(position, getIconForCurrentPlayer());
        updateMemberPositions(position);
    }

    private int getIconForCurrentPlayer() {
        // Reference to X or O, depending on value of mTurnX (which player's turn it is)
        return mTurnX ? R.drawable.ic_x : R.drawable.ic_o;
    }

    private void updateMemberPositions(int position) {
        mPriorPosition = mCurrentPosition;
        mCurrentPosition = position;
    }
    private void doPostPlayerTurn() {
        if (isGameOver()) {
            doGameOverTasks();
        } else {
            // flip mTurnX between X and O; meaning, set the turn to be the other player's turn
            setCurrentPlayerToX(!mTurnX);
        }
    }

    private void doGameOverTasks() {
        mGameOver = true;
        generateGameResults();
        tintWinningSpacesIfNotDraw();
        showGameOverSB(false);
    }
    private void tintWinningSpacesIfNotDraw() {
        if (!mLastWinner.equals(getString(R.string.no_winner))) {
            mAdapter.setImagesTint(mWinningSpaces, R.color.color_yes);
        }
    }

    private boolean isGameOver() {
        // Assume Game is over
        boolean gameOver = true;

        if (isWinner()) {
            mLastWinner = mTurnX ? getString(R.string.x) : getString(R.string.o);
        } else if (isBoardFull()) {
            mLastWinner = getString(R.string.no_winner);
        } else {
            gameOver = false;
        }
        return gameOver;
    }

    @NonNull
    private String getWinningRowColumnOrDiagonalMessage(int rowColLength) {
        int rowColDivideValue = mWinType.equals(WinType.ROW) ? rowColLength : 1;

        return getCurrentPlayer () + getString (R.string.info_has_won) +
                "\nWinning " +
                mWinType.toString ().toLowerCase () +
                (mWinType.equals (WinType.DIAGONAL) ? ": " : " number: ") +
                (mWinType.equals (WinType.DIAGONAL) ?
                        mWinTypeDiagonal.toString ().toLowerCase ().replace ('_', ' ') :
                        (mWinningSpaces[0] / rowColDivideValue) + 1) +
                '.';
    }
    private boolean isBoardFull() {
        int count = mAdapter.getItemCount(); //9
        boolean emptySpaceFound = false;

        for (int i = 0; i < count && !emptySpaceFound; i++) {
            emptySpaceFound = isSpaceEmpty(i);
        }
        return !emptySpaceFound;
    }

    private void generateGameResults() {
        if (mLastWinner.equals(getString(R.string.no_winner))) {
            mLastGameResultsMessage = getString(R.string.info_board_full);
        } else {
            final int ROW_COL_LENGTH = (int) Math.sqrt(mAdapter.getItemCount());
            mLastGameResultsMessage = getWinningRowColumnOrDiagonalMessage(ROW_COL_LENGTH);
        }
    }

    private boolean isWinner(){
        //TODO
        return true;
    }


}
