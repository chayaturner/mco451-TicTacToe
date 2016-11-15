package com.cturner.tictactoe;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    }

    private void startNewOrResumeGameState(){

    }
    
    private void createUnfilledBoard() {

    }

    private void initializePreferences(){

    }

    private void initializeSnackBar(){

    }

    private void setupInitialSession(){

    }

}
