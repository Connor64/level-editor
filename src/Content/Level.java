package Content;

import Core.EditorWindow;
import History.*;
import UIComponents.LevelCanvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class Level {
    private ArrayList<Layer> layers;
    private Layer currentLayer;

    private Stack<HistoricAction> actionHistory;
    private ArrayList<TileEdit> currentTileAction;
    private int currentActionIndex = -1;

    private int width, height;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;

        layers = new ArrayList<>();
        currentLayer = null;

        actionHistory = new Stack<>();
        currentTileAction = new ArrayList<>();
    }

    /**
     * Draws all the layers of the level.
     *
     * @param g2 The graphics resource.
     * @param xPos The offset X location of the level within the viewport.
     * @param yPos The offset Y location of the level within the viewport.
     * @param scaledSize The size which the viewport has scaled the level.
     */
    public void draw(Graphics2D g2, int xPos, int yPos, double scaledSize) {
        int scaledSizeInt = (int) Math.ceil(scaledSize);
        LevelCanvas canvas = EditorWindow.INSTANCE.getCanvas();

        for (Layer layer : layers) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (layer.getTiles()[x][y] == null) continue;

                    // Create filled square if it exists
                    g2.setColor(Color.DARK_GRAY);

                    int newX = (int) (x * scaledSize) + xPos;
                    int newY = (int) (y * scaledSize) + yPos;

                    // Draw current tile
                    layer.getTiles()[x][y].draw(g2, newX, newY, scaledSizeInt, canvas);
                }
            }
        }
    }

    /**
     * Paints the tile at the given mouse coordinates.
     *
     * @param mousePos The current location of the mouse in grid coordinates.
     * @param prevPos The previous location of the mouse in grid coordinates.
     * @param tile The type of tile which will be painted.
     */
    public void paintTiles(Point mousePos, Point prevPos, Tile tile) {
        if (layers.isEmpty() || (currentLayer == null)) return;

        int x1 = mousePos.x;
        int y1 = mousePos.y;

        // If the coordinates aren't within the bounds of the array, return
        if ((x1 < 0 || x1 >= width) || (y1 < 0 || y1 >= height)) return;

        Tile[][] tiles = currentLayer.getTiles();

        // If only one tile needs to be painted
        if ((prevPos == null) || mousePos.equals(prevPos)) {

            // If the tile already matches, return
            if (tiles[x1][y1] == tile) return;

            currentTileAction.add(new TileEdit(x1, y1, tiles[x1][y1], tile));
            tiles[x1][y1] = tile;
            return;
        }

        // Algorithm below is a modified Bresenham line-drawing algorithm as specified here:
        // http://eugen.dedu.free.fr/projects/bresenham/
        int x0 = prevPos.x;
        int y0 = prevPos.y;

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

                // Only apply and store the change if the tile doesn't match the new one
                if (tiles[x][y] != tile) {
                    currentTileAction.add(new TileEdit(x, y, tiles[x][y], tile));
                    tiles[x][y] = tile;
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

    /**
     * Resizes the level and all its layers.
     *
     * @param newWidth The new height of the level.
     * @param newHeight The new width of the level.
     * @param resizeDirection The direction the level will expand.
     * @param record Whether the change will be recorded in the history of changes.
     */
    public void resize(int newWidth, int newHeight, int resizeDirection, boolean record) {
        if ((newWidth == width) && (newHeight == height)) return; // Don't bother if it's the same size

        for (Layer layer : layers) {
            layer.resize(newWidth, newHeight, resizeDirection);
        }

        if (record) {
            addAction(new ResizeAction(this, width, height, newWidth, newHeight, resizeDirection));
        }

        width = newWidth;
        height = newHeight;

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Creates a new layer with the specified name.
     *
     * @param layerName The name of the new layer.
     */
    public void addLayer(String layerName) {
        if (layerName == null || layerName.trim().isEmpty()) {
            layerName = "layer " + layers.size() + 1;
        }

        addLayer(new Layer(width, height, layerName));
    }

    /**
     * Adds a layer to the level.
     *
     * @param layer The layer to be added.
     */
    public void addLayer(Layer layer) {
        if (layers.contains(layer)) return;

        // Save the previous state of the layers
        Layer[] oldLayers = layers.toArray(new Layer[0]);

        layers.add(layer);
        if (currentLayer == null) {
            currentLayer = layer;
        }

        // Add edit to the history of changes
        addAction(new LayerAction(this, oldLayers));

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Deletes the specified layer.
     *
     * @param layer The layer to be deleted.
     */
    public void deleteLayer(Layer layer) {
        if (layer == currentLayer) {
            currentLayer = null;
        }

        // Save the previous state of the layers
        Layer[] oldLayers = layers.toArray(new Layer[0]);

        layers.remove(layer);

        // Add edit to the history of changes
        addAction(new LayerAction(this, oldLayers));

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Shifts the specified layer up or down in the layer stack.
     *
     * @param layer The layer to be moved.
     * @param up Dictates which direction the layer will move (true = up, false = down).
     */
    public void shiftLayer(Layer layer, boolean up) {
        int index = layers.indexOf(layer);

        if (index == -1) {
            System.err.println("Layer does not exist!");
            EditorWindow.INSTANCE.refreshComponents();
            return;
        }

        if ((index == 0 && !up) || (index >= layers.size() - 1 && up)) return;

        int newIndex = up ? (index + 1) : (index - 1);

        System.out.println("old index: " + index);
        System.out.println("new index: " + newIndex);

        // Save the previous state of the layers
        Layer[] oldLayers = layers.toArray(new Layer[0]);

        // Swap the layers
        Layer prevLayer = layers.get(newIndex);
        layers.set(newIndex, layer);
        layers.set(index, prevLayer);

        // Add edit to the history of changes
        addAction(new LayerAction(this, oldLayers));

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Renames the specified layer. Should not be called to initialize the name.
     *
     * @param layer The layer to be renamed.
     * @param newName The new name of the layer.
     */
    public void renameLayer(Layer layer, String newName) {
        if (!layers.contains(layer)) return;

        String oldName = layer.getName();
        layer.setName(newName);
        addAction(new LayerRenameAction(layer, oldName, newName));

        // Add edit to the history of changes
        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Undoes the previous action executed by the user.
     */
    public void undo() {
        if (actionHistory.isEmpty() || (currentActionIndex < 0)) return;

        actionHistory.get(currentActionIndex).undoAction();
        currentActionIndex--;

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Redoes a previously undone action (if it exists).
     */
    public void redo() {
        if (actionHistory.isEmpty() || (currentActionIndex >= actionHistory.size() - 1)) return;

        actionHistory.get(currentActionIndex + 1).redoAction();
        currentActionIndex++;

        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * Completes the most recent tile action and adds it to the stack.
     */
    public void completeTileAction() {
        if (!currentTileAction.isEmpty()) {
            TileEdit[] actions = currentTileAction.toArray(new TileEdit[0]);

            addAction(new TileAction(currentLayer, actions));

            currentTileAction = new ArrayList<>();
        }
    }

    /**
     * Adds the specified action to the history of actions, removing any overwritten undoes.
     *
     * @param action The action to be recorded.
     */
    private void addAction(HistoricAction action) {
        // If any actions were undone, remove them from the stack
        if (currentActionIndex < actionHistory.size() - 1) {
            for (int i = actionHistory.size() - 1; i > currentActionIndex; i--) {
                actionHistory.pop();
            }
        }

        actionHistory.add(action);
        currentActionIndex++;
    }

    /**
     * @return The selected layer for this level.
     */
    public Layer getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Sets the selected layer for this level.
     *
     * @param layer The layer to be selected.
     */
    public void setCurrentLayer(Layer layer) {
        if ((layer != null) && !layers.contains(layer)) {
            System.err.println("Error: Layer " + layer.getName() + " doesn't exist!");
            return;
        }

        currentLayer = layer;
        EditorWindow.INSTANCE.refreshComponents();
    }

    /**
     * @return The list of all layers in this level.
     */
    public ArrayList<Layer> getLayers() {
        return layers;
    }

    /**
     * @return The number of all layers in this level.
     */
    public int getNumLayers() {
        return layers.size();
    }

    /**
     * @return The width of this level.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The height of this level.
     */
    public int getHeight() {
        return height;
    }
}