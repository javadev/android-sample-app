package com.github.tetris2.game;

public class Point {
	private int x;
	private int y;
	
	public Point( Point that ) {
		this( that.x, that.y );
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX( int x ) {
		this.x = x;
	}
	public void setY( int y ) {
		this.y = y;
	}

}
