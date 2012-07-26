package com.github.tetris;

import android.graphics.Point;

public abstract class Tetrino {
	public static final int SIZE = 3;
	public static boolean ghostEnabled = true;
	public int[][] sMap;
	public int[][] gMap;//ghost map of tetrino
	//public int[][] shadowMap;
	private Point pos;
	protected Point ghostPos;
	
	public int getGhostXPos() {
		return ghostPos.x;
	}
	
	public int getGhostYPos() {
		return ghostPos.y;
	}

	public Tetrino(int x, int y) {
		sMap = new int[SIZE][SIZE];
		gMap = new int[SIZE][SIZE];
		initTetrino(SIZE);
		pos = new Point(x,y);
		ghostPos = new Point(x,y);
	}
	
	private void initTetrino(int tetrinoSize) {
		for(int col = 0; col < tetrinoSize; col++) {
			for(int row = 0; row < tetrinoSize; row++) {
				sMap[col][row] = TileView.BLOCK_EMPTY;
				gMap[col][row] = TileView.BLOCK_EMPTY;//ghost Map
			}
		}
		
	}

	protected void initGhost() {
		copyTetrinoMap(sMap, gMap, Tetrino.SIZE);
		ghostPos.set(pos.x, pos.y);
		setGhostY();
	}
	
	protected void resetGhost(int size) {
		for(int col = 0; col < size; col++) {
			for(int row = 0; row < size; row++)
				gMap[col][row] = TileView.BLOCK_EMPTY;//ghost Map
		}
	}

	public boolean rotateTetrino(TetrinoMap map) {
		int[][] temp = new int[SIZE][SIZE];
		for(int col = 0; col < SIZE; col++){
			for(int row = 0; row < SIZE; row++) {
				temp[col][row] = sMap[row][2-col];
			}
		}
		
		if(!isColusionX(this.pos.x, temp, map) && !isColusionY(this.pos.y, this.pos.x, temp, map, false)) {
			sMap = temp;
			resetGhost(SIZE);
			copyTetrinoMap(temp, gMap, SIZE);
			setGhostY();
			return true;
		}
		else if(!isColusionX(this.pos.x-1, temp, map) && !isColusionY(this.pos.y, this.pos.x-1, temp, map, false)) {
			this.pos.x -=1;
			this.ghostPos.x -=1;
			sMap = temp;
			resetGhost(SIZE);
			copyTetrinoMap(temp, gMap, SIZE);
			setGhostY();
			return true;
		}
		else if(!isColusionX(this.pos.x+1, temp, map) && !isColusionY(this.pos.y, this.pos.x+1, temp, map, false)) {
			this.pos.x +=1;
			this.ghostPos.x +=1;
			sMap = temp;
			resetGhost(SIZE);
			copyTetrinoMap(temp, gMap, SIZE);
			setGhostY();
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return the pos
	 */
	public Point getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public boolean setPos(int x, int y, TetrinoMap map) {
		if(x >= 0 && x < TetrinoMap.MAP_X_SIZE) {
			for(int col = 0; col < this.getSize(); col++){
				for(int row = 0; row < this.getSize(); row++) {
					if (sMap[col][row] != TileView.BLOCK_EMPTY) {
						if (x + col >= TetrinoMap.MAP_X_SIZE || x + col < 0 ||
								y + row >= TetrinoMap.MAP_Y_SIZE ||
								map.getMapValue(x + col, y + row) != TileView.BLOCK_EMPTY)
							return false;
					}
				}
			}
		}
		this.pos.x = x;
		this.pos.y = y;
		return true;
	}
	
	protected boolean isColusionY(int newY, int newX, int[][] tMap,TetrinoMap map, boolean isGhost) {
		// TODO Auto-generated method stub
		if(newY < TetrinoMap.MAP_Y_SIZE) {
			for(int col = 0; col < this.getSize(); col++){
				for(int row = 0; row < this.getSize(); row++) {
					if (tMap[col][row] != TileView.BLOCK_EMPTY) {
						if (isGhost) {//TODO need to think about if condition
							if ((newX + col) >= 0 && (newX + col) < TetrinoMap.MAP_X_SIZE) {
								if (newY + row >= TetrinoMap.MAP_Y_SIZE || 
										map.getMapValue(newX + col, newY + row) != TileView.BLOCK_EMPTY)
									return true;
							}
						}
						else {
							if ((newX + col) >= 0 && (newX + col) < TetrinoMap.MAP_X_SIZE) {
								if (newY + row >= TetrinoMap.MAP_Y_SIZE ||
										map.getMapValue(newX + col, newY + row) != TileView.BLOCK_EMPTY)
									return true;
							}
						}
					}
				}
			}
		}
		else
			return true;
		//if no collisions 
		return false;
	}
	/**
	 * This function move tetrino down by 1
	 * @param map - to check if possible
	 * @return true is success else false
	 */
	public boolean moveDown(TetrinoMap map) {
		if(!isColusionY(this.pos.y+1, this.pos.x, sMap, map, false)) {
			this.pos.y++;
			return true;
		}
		else
			return false;
	}
	
	protected boolean isColusionX(int newX, int[][] tMap,TetrinoMap map) {
		// TODO Auto-generated method stub
		if(newX >= -1 && newX < TetrinoMap.MAP_X_SIZE) {
			for(int col = 0; col < this.getSize(); col++){
				for(int row = 0; row < this.getSize(); row++) {
					if (tMap[col][row] != TileView.BLOCK_EMPTY) {
						if (newX + col >= TetrinoMap.MAP_X_SIZE || newX + col < 0 ||
								map.getMapValue(newX + col, this.pos.y + row) != TileView.BLOCK_EMPTY)
							return true;
					}
				}
			}
		}
		else
			return true;
		//if no collisions 
		return false;
	}


	public boolean moveLeft(TetrinoMap map) {
		if(!isColusionX(this.pos.x-1, sMap, map)) {
			this.pos.x--;
			this.ghostPos.x--;
			setGhostY();
			return true;
		}
		return false;
	}
	
	public boolean moveRight(TetrinoMap map) {
		if(!isColusionX(this.pos.x+1, sMap, map)) {
			this.pos.x++;
			this.ghostPos.x++;
			setGhostY();
			return true;
		}
		return false;
	}
	
	public void drop(TetrinoMap map) {
		if(ghostEnabled)
			this.pos.y = this.ghostPos.y;
		else
			for (int y = 0; y < TetrinoMap.MAP_Y_SIZE && !isColusionY(y, this.pos.x, sMap, map, false); y++)
				this.pos.y = y;
	}
	
	/**
	 * @return the x position
	 */
	public int getXPos() {
		return pos.x;
	}
	
	/**
	 * @return the y position
	 */
	public int getYPos() {
		return pos.y;
	}

	public int getSize() {
		// TODO Auto-generated method stub
		return SIZE;
	}
	
	protected void copyTetrinoMap(int [][] srcMap, int[][] destMap, int size) {
		for(int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (srcMap[x][y] != TileView.BLOCK_EMPTY)
					destMap[x][y] = TileView.BLOCK_GHOST;
			}
		}
	}
	
	protected void setGhostY() {
		for (ghostPos.y = this.pos.y; 
				!isColusionY(this.ghostPos.y+1, this.ghostPos.x, gMap, MainMap.mapOld, true);
				this.ghostPos.y++);
	}

	public boolean onGhost() {
		if(pos.y == ghostPos.y)
			return true;
		return false;
	}
	
}
