package com.github.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TetriBlastActivity extends Activity {

    private MainMap mMainMapView;

    public static final String TAG = "TetrisBlast";

    private static String ICICLE_KEY = "tetris-blast-view";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "Create main layout");
        getWindow().setBackgroundDrawableResource(R.drawable.tetris_bg); // Draw background
        mMainMapView = (MainMap) findViewById(R.id.tetris);
        mMainMapView.initNewGame();
        // TextView myText = (TextView) findViewById(R.id.txt);

        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mMainMapView.setMode(MainMap.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mMainMapView.restoreState(map);
            } else {
                mMainMapView.setMode(MainMap.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mMainMapView.setMode(MainMap.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onPause();
        // Pause the game along with the activity
        mMainMapView.setMode(MainMap.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the game state
        outState.putBundle(ICICLE_KEY, mMainMapView.saveState());
    }
}
