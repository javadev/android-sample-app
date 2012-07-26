package com.github.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TileView extends View {
	public static final String TAG = "TetrisBlast";
	
	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	protected static final int BLOCK_EMPTY = 0;
	protected static final int BLOCK_RED = 1;
	protected static final int BLOCK_BLUE = 2;
	protected static final int BLOCK_GREEN = 3;
	protected static final int BLOCK_YELLOW = 4;
	protected static final int BLOCK_PINK = 5;
	protected static final int BLOCK_LIGHBLUE = 6;
	protected static final int BLOCK_ORANGE = 7;
	protected static final int BLOCK_GREY = 8;
	protected static final int BLOCK_GHOST = 9;
	protected static final int BLOCK_BLOCK = 10;
	protected static final int BLOCK_BG1 = 11;
	protected static final int BLOCK_BG2 = 12;
	protected static final int NUM_OF_TILES = 12;
    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */
	protected static final double mXRatio = 0.625; //This is ratio of the tetris map to size of view
    protected static int mTileSize;

    protected static final int mXTileCount = 10;
    protected static final int mYTileCount = 20;

    protected static int mXOffset;
    protected static int mYOffset;


    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    private Bitmap[] mNextTetrinoArr;
    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    private final Paint mPaint = new Paint();

	protected int mCurNext;

    //public TextView myText;
    //Constructors
    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTileGrid = new int[mXTileCount][mYTileCount];
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTileGrid = new int[mXTileCount][mYTileCount];
    }
    

     /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    	mNextTetrinoArr = new Bitmap[MainMap.I_TYPE+1];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	calculateTileSize(w, h);
    	Resources r = this.getContext().getResources();
        loadTile(BLOCK_RED, r.getDrawable(R.drawable.block_red));
		loadTile(BLOCK_BLUE, r.getDrawable(R.drawable.block_blue));
		loadTile(BLOCK_GREEN, r.getDrawable(R.drawable.block_green));
		loadTile(BLOCK_YELLOW, r.getDrawable(R.drawable.block_yelow));
		loadTile(BLOCK_PINK, r.getDrawable(R.drawable.block_pink));
		loadTile(BLOCK_LIGHBLUE, r.getDrawable(R.drawable.block_lightblue));
		loadTile(BLOCK_ORANGE, r.getDrawable(R.drawable.block_orange));
		loadTile(BLOCK_GREY, r.getDrawable(R.drawable.block_grey));
		loadTile(BLOCK_GHOST, r.getDrawable(R.drawable.block_ghost2));
		loadTile(BLOCK_BLOCK, r.getDrawable(R.drawable.block_block));
		loadTile(BLOCK_BG1, r.getDrawable(R.drawable.block_bg1));
		loadTile(BLOCK_BG2, r.getDrawable(R.drawable.block_bg2));
		loadNextTetr(MainMap.I_TYPE, r.getDrawable(R.drawable.next_i));
		loadNextTetr(MainMap.J_TYPE, r.getDrawable(R.drawable.next_j));
		loadNextTetr(MainMap.L_TYPE, r.getDrawable(R.drawable.next_l));
		loadNextTetr(MainMap.O_TYPE, r.getDrawable(R.drawable.next_o));
		loadNextTetr(MainMap.S_TYPE, r.getDrawable(R.drawable.next_s));
		loadNextTetr(MainMap.T_TYPE, r.getDrawable(R.drawable.next_t));
		loadNextTetr(MainMap.Z_TYPE, r.getDrawable(R.drawable.next_z));
        clearTiles();
    }
    
    protected void calculateTileSize(int w, int h) {
    	Log.d(TAG, "OnSize changed, w = " + Integer.toString(w)+"h = " + Integer.toString(h));
    	mTileSize = (int)Math.floor((w*mXRatio)/mXTileCount);
        mXOffset = mTileSize;
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);
        
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mTileSize, mTileSize);
        tile.draw(canvas);
        mTileArray[key] = bitmap;
    }
    
    public void loadNextTetr(int key, Drawable tetrino) {
        Bitmap bitmap = Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tetrino.setBounds(0, 0, 90, 90);
        tetrino.draw(canvas);
        mNextTetrinoArr[key] = bitmap;
    }

    /**
     * Resets all tiles to BLOCK_EMPTY
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
            	setTile(BLOCK_EMPTY, x, y);
            }
        }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXTileCount; x += 1) {
            for (int y = 0; y < mYTileCount; y += 1) {
                if (mTileGrid[x][y] > 0) {
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y]], 
                    		mXOffset + x * mTileSize,
                    		mYOffset + y * mTileSize,
                    		mPaint);//TODO play with mPaint
                }
            }
        }
        canvas.drawBitmap(mNextTetrinoArr[mCurNext], 370, 140, mPaint);
    }

}
