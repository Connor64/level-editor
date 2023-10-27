package Content;

import Serial.Tile;

public class Layer {
    private Tile[][] tiles;
    private String name;

    public Layer(int width, int height, String name) {
        this.tiles = new Tile[width][height];
        this.name = name;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resize(int newWidth, int newHeight, int resizeOption) {
        int oldWidth = tiles.length;
        int oldHeight = tiles[0].length;

        Tile[][] newLayer = new Tile[newWidth][newHeight];

        int xBound = Math.min(oldWidth, newWidth);
        int yBound = Math.min(oldHeight, newHeight);
        int xMaxBound = Math.max(oldWidth, newWidth);
        int yMaxBound = Math.max(oldHeight, newHeight);

        int xOffset = (xMaxBound - xBound) / 2;
        int yOffset = (yMaxBound - yBound) / 2;

        int newXOffset = (newWidth > oldWidth) ? xOffset : 0;
        int newYOffset = (newHeight > oldHeight) ? yOffset : 0;
        int oldXOffset = (newWidth < oldWidth) ? xOffset : 0;
        int oldYOffset = (newHeight < oldHeight) ? yOffset : 0;

        // Resize level based on option selected
        switch (resizeOption) {
            case 0: // Top left
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[newWidth - 1 - x][newHeight - 1 - y] = tiles[oldWidth - 1 - x][oldHeight - 1 - y];
                    }
                }
                break;
            case 1: // Up
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x + newXOffset][newHeight - 1 - y] = tiles[x + oldXOffset][oldHeight - 1 - y];
                    }
                }
                break;
            case 2: // Top right
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x][newHeight - 1 - y] = tiles[x][oldHeight - 1 - y];
                    }
                }
                break;
            case 3: // Left
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[newWidth - 1 - x][y + newYOffset] = tiles[oldWidth - 1 - x][y + oldYOffset];
                    }
                }
                break;
            case 4: // Center
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x + newXOffset][y + newYOffset] = tiles[x + oldXOffset][y + oldYOffset];
                    }
                }
                break;
            case 5: // Right
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x][y + newYOffset] = tiles[x][y + oldYOffset];
                    }
                }
                break;
            case 6: // Bottom left
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[newWidth - 1 - x][y] = tiles[oldWidth - 1 - x][y];
                    }
                }
                break;
            case 7: // Down
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x + newXOffset][y] = tiles[x + oldXOffset][y];
                    }
                }
                break;
            case 8: // Bottom right
                for (int x = 0; x < xBound; x++) {
                    for (int y = 0; y < yBound; y++) {
                        newLayer[x][y] = tiles[x][y];
                    }
                }
                break;
            default: // This shouldn't ever be possible
                System.err.println("Error: invalid resize option");
                break;
        }

        tiles = newLayer;
    }
}
