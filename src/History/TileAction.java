package History;

import Content.Layer;
import Content.Tile;

public class TileAction extends HistoricAction {

    private final Layer LAYER;
    private final TileEdit[] EDITS;

    public TileAction(Layer layer, TileEdit[] history) {
        this.LAYER = layer;
        this.EDITS = history;
    }

    @Override
    public void redoAction() {
        Tile[][] tiles = LAYER.getTiles();

        for (TileEdit edit : EDITS) {
            tiles[edit.getX()][edit.getY()] = edit.getNewTile();
        }
    }

    @Override
    public void undoAction() {
        Tile[][] tiles = LAYER.getTiles();

        for (TileEdit action : EDITS) {
            tiles[action.getX()][action.getY()] = action.getOldTile();
        }
    }
}
