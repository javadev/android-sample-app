package com.github.tetris;

public class TetrinoMap {
    public static final int MAP_X_SIZE = 10;
    public static final int MAP_Y_SIZE = 20;
    private int[][] map;

    public TetrinoMap() {
        map = new int[MAP_X_SIZE][MAP_Y_SIZE];
        this.resetMap();
    }

    public TetrinoMap(int x, int y) {
        // TODO implement this for any map size
    }

    public void resetMap() {
        for (int x = 0; x < MAP_X_SIZE; x++) {
            for (int y = 0; y < MAP_Y_SIZE; y++) {
                map[x][y] = 0;
            }
        }
    }

    /**
     * This function puts the tetrino on the map and check collision with the other objects on a map
     *
     * @param shape - tetrino to put
     * @return true if puts successful otherwise false
     */
    public boolean putTetrinoOnMap(Tetrino shape) {
        for (int col = 0; col < shape.getSize(); col++) {
            for (int row = 0; row < shape.getSize(); row++) {
                if (shape.sMap[col][row] != TileView.BLOCK_EMPTY) {
                    if (shape.getXPos() + col >= 0
                            && shape.getXPos() + col < TetrinoMap.MAP_X_SIZE
                            && shape.getYPos() + row >= 0
                            && shape.getYPos() + row < TetrinoMap.MAP_Y_SIZE
                            && (map[shape.getXPos() + col][shape.getYPos() + row]
                                            == TileView.BLOCK_EMPTY
                                    || map[shape.getXPos() + col][shape.getYPos() + row]
                                            == TileView.BLOCK_GHOST))
                        map[shape.getXPos() + col][shape.getYPos() + row] = shape.sMap[col][row];
                    else return false;
                }
                if (Tetrino.ghostEnabled && !shape.onGhost()) {
                    if (shape.gMap[col][row] != TileView.BLOCK_EMPTY)
                        map[shape.getGhostXPos() + col][shape.getGhostYPos() + row] =
                                shape.gMap[col][row];
                }
            }
        }
        return true;
    }

    public int getMapValue(int x, int y) {
        return map[x][y];
    }

    public void copyFrom(TetrinoMap srcMap) {
        for (int x = 0; x < TetrinoMap.MAP_X_SIZE; x++) {
            for (int y = 0; y < TetrinoMap.MAP_Y_SIZE; y++) {
                this.map[x][y] = srcMap.getMapValue(x, y);
            }
        }
    }
    /**
     * This function checks the filled lines and return number of cleared lines
     *
     * @return number of cleared lines
     */
    public int lineCheckAndClear() {
        boolean isLine = true;
        int lineCounter = 0;
        for (int y = 0; y < 20; y++) { // TODO Auto-generated method stub
            for (int x = 0; x < 10; x++) {
                if (map[x][y] == TileView.BLOCK_EMPTY) isLine = false;
            }
            if (isLine) {
                removeLine(y);
                lineCounter++;
            }
            isLine = true;
        }
        return lineCounter;
    }

    private void removeLine(int row) {
        for (int y = row; y > 0; y--) { // TODO Auto-generated method stub
            for (int x = 0; x < 10; x++) {
                map[x][y] = map[x][y - 1];
            }
        }
        for (int x = 0; x < 10; x++) {
            map[x][0] = TileView.BLOCK_EMPTY;
        }
    }

    public void setMapValue(int i, int j, int value) {
        // TODO Auto-generated method stub
        map[i][j] = value;
    }
}
