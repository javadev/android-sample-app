package com.github.tetris2.game;

import android.os.Bundle;
import android.widget.TextView;

public class ScoresCounter {
	private static final String TAG_SCORES = "scores";
	private static final String TAG_LINES = "lines";
	
	private int scores = 0;
	private int lines = 0;
	private int scoreDelta = 4;
	
	private final TextView status;
	private final String format;
	
	public ScoresCounter( TextView status, String format ) {
		this.status = status;
		this.format = format;
	}

	public void reset() {
		scores = 0;
		lines = 0;
		updateStatus();
	}

	public int getScores() {
		return scores;
	}

	public int getLines() {
		return lines;
	}

	public void addScores() {
		scores += scoreDelta;
		updateStatus();
	}

	public void addLine() {
		lines++;
		updateStatus();
	}
	

	private void updateStatus() {
		status.setText( String.format( format, lines, scores));
	}

	public void storeTo(Bundle bundle) {
		bundle.putInt(TAG_LINES, lines);
		bundle.putInt(TAG_SCORES, scores);
	}

	public void restoreFrom(Bundle bundle) {
		this.lines = bundle.getInt(TAG_LINES);
		this.scores = bundle.getInt(TAG_SCORES);
	}	
}
