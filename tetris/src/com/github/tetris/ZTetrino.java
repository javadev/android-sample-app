package com.github.tetris;

public class ZTetrino extends Tetrino {
    private static final int BLOCK_TYPE = TileView.BLOCK_RED;

    public ZTetrino(int x, int y) {
        super(x, y);
        initTetrino();
        if (ghostEnabled) initGhost();
    }

    private void initTetrino() {
        this.sMap[0][0] = BLOCK_TYPE;
        this.sMap[0][1] = 0;
        this.sMap[0][2] = 0;
        this.sMap[1][0] = BLOCK_TYPE;
        this.sMap[1][1] = BLOCK_TYPE;
        this.sMap[1][2] = 0;
        this.sMap[2][0] = 0;
        this.sMap[2][1] = BLOCK_TYPE;
        this.sMap[2][2] = 0;
    }
}
