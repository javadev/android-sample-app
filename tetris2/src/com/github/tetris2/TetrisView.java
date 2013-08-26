package com.github.tetris2;

import java.io.IOException;
import java.io.InputStream;

import com.github.tetris2.game.Block;
import com.github.tetris2.game.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TetrisView extends View {

	private static final int DELAY = 400;

	private RedrawHandler redrawHandler = new RedrawHandler(this);

	private static final int BLOCK_OFFSET = 2;
	private static final int FRAME_OFFSET_BASE = 10;

	private final Paint paint = new Paint();

	private int width;
	private int height;
	private Dimension cellSize = null;
	private Dimension frameOffset = null;

	private Model model = null;
	private long lastMove = 0;

	private MainActivity activity;

	public TetrisView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TetrisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}

	public void setGameCommand(Model.Move move) {
		if (null == model || !model.isGameActive()) {
			return;
		}
		if (Model.Move.DOWN.equals(move)) {
			model.genereteNewField(move);
			invalidate();
			return;
		}
		setGameCommandWithDelay(move);
	}

	public void setGameCommandWithDelay(Model.Move move) {
		long now = System.currentTimeMillis();

		if (now - lastMove > DELAY) {
			model.genereteNewField(move);
			invalidate();
			lastMove = now;
		}
		redrawHandler.sleep(DELAY);
	}

	private void drawCell(Canvas canvas, int row, int col) {

		byte nStatus = model.getCellStatus(row, col);

		if (Block.CELL_EMPTY != nStatus) {
			int color = Block.CELL_DYNAMIC == nStatus ? model
					.getActiveBlockColor() : Block
					.getColorForStaticValue(nStatus);
			drawCell(canvas, col, row, color);
		}
	}

	private void drawCell(Canvas canvas, int x, int y, int colorFG) {
		paint.setColor(colorFG);
		float top = frameOffset.getHeight() + y * cellSize.getHeight()
				+ BLOCK_OFFSET;
		float left = frameOffset.getWidth() + x * cellSize.getWidth()
				+ BLOCK_OFFSET;
		float bottom = frameOffset.getHeight() + (y + 1) * cellSize.getHeight()
				- BLOCK_OFFSET;
		float right = frameOffset.getWidth() + (x + 1) * cellSize.getWidth()
				- BLOCK_OFFSET;
		RectF rect = new RectF(left, top, right, bottom);

		canvas.drawRoundRect(rect, 4, 4, paint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawFrame(canvas);
		if (null == model) {
			return;
		}

		// draw all the cells:
		for (int i = 0; i < Model.NUM_ROWS; i++) {
			for (int j = 0; j < Model.NUM_COLS; j++) {
				drawCell(canvas, i, j);
			}
		}
	}

	private void drawFrame(Canvas canvas) {
		try {
			InputStream input = activity.getAssets().open("frame.png");
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			canvas.drawBitmap(bitmap, 0, 0, paint);
		} catch (IOException ex) {
			Log.e("asset", "can't open asset bitmap", ex);
		}
		paint.setColor(Color.LTGRAY);
		canvas.drawRect(frameOffset.getWidth(), frameOffset.getHeight(), width
				- frameOffset.getWidth(), height - frameOffset.getHeight(),
				paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		int cellWidth = (width - 2 * FRAME_OFFSET_BASE) / Model.NUM_COLS;
		int cellHeight = (height - 2 * FRAME_OFFSET_BASE) / Model.NUM_ROWS;

		int n = Math.min(cellWidth, cellHeight);

		this.cellSize = new Dimension(n, n);

		int offsetX = (w - Model.NUM_COLS * n) / 2;
		int offsetY = (h - Model.NUM_ROWS * n) / 2;
		this.frameOffset = new Dimension(offsetX, offsetY);
	}

	static class RedrawHandler extends Handler {

		private final TetrisView owner;

		private RedrawHandler(TetrisView owner) {
			this.owner = owner;
		}

		@Override
		public void handleMessage(Message msg) {
			if (null == owner.model) {
				return;
			}
			if (owner.model.isGameOver()) {
				owner.activity.endGame();
			}
			if (owner.model.isGameActive()) {
				owner.setGameCommandWithDelay(Model.Move.DOWN);
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};
}
