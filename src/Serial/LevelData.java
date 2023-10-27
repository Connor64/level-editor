package Serial;

import Content.Level;

import java.io.Serializable;

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;
    public Tile[][][] layers;
    public String levelName;
    public int width;
    public int height;

    public LevelData(Level level, String levelName) {
        this.levelName = levelName;
        this.width = level.getWidth();
        this.height = level.getHeight();

        this.layers = new Tile[level.getNumLayers()][width][height];

        for (int i = 0; i < level.getNumLayers(); i++) {
            Tile[][] layer = level.getCurrentLayer().getTiles();
            for (int x = 0; x < layer.length; x++) {
                for (int y = 0; y < layer[x].length; y++) {
                    this.layers[i][x][y] = new Tile(layer[x][y]);
                }
            }
        }
    }
}
