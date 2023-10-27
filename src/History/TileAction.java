package History;

import Serial.Tile;

public class TileAction {
    private int x, y;
    private Tile oldTile, newTile;

    public TileAction(int x, int y, Tile oldTile, Tile newTile) {
        this.x = x;
        this.y = y;
        this.oldTile = oldTile;
        this.newTile = newTile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Tile getOldTile() {
        return oldTile;
    }

    public Tile getNewTile() {
        return newTile;
    }
}