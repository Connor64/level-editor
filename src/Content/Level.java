package Content;

import UIComponents.LevelCanvas;
import History.TileAction;
import Serial.Tile;

import java.awt.*;
import java.util.ArrayList;

public class Level {
    private ArrayList<Layer> layers;

    private int width, height;
    private Layer currentLayer;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;

        layers = new ArrayList<>();
        currentLayer = null;
    }

    public void draw(Graphics2D g2, LevelCanvas canvas, int xPos, int yPos, double scaledSize) {
        int scaledSizeInt = (int) Math.ceil(scaledSize);

        System.out.println("layer count: " + layers.size());

        for (int i = 0; i < layers.size(); i++) {

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (layers.get(i).getTiles()[x][y] == null) continue;

                    // Create filled square if it exists
                    g2.setColor(Color.DARK_GRAY);

                    int newX = (int) (x * scaledSize) + xPos;
                    int newY = (int) (y * scaledSize) + yPos;

                    // Draw current tile
                    layers.get(i).getTiles()[x][y].draw(g2, newX, newY, scaledSizeInt, canvas);
                }
            }
        }
    }

    /**
     * Paints the tile at the given mouse coordinates.
     */
    public void paintTiles(Point newPos, Point oldPos, Tile tile, ArrayList<TileAction> currentAction) {
        if (layers.isEmpty() || (currentLayer == null)) return;

        int x1 = newPos.x;
        int y1 = newPos.y;

        if ((x1 < 0 || x1 >= width) || (y1 < 0 || y1 >= height)) return;

        Tile[][] layer = currentLayer.getTiles();

        if ((oldPos == null) || newPos.equals(oldPos)) {
            // If the coordinates are within the bounds of the array
            if (layer[x1][y1] == tile) return;

            currentAction.add(new TileAction(x1, y1, layer[x1][y1], tile));
            layer[x1][y1] = tile;
            return;
        }

        // Algorithm below is a modified Bresenham line-drawing algorithm as specified here:
        // http://eugen.dedu.free.fr/projects/bresenham/
        int x0 = oldPos.x;
        int y0 = oldPos.y;

        int x = x0;
        int y = y0;
        int incrementX = (x1 > x0) ? 1 : -1;
        int incrementY = (y1 > y0) ? 1 : -1;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int n = 1 + dx + dy;
        int error = dx - dy;

        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            if ((x >= 0 && x < width) && (y >= 0 && y < height)) {
                if (layer[x][y] != tile) {
                    currentAction.add(new TileAction(x, y, layer[x][y], tile));
                    layer[x][y] = tile;
                }

                if (error > 0) {
                    x += incrementX;
                    error -= dy;
                } else {
                    y += incrementY;
                    error += dx;
                }
            }
        }
    }

    public void resize(int newWidth, int newHeight, int resizeOption) {
        for (Layer layer : layers) {
            layer.resize(newWidth, newHeight, resizeOption);
        }

        width = newWidth;
        height = newHeight;
    }

    public void addLayer(String layerName) {
        layers.add(new Layer(width, height, layerName));
        if (currentLayer == null) {
            currentLayer = layers.get(layers.size() - 1);
        }
    }

    public void addLayer() {
        addLayer("layer " + layers.size() + 1);
    }

    public void removeLayer(int i) {
        if (i == layers.indexOf(currentLayer)) {
            if (((i - 1) >= 0) && ((i - 1) < layers.size())) {
                currentLayer = layers.get(i - 1);
            } else {
                currentLayer = null;
            }
        }

        layers.remove(i);
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int i) {
        currentLayer = layers.get(i);
    }

    public int getNumLayers() {
        return layers.size();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
