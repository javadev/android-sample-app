package com.github.tetris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MainMap extends TileView {
    public static final int L_TYPE = 0;
    public static final int J_TYPE = 1;
    public static final int T_TYPE = 2;
    public static final int Z_TYPE = 3;
    public static final int S_TYPE = 4;
    public static final int O_TYPE = 5;
    public static final int I_TYPE = 6;

    public int tempCount;
    /**
     * mSnakeTrail: a list of Coordinates that make up the snake's body mAppleList: the secret
     * location of the juicy apples the snake craves.
     */

    // private TetrisShape myShape;

    /**
     * Create a simple handler that we can use to cause animation to happen. We set ourselves as a
     * target and we can use the sleep() function to cause an update/invalidate to occur at a later
     * date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    /** This is speed parameter of the game */
    private long mMoveDelay;

    private int mGameState = PAUSE;
    // private boolean noShape = true;
    private Tetrino curTetrino;

    /**
     * Two dimensional arrays hold the tetris map - mapCur - hold the current map - mapOld - hold
     * the map without current tetrino - mapLast - hold the map before last move of tetrino
     */
    private static TetrinoMap mapCur;

    public static TetrinoMap mapOld;
    private static TetrinoMap mapLast;

    private static int[] randArr = {-1, -1};
    /**
     * This parameter is the flag that indicate that Action_Down event was occur and tetrino was
     * moved left or right
     */
    private boolean wasMoved;

    private boolean pausePressed = false;

    /** Initial coordinate of the Action_Down event */
    private int xInitRaw;

    private int yInitRaw;
    private int yInitDrop;
    private long initTime;
    private static final long deltaTh = 300; // threshold time for drop
    /** X move sensitivity */
    private static final int xMoveSens = 20;

    /** Rotate sensitivity */
    private static final int rotateSens = 10;
    /** Drop down sensitivity */
    private static final int dropSensativity = 30; // ~30*3.5

    public static final int READY = 1;
    public static final int PAUSE = 0;

    private class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (mGameState == READY) {
                if (msg.what == 1) { // TODO change to final Name
                    mapCur.copyFrom(mapLast);
                    int i = mapCur.lineCheckAndClear();
                    // Log.d(TAG, "Cleared " + Integer.toString(i) + " lines!");
                    mapOld.copyFrom(mapCur);

                    curTetrino = newTetrino(getRandomFromArr(), 4, 0); // TODO check this
                    Log.d(TAG, "Next: " + Integer.toString(randArr[1]));
                    tempCount++;
                    if (!mapCur.putTetrinoOnMap(curTetrino)) {
                        Log.d(TAG, "Game Over!");
                        initNewGame();
                        mGameState = PAUSE;
                    }
                    mRedrawHandler.sleep(mMoveDelay);
                } else {
                    clearTiles();
                    updateMap();
                    mapCur.resetMap();
                    mapCur.copyFrom(mapOld);
                    gameMove(); // TODO insert this function to the Tetrino
                    MainMap.this.invalidate();
                }
            }
            // mRedrawHandler.sleep(mMoveDelay);
            // MainMap.this.update();
            // mGameState = true;
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendEmptyMessageDelayed(0, delayMillis);
            // sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    /**
     * Constructs a MainMap View based on inflation from XML
     *
     * @param context
     * @param attrs
     */
    public MainMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "MainMap constructor");
        initMainMap();
    }

    public MainMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "MainMap constructor defStyle");
        initMainMap();
    }

    /** Initialize MainMap Tail icons from drawable */
    private void initMainMap() {
        setFocusable(true);
        mapCur = new TetrinoMap();
        mapOld = new TetrinoMap();
        mapLast = new TetrinoMap();
        resetTiles(NUM_OF_TILES + 10); // TODO fix this
    }

    public void initNewGame() {
        // mTileList.clear();
        Log.d(TAG, "game init");
        mMoveDelay = 1200; // delay [ms]
        mapCur.resetMap();
        mapOld.resetMap();
        mapLast.resetMap();
        // noShape = true;
        tempCount = 0;
        mRedrawHandler.sendEmptyMessage(1); // TODO change to final name
    }

    private Tetrino newTetrino(int type, int x, int y) {
        switch (type) {
            case L_TYPE:
                return new LTetrino(x, y);
            case J_TYPE:
                return new JTetrino(x, y);
            case T_TYPE:
                return new TTetrino(x, y);
            case Z_TYPE:
                return new ZTetrino(x, y);
            case S_TYPE:
                return new STetrino(x, y);
            case O_TYPE:
                return new OTetrino(x, y);
            case I_TYPE:
                return new ITetrino(x, y);
            default:
                return new LTetrino(x, y);
        }
    }

    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of ints before we can
     * stuff them into a map for flattening and storage.
     *
     * @param pointsList : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates as
     *     [x1,y1,v1,x2,y2,v2,x3,y3,v3...]
     */
    private int[] coordArrayListToArray(TetrinoMap map) {
        int[] rawArray = new int[TetrinoMap.MAP_X_SIZE * TetrinoMap.MAP_Y_SIZE];
        for (int row = 0; row < TetrinoMap.MAP_Y_SIZE; row++) {
            for (int col = 0; col < TetrinoMap.MAP_X_SIZE; col++) {
                rawArray[row * TetrinoMap.MAP_X_SIZE + col] = map.getMapValue(col, row);
            }
        }
        return rawArray;
    }

    /**
     * Save game state so that the user does not lose anything if the game process is killed while
     * we are in the background.
     *
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();
        map.putIntArray("mapCur", coordArrayListToArray(mapCur));
        map.putIntArray("mapLast", coordArrayListToArray(mapLast));
        map.putIntArray("mapOld", coordArrayListToArray(mapOld));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        return map;
    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a ArrayList of
     * Coordinate objects
     *
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    private TetrinoMap coordArrayToArrayList(int[] rawArray) {
        TetrinoMap tMap = new TetrinoMap(); // TODO change to get map from argument
        int arrSize = rawArray.length;
        for (int i = 0; i < arrSize; i++) {
            tMap.setMapValue(i % TetrinoMap.MAP_X_SIZE, (i / TetrinoMap.MAP_Y_SIZE), rawArray[i]);
        }
        return tMap;
    }

    /**
     * Restore game state if our process is being relaunched
     *
     * @param icicle a Bundle containing the game state
     */
    public void restoreState(Bundle icicle) {
        setMode(PAUSE);
        mapCur = coordArrayToArrayList(icicle.getIntArray("mapCur"));
        mapLast = coordArrayToArrayList(icicle.getIntArray("mapLast"));
        mapOld = coordArrayToArrayList(icicle.getIntArray("mapOld"));
        mMoveDelay = icicle.getLong("mMoveDelay");
    }

    /*
     * touch recognition
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // This prevents touchscreen events from flooding the main thread
        synchronized (event) {
            try {
                // Waits 16ms.
                event.wait(16);

                // when user touches the screen
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    initTime = SystemClock.uptimeMillis();
                    xInitRaw = (int) Math.floor(event.getRawX());
                    yInitRaw = (int) Math.floor(event.getRawY());
                    yInitDrop = yInitRaw;
                    wasMoved = false;
                    if (xInitRaw > 360 && xInitRaw < 450 && yInitRaw > 560 && yInitRaw < 600) {
                        pausePressed = true;
                        if (mGameState == READY) mGameState = PAUSE;
                        else mGameState = READY;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE
                        && mGameState == READY
                        && !pausePressed) {
                    int xCurRaw = (int) Math.floor(event.getRawX());
                    int yCurRaw = (int) Math.floor(event.getRawY());
                    if ((xInitRaw - xCurRaw) > xMoveSens
                            && Math.abs(yInitRaw - yCurRaw) < dropSensativity) {
                        int q = (xInitRaw - xCurRaw) / xMoveSens;
                        if (q > 1) Log.d(TAG, "move left q = " + Integer.toString(q));
                        wasMoved = true;
                        xInitRaw = xCurRaw;
                        mapCur.resetMap();
                        mapCur.copyFrom(mapOld);
                        for (int i = 0; i < q; i++) {
                            if (curTetrino.moveLeft(mapCur)
                                    && !curTetrino.isColusionY(
                                            curTetrino.getYPos() + 1,
                                            curTetrino.getXPos(),
                                            curTetrino.sMap,
                                            mapCur,
                                            false)) {
                                if (mRedrawHandler.hasMessages(1)
                                        == true) { // TODO change to final Name
                                    mRedrawHandler.removeMessages(1);
                                    mRedrawHandler.sendEmptyMessageDelayed(
                                            0,
                                            400); // TODO convert to parameter and change to final
                                                  // Name
                                }
                            }

                            mapCur.putTetrinoOnMap(curTetrino);
                        }
                        update();
                    } else if ((xCurRaw - xInitRaw) > xMoveSens
                            && Math.abs(yInitRaw - yCurRaw) < dropSensativity) {
                        int q = (xCurRaw - xInitRaw) / xMoveSens;
                        if (q > 1) Log.d(TAG, "move left q = " + Integer.toString(q));
                        wasMoved = true;
                        xInitRaw = xCurRaw;
                        mapCur.resetMap();
                        mapCur.copyFrom(mapOld);
                        for (int i = 0; i < q; i++) {
                            if (curTetrino.moveRight(mapCur)
                                    && !curTetrino.isColusionY(
                                            curTetrino.getYPos() + 1,
                                            curTetrino.getXPos(),
                                            curTetrino.sMap,
                                            mapCur,
                                            false)) {
                                if (mRedrawHandler.hasMessages(1)
                                        == true) { // TODO change to final Name
                                    mRedrawHandler.removeMessages(1);
                                    mRedrawHandler.sendEmptyMessageDelayed(
                                            0,
                                            400); // TODO convert to parameter and change to final
                                                  // Name
                                }
                            }
                            mapCur.putTetrinoOnMap(curTetrino);
                        }
                        update();
                    }
                    if ((yCurRaw - yInitRaw) > xMoveSens) {
                        long timeDelta = Math.abs(initTime - SystemClock.uptimeMillis());
                        if (timeDelta > deltaTh) {
                            yInitDrop = yCurRaw;
                            initTime = SystemClock.uptimeMillis();
                        }
                        wasMoved = true;
                        yInitRaw = yCurRaw;
                        // yInitDrop = yInitRaw;
                        mapCur.resetMap();
                        mapCur.copyFrom(mapOld);
                        curTetrino.moveDown(mapCur);
                        mapCur.putTetrinoOnMap(curTetrino);
                        update();
                    }
                }

                // when screen is released
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long timeDelta = Math.abs(initTime - SystemClock.uptimeMillis());
                    if (mGameState == READY && !pausePressed) {
                        int yCurRaw = (int) Math.floor(event.getRawY());
                        if (yCurRaw - yInitDrop > dropSensativity && timeDelta < deltaTh) {
                            mapCur.resetMap();
                            mapCur.copyFrom(mapOld);
                            curTetrino.drop(mapCur);
                            mapCur.putTetrinoOnMap(curTetrino);
                            update();
                            mRedrawHandler.removeMessages(0);
                            mRedrawHandler.sendEmptyMessage(1); // TODO change to final name
                        }
                        // Rotate tetrino (release on same x pos)
                        else if (!wasMoved && Math.abs(yCurRaw - yInitRaw) < rotateSens) {
                            mapCur.resetMap();
                            mapCur.copyFrom(mapOld);
                            curTetrino.rotateTetrino(mapCur);
                            mapCur.putTetrinoOnMap(curTetrino);
                            update();
                        }
                    } else pausePressed = false;
                }

            } catch (InterruptedException e) {
                return true;
            }
        }
        return true;
    }

    private int getRandomFromArr() {
        if (randArr[1] == -1) randArr[1] = (int) Math.floor(Math.random() * 7);
        randArr[0] = randArr[1]; // shift to next
        randArr[1] = (int) Math.floor(Math.random() * 7); // next
        mCurNext = randArr[1];
        return randArr[0];
    }

    private void gameMove() {
        if (curTetrino.moveDown(mapCur)) {
            mapCur.putTetrinoOnMap(curTetrino);
            mRedrawHandler.sleep(mMoveDelay);
        } else { // TODO convert to parametr and convert to final name
            mRedrawHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running state, determining if
     * a move should be made, updating the snake's location.
     */
    public void update() {
        if (mGameState == READY) {
            clearTiles();
            updateMap();
            MainMap.this.invalidate();
        }
    }

    private void updateMap() {
        mapLast.copyFrom(mapCur);
        for (int col = 0; col < TetrinoMap.MAP_X_SIZE; col++) {
            for (int row = 0; row < TetrinoMap.MAP_Y_SIZE; row++) {
                setTile(mapLast.getMapValue(col, row), col, row);
            }
        }
    }

    public void setMode(int state) {
        // TODO Auto-generated method stub
        mGameState = state;
    }
}
