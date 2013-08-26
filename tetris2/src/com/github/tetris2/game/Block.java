package com.github.tetris2.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Block implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum BlockColor {
		RED(0xff990000, (byte) 2), GREEN(0xff009900, (byte) 3), BLUE(
				0xff000099, (byte) 4), YELLOW(0xffffcc33, (byte) 5), CYAN(
				0xff3399aa, (byte) 6);
		private final int color;
		private final byte value;

		private BlockColor(int color, byte value) {
			this.color = color;
			this.value = value;
		}
	}

	// cell status values (outer):
	public static final byte CELL_EMPTY = 0;
	public static final byte CELL_DYNAMIC = 1;

	private static Random random = new Random();

	// current block state:
	private int shape = 0;
	private int frame = 0;
	private Point topLeft = new Point( Model.NUM_COLS / 2, 0);
	private BlockColor color;

	public int getFrame() {
		return frame;
	}

	public int getColor() {
		return color.color;
	}

	public byte getStaticValue() {
		return color.value;
	}

	public static int getColorForStaticValue(byte b) {
		for (BlockColor item : BlockColor.values()) {
			if (b == item.value) {
				return item.color;
			}
		}
		return -1; // color is not found
	}

	public final void setState(int frame, Point topLeft) {
		this.frame = frame;
		this.topLeft = topLeft;
	}

	public final int getFramesCount() {
		return Shape.values()[shape].frameCount;
	}

	public final byte[][] getShape(int nFrame) {
		return Shape.values()[shape].getFrame(nFrame).get();
	}

	public final int getShapeWidth(int nFrame) {
		return Shape.values()[shape].getFrame(nFrame).width;
	}

	public final static synchronized Block createBlock() {
		// generate random number:
		int indexShape = random.nextInt(Shape.values().length);
		BlockColor blockColor = BlockColor.values()[random.nextInt(BlockColor
				.values().length)];
		Block result = new Block(indexShape, blockColor);
		// Set to the middle
		result.topLeft.setX( result.topLeft.getX() - Shape.values()[indexShape].getStartMiddleX());
		
		return result;
		
	}

	private Block(int nShape, BlockColor blockColor) {
		shape = nShape;
		this.color = blockColor;
	}

	public Point getTopLeft() {
		return this.topLeft;
	}

	private enum Shape {
		S1(1, 1) {
			@Override
			public Frame getFrame(int n) {
				return new Frame(2).add("11").add("11");
			}

		},
		S2(2, 2) {

			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(4).add("1111");
				case 1:
					return new Frame(1).add("1").add("1").add("1").add("1");
				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		},
		S3(4, 1) {
			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(3).add("010").add("111");
				case 1:
					return new Frame(2).add("10").add("11").add("10");
				case 2:
					return new Frame(3).add("111").add("010");
				case 3:
					return new Frame(2).add("01").add("11").add("01");
				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		},
		S4(4, 1) {
			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(3).add("100").add("111");
				case 1:
					return new Frame(2).add("11").add("10").add("10");
				case 2:
					return new Frame(3).add("111").add("001");
				case 3:
					return new Frame(2).add("01").add("01").add("11");
				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		},
		S5(4, 1) {
			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(3).add("001").add("111");
				case 1:
					return new Frame(2).add("10").add("10").add("11");
				case 2:
					return new Frame(3).add("111").add("100");
				case 3:
					return new Frame(2).add("11").add("01").add("01");
				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		},
		S6(2, 1) {
			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(3).add("110").add("011");
				case 1:
					return new Frame(2).add("01").add("11").add("10");
				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		},
		S7(2, 1) {
			@Override
			public Frame getFrame(int n) {
				switch (n) {
				case 0:
					return new Frame(3).add("011").add("110");
				case 1:
					return new Frame(2).add("10").add("11").add("01");

				}
				throw new IllegalArgumentException("Invalid frame number: " + n);
			}
		};
		private final int frameCount;
		private final int startMiddleX;

		private Shape(int frameCount, int startMiddleX) {
			this.frameCount = frameCount;
			this.startMiddleX = startMiddleX;
		}
		
		private int getStartMiddleX() {
			return startMiddleX;
		}

		public abstract Frame getFrame(int n);
	}

	private static class Frame {
		private final int width;

		private Frame(int width) {
			this.width = width;
		}

		private final List<byte[]> data = new ArrayList<byte[]>(4);

		private Frame add(String rowStr) {
			byte[] row = new byte[rowStr.length()];
			for (int i = 0; i < rowStr.length(); i++) {
				row[i] = Byte.valueOf("" + rowStr.charAt(i));
			}
			data.add(row);
			return this;
		}

		private byte[][] get() {
			byte[][] result = new byte[data.size()][];
			return data.toArray(result);
		}
	}
}
