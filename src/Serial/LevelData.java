package Serial;

import java.io.Serializable;
import java.util.ArrayList;

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;
    public Tile[][][] layers;
    public String levelName;
    public int width;
    public int height;

    public LevelData(ArrayList<Tile[][]> layers, String levelName, int width, int height) {
        this.layers = new Tile[layers.size()][width][height];

        for (int i = 0; i < layers.size(); i++) {
            for (int x = 0; x < layers.get(i).length; x++) {
                for (int y = 0; y < layers.get(i)[x].length; y++) {
                    this.layers[i][x][y] = new Tile(layers.get(i)[x][y]);
                }
            }
        }

        this.levelName = levelName;
        this.width = width;
        this.height = height;
    }
}
