package History;

import Content.Tile;

public class TileEdit {
    private final int X, Y;
    private final Tile OLD_TILE, NEW_TILE;

    public TileEdit(int x, int y, Tile oldTile, Tile newTile) {
        this.X = x;
        this.Y = y;
        this.OLD_TILE = oldTile;
        this.NEW_TILE = newTile;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Tile getOldTile() {
        return OLD_TILE;
    }

    public Tile getNewTile() {
        return NEW_TILE;
    }
}