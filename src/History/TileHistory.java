package History;

import Content.Layer;
import Serial.Tile;

public class TileHistory extends Historic {

    private Layer layer;
    private TileAction[] history;

    public TileHistory(Layer layer, TileAction[] history) {
        this.layer = layer;
        this.history = history;
    }

    @Override
    public void redoAction() {
        Tile[][] tiles = layer.getTiles();

        for (TileAction action : history) {
            tiles[action.getX()][action.getY()] = action.getNewTile();
        }
    }

    @Override
    public void undoAction() {
        Tile[][] tiles = layer.getTiles();

        for (TileAction action : history) {
            tiles[action.getX()][action.getY()] = action.getOldTile();
        }
    }
}
