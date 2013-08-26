package com.github.tetris2.game;


import android.os.Bundle;

public class Model {

	public enum GameStatus {
		BEFORE_START {}, ACTIVE {}, PAUSED {}, OVER {};
	}
	
	public enum Move {
		LEFT, RIGHT, ROTATE, DOWN
	}
	
	private static final String TAG_DATA = "data";
	private static final String TAG_ACTIVE_BLOCK = "active-block";

	// some constants in the model:
	public static final int NUM_COLS = 10; // number of columns in field
	public static final int NUM_ROWS = 20; // number of rows in field

	// game status constants:
	private GameStatus gameStatus = GameStatus.BEFORE_START;

	// array of cell values:
	private byte[][] field = null;

	// active block:
	private Block activeBlock = null;

	// scores counter:
	private ScoresCounter counter = null;

	public Model() {
		field = new byte[NUM_ROWS][NUM_COLS];
	}
	
	public void setCounter(ScoresCounter counter) {
		this.counter = counter;
	}

	public boolean isGameActive() {
		return GameStatus.ACTIVE.equals(gameStatus);
	}

	public boolean isGameOver() {
		return GameStatus.OVER.equals(gameStatus);
	}
	
	public boolean isGameBeforeStart() {
		return GameStatus.BEFORE_START.equals(gameStatus);
	}	

	public void reset() {
		reset(false); // call the inner method - reset the all data
	}

	public byte getCellStatus(int nRow, int nCol) {
		return field[nRow][nCol];
	}

	public void setCellStatus(int nRow, int nCol, byte nStatus) {
		field[nRow][nCol] = nStatus;
	}

	public synchronized void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	// Start the game:
	public void gameStart() {
		if (isGameActive()) {
			return;
		}
		setGameActive();
		activeBlock = Block.createBlock();
		
	}

	public void setGameActive() {
		setGameStatus(GameStatus.ACTIVE);
	}
	
	public void setGamePaused() {
		setGameStatus(GameStatus.PAUSED);
	}

	public boolean isGamePaused() {
		return GameStatus.PAUSED.equals(gameStatus);
	}	
	
	public synchronized void genereteNewField(Move move) {	
		
		if (!isGameActive()) {
			return;
		}

		// get the parameters of block:
		Point newTopLeft = new Point(activeBlock.getTopLeft());
		int nFrame = activeBlock.getFrame();

		// Clear the old values:
		reset(true);

		// count new parameters:
		switch (move) {
		case LEFT:
			newTopLeft.setX(newTopLeft.getX() - 1);
			break;
		case RIGHT:
			newTopLeft.setX(newTopLeft.getX() + 1);
			break;
		case DOWN:
			newTopLeft.setY(newTopLeft.getY() + 1);
			break;
		case ROTATE:
			nFrame++;
			if (nFrame >= activeBlock.getFramesCount())
				nFrame = 0;
			break;
		}
		if (!isMoveValid(newTopLeft, nFrame)) {

			// set old the block:
			isMoveValid(activeBlock.getTopLeft(), activeBlock.getFrame());

			if (Move.DOWN.equals(move)) {

				// add the scores:
				counter.addScores();

				if (!newBlock()) {
					// Game is over
					setGameStatus(GameStatus.OVER);
					
					activeBlock = null;
					reset(false);
				}
			}

		} else {
			// Make the new move:
			activeBlock.setState(nFrame, newTopLeft);
		}
	}

	// ================================================
	// Helper functions:

	/**
	 * Reset the field data:
	 * 
	 * @param true - clear only dynamic data, false - clear all the data
	 */
	private final void reset(boolean bDynamicDataOnly) {
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				if (!bDynamicDataOnly || field[i][j] == Block.CELL_DYNAMIC) {
					field[i][j] = Block.CELL_EMPTY;
				}
			}
		}
	}

	private final boolean isMoveValid(Point newTopLeft, int nFrame) {
		synchronized (field) {
			byte[][] shape = activeBlock.getShape(nFrame);

			if (newTopLeft.getY() < 0) {
				return false;
			}
			if (newTopLeft.getX() < 0) {
				return false;
			}
			if (newTopLeft.getY() + shape.length > NUM_ROWS) {
				return false;
			}
			if (newTopLeft.getX() + shape[0].length > NUM_COLS) {
				return false;
			}

			// Check all the items in field:
			for (int i = 0; i < shape.length; i++) {
				for (int j = 0; j < shape[i].length; j++) {
					int y = newTopLeft.getY() + i;
					int x = newTopLeft.getX() + j;
					if( Block.CELL_EMPTY!=shape[i][j] && Block.CELL_EMPTY!=field[y][x]) {
						return false;
					}
				}
			}

			// All cell is correct - add the data:
			for (int i = 0; i < shape.length; i++) {
				for (int j = 0; j < shape[i].length; j++) {
					int y = newTopLeft.getY() + i;
					int x = newTopLeft.getX() + j;
					if( Block.CELL_EMPTY!=shape[i][j]) {
						field[y][x] = shape[i][j];
					}
				}
			}
			return true;
		}
	}

	/**
	 * Create the new block:
	 * 
	 * @return true - block can be generated,
	 * @return false - can't generate the block - GAME OVER!
	 */
	private synchronized boolean newBlock() {

		// set all the dynamic data as static:
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				byte status = getCellStatus(i, j);
				if (status == Block.CELL_DYNAMIC) {
					status = activeBlock.getStaticValue();
					setCellStatus(i, j, status);
				}
			}
		}

		for (int i = 0; i < field.length; i++) {
			boolean bFullRow = true;
			for (int j = 0; j < field[i].length; j++) {
				byte status = getCellStatus(i, j);
				boolean isEmpty = Block.CELL_EMPTY == status;
				bFullRow &= !isEmpty;
			}
			if (bFullRow) {
				shiftRows(i);

				// add lines to counter:
				counter.addLine();
			}
		}

		// Generate the new block:
		activeBlock = Block.createBlock();

		// Check the validity of new block:
		if (!isMoveValid(activeBlock.getTopLeft(), activeBlock.getFrame())) {
			// GAME IS OVER!
			return false;
		}
		return true;
	}

	private synchronized final void shiftRows(int nToRow) {
		if (nToRow > 0) {
			for (int j = nToRow - 1; j >= 0; j--) {
				for (int m = 0; m < field[j].length; m++) {
					setCellStatus(j + 1, m, getCellStatus(j, m));
				}
			}
		}
		for (int j = 0; j < field[0].length; j++) {
			setCellStatus(0, j, Block.CELL_EMPTY);
		}
	}

	public int getActiveBlockColor() {
		return activeBlock.getColor();
	}

	public void storeTo(Bundle bundle) {
		bundle.putSerializable(TAG_ACTIVE_BLOCK, activeBlock);
		bundle.putIntArray(TAG_DATA, getIntArrayFromData());
	}

	public void restoreFrom(Bundle bundle) {
		activeBlock = Block.class.cast( bundle.getSerializable(TAG_ACTIVE_BLOCK));
		restoreDataFromIntArray( bundle.getIntArray(TAG_DATA));
	}
	
	private void restoreDataFromIntArray(int[] src) {
		if( null==src ) {
			return;
		}
		for( int k=0; k<src.length; k++ ) {
			int i = k / NUM_COLS;
			int j = k % NUM_COLS;
			field[i][j] = (byte) src[k];
		}
	}

	private int[] getIntArrayFromData() {
		int[] result = new int[ NUM_COLS * NUM_ROWS ];
		for( int i=0; i<NUM_ROWS; i++ ) {
			for( int j=0; j<NUM_COLS; j++ ) {
				result[ NUM_COLS * i + j ] = field[i][j];
			}
		}
		return result;
	}
	
}
