package Components;

import Core.EditorWindow;
import Core.EditorWindow.EditorMode;
import Serial.LevelData;
import Serial.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * The canvas for the level editor. Allows the user to zoom and pan around the level's grid and paint tiles.
 */
public class LevelCanvas extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener, KeyListener {

    /**
     * The level's grid of tiles.
     */
    private ArrayList<Tile[][]> layers;
    public int currentLayer;

    /**
     * The width/height of the level's grid.
     */
    private int width, height;

    /**
     * The size, in pixels, of each tile in the level.
     */
    private final int TILE_SIZE = 16;

    /**
     * The zoom scale of viewport. The higher the value, the more zoomed in the grid appears.
     */
    private double scale;
    private final double MIN_SCALE = 0.3;
    private final double MAX_SCALE = 3;

    /**
     * The previous location that the mouse was last clicked on the viewport. Used to track panning.
     */
    private Point prevPressPoint;

    /**
     * The previous position that was painted on the canvas. Used to ensure tiles aren't skipped when painting.
     */
    private Point prevPaintPoint;

    /**
     * The x/y offset of the viewport during panning.
     */
    private int xOffset, yOffset;

    /**
     * The current x/y position of the viewport.
     */
    private int xPosition, yPosition;

    private final EditorWindow EDITOR;

    private boolean ctrlSelect;
    private int selectX, selectY;

    private JFileChooser fileChooser;

    private JPanel resizeDirectionPanel;
    private int resizeOption = 4;

    /**
     * Initializes a new instance of the level editor's viewport.
     *
     * @param width  The number of tiles in the horizontal direction.
     * @param height The number of tiles in the vertical direction.
     */
    public LevelCanvas(int width, int height, EditorWindow editor) {
        // Initialize values
        this.width = width;
        this.height = height;
        layers = new ArrayList<>();
        currentLayer = -1;

        prevPressPoint = new Point(0, 0);
        prevPaintPoint = null;
        scale = 1.0;
        xOffset = 0;
        yOffset = 0;
        xPosition = 0;
        yPosition = 0;

        ctrlSelect = false;
        selectX = -1;
        selectY = -1;

        EDITOR = editor;

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Level File (*.lvl, *.level)", "lvl", "level"
        );
        fileChooser.setFileFilter(filter);

        // Add the necessary mouse input listeners for moving the viewport
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        resizeDirectionPanel = new JPanel();
        resizeDirectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Add resize direction buttons to the panel
        try {
            int index = 0;
            for (int y = 0; y < 3; y++) {
                gc.gridy = y;
                for (int x = 0; x < 3; x++) {
                    int finalIndex = index;
                    BufferedImage icon = ImageIO.read(new File("icons/resize_icon_" + index + ".png"));

                    JButton button = new JButton();
                    button.setBackground((index == resizeOption) ? EDITOR.TOGGLE_COLOR : EDITOR.BUTTON_COLOR);
                    button.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_FAST)));
                    button.setPreferredSize(new Dimension(32, 32));
                    button.addActionListener(e -> {
                        for (int i = 0; i < 9; i++) {
                            resizeDirectionPanel.getComponent(i).setBackground(EDITOR.BUTTON_COLOR);
                        }
                        button.setBackground(EDITOR.TOGGLE_COLOR);
                        resizeOption = finalIndex;
                    });

                    gc.gridx = x;
                    resizeDirectionPanel.add(button, gc);
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Draws the grid of tiles to the viewport.
     *
     * @param g2 The Graphics2D object to handle the graphics resources.
     */
    public void drawGrid(Graphics2D g2) {
        if (currentLayer < 0) return;

        double scaledSize = TILE_SIZE * scale;

        int yPos = yPosition + yOffset;
        int xPos = xPosition + xOffset;
        int scaledSizeInt = (int) Math.ceil(scaledSize);

        // Draw vertical grid lines
        for (int x = 0; x <= width; x++) {
            int xPos2 = (int) (x * scaledSize) + xPos;
            g2.drawLine(xPos2, yPos, xPos2, yPos + (int) (height * scaledSize));
        }

        // Draw horizontal grid lines
        for (int y = 0; y <= height; y++) {
            int yPos2 = (int) (y * scaledSize) + yPos;
            g2.drawLine(xPos, yPos2, xPos + (int) (width * scaledSize), yPos2);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (layers.get(currentLayer)[x][y] == null) continue;

                // Create filled square if it exists
                g2.setColor(Color.DARK_GRAY);

                int newX = (int) (x * scaledSize) + xPos;
                int newY = (int) (y * scaledSize) + yPos;

                // Draw current tile
                layers.get(currentLayer)[x][y].draw(g2, newX, newY, scaledSizeInt, this);
            }
        }

        if ((selectX != -1) && (selectY != -1)) {
            g2.setColor(Color.RED);

            // Draw selection icon over tile
            g2.drawRect(
                    (int) (selectX * scaledSize) + xPos,
                    (int) (selectY * scaledSize) + yPos,
                    scaledSizeInt, scaledSizeInt
            );
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear panel

        // Draw tiles
        Graphics2D g2 = (Graphics2D) g;
        drawGrid(g2);

        g.dispose(); // Dispose of graphics resources (this has to be last)
    }

    /**
     * Paints the tile at the given mouse coordinates.
     *
     * @param mousePos The mouse's coordinates within the canvas (in pixels).
     */
    private void paintTile(Point mousePos) {
        if ((EDITOR.mode == EditorMode.SELECT) || (currentLayer < 0)) return;

        Tile tileToPaint = (EDITOR.mode == EditorMode.ERASE) ? null : EDITOR.getCurrentTile();

        Point gridPos = screenToGrid(mousePos);
        Point prevPos = null;
        if (prevPaintPoint != null) prevPos = screenToGrid(prevPaintPoint);

        int x1 = gridPos.x;
        int y1 = gridPos.y;

        if ((x1 < 0 || x1 >= width) || (y1 < 0 || y1 >= height)) return;

        Tile[][] layer = layers.get(currentLayer);

        if ((prevPos == null) || gridPos.equals(prevPos)) {
            // If the coordinates are within the bounds of the array
            if (layer[x1][y1] == tileToPaint) return; // Don't bother repainting if the tile doesn't change

            layer[x1][y1] = tileToPaint;
        } else {
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
                    layer[x][y] = tileToPaint;

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

        repaint();
    }

    /**
     * Resets the position and scale of the canvas grid.
     */
    public void resetCanvasPosition() {
        // Reset "camera" position values
        xPosition = 0;
        yPosition = 0;
        xOffset = 0;
        yOffset = 0;
        scale = 1;

        repaint();
    }

    public void addLayer() {
        layers.add(new Tile[width][height]);
        currentLayer = Math.max(0, currentLayer + 1);
        repaint();
    }

    public void removeCurrentLayer() {
        if (currentLayer < 0) return;

        layers.remove(currentLayer);
        currentLayer = Math.min(layers.size() - 1, currentLayer);
        repaint();
    }

    private void selectTile(Point point) {
        Point pos = screenToGrid(point);

        System.out.println("position: " + pos);

        if (pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height) {
            selectX = pos.x;
            selectY = pos.y;
        } else {
            selectX = -1;
            selectY = -1;
        }

        repaint();
    }

    /**
     * Converts the given mouse coordinates to grid coordinates. The coordinates returned may exceed level bounds.
     *
     * @param mousePos The mouse's coordinates within the canvas (in pixels).
     * @return The grid coordinates that correspond with the mouse position.
     */
    private Point screenToGrid(Point mousePos) {
        // Get the x and y coordinates of the tile within the array
        int x = (int) Math.floor((mousePos.x - xPosition) / (TILE_SIZE * scale));
        int y = (int) Math.floor((mousePos.y - yPosition) / (TILE_SIZE * scale));

        return new Point(x, y);
    }

    /**
     * Prompts the user to name the level and choose where to save the file.
     *
     * @throws IOException If the file is unable to be written.
     */
    public void exportLevelFile() throws IOException {
        String levelName = JOptionPane.showInputDialog(
                null,
                "Enter a name for the level (this is not the file name!):", "Layer Name",
                JOptionPane.PLAIN_MESSAGE
        );

        int val = fileChooser.showSaveDialog(null);

        if (val != JFileChooser.APPROVE_OPTION) return;

        if (levelName == null || levelName.trim().isEmpty()) return;

        LevelData level = new LevelData(layers, levelName, width, height);

        File fileToSave = fileChooser.getSelectedFile();
        ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(fileToSave.toPath()));

        outputStream.writeObject(level);

        outputStream.close();
    }

    /**
     * Prompts to the user to resize the level canvas and choose which direction it grows/shrinks
     */
    public void resizeCanvas() {
        // Create panel containing controls
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Create width label and field
        JLabel widthLabel = new JLabel("Width:");
        JTextField widthField = new JTextField(4);
        widthField.setText(String.valueOf(width));

        // Create height label and field
        JLabel heightLabel = new JLabel("Height:");
        JTextField heightField = new JTextField(4);
        heightField.setText(String.valueOf(height));

        // Add labels and fields to panel
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(2, 2, 2, 2);

        sizePanel.add(widthLabel, gc);
        gc.gridx = 1;
        sizePanel.add(widthField, gc);

        gc.gridy = 1;
        sizePanel.add(heightField, gc);
        gc.gridx = 0;
        sizePanel.add(heightLabel, gc);

        gc.gridwidth = 2;
        gc.gridx = 0;
        gc.gridy = 2;
        sizePanel.add(resizeDirectionPanel, gc);

        int newWidth = 0;
        int newHeight = 0;

        // Loop over prompt until user cancels or gives valid input
        while (true) {
            // Prompt the user for a tile size
            int decision = JOptionPane.showConfirmDialog(
                    null,
                    sizePanel,
                    "Choose a new level size",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (decision != JOptionPane.OK_OPTION) return;

            // Parse input
            try {
                newWidth = Integer.parseInt(widthField.getText());
                newHeight = Integer.parseInt(heightField.getText());
            } catch (NumberFormatException ignored) {
            }

            // If input is valid, exit loop
            if ((newWidth > 0) && (newHeight > 0)) {
                break;
            }

            // Prompt user for valid input
            JOptionPane.showMessageDialog(null,
                    "Input must be an integer greater than 0!",
                    "Invalid Input!", JOptionPane.ERROR_MESSAGE
            );

            // Reset fields
            widthField.setText(String.valueOf(width));
            heightField.setText(String.valueOf(height));
            newWidth = 0;
            newHeight = 0;
        }

        // If the size wasn't changed
        if ((newWidth == width) && (newHeight == height)) return;

        for (int i = 0; i < layers.size(); i++) {
            Tile[][] newLayer = new Tile[newWidth][newHeight];
            Tile[][] oldLayer = layers.get(i);

            int xBound = Math.min(width, newWidth);
            int yBound = Math.min(height, newHeight);
            int xMaxBound = Math.max(width, newWidth);
            int yMaxBound = Math.max(height, newHeight);

            int xOffset = (xMaxBound - xBound) / 2;
            int yOffset = (yMaxBound - yBound) / 2;

            int newXOffset = (newWidth > width) ? xOffset : 0;
            int newYOffset = (newHeight > height) ? yOffset : 0;
            int oldXOffset = (newWidth < width) ? xOffset : 0;
            int oldYOffset = (newHeight < height) ? yOffset : 0;

            // Resize level based on option selected
            switch (resizeOption) {
                case 0: // Top left
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[newWidth - 1 - x][newHeight - 1 - y] = oldLayer[width - 1 - x][height - 1 - y];
                        }
                    }
                    break;
                case 1: // Up
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x + newXOffset][newHeight - 1 - y] = oldLayer[x + oldXOffset][height - 1 - y];
                        }
                    }
                    break;
                case 2: // Top right
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x][newHeight - 1 - y] = oldLayer[x][height - 1 - y];
                        }
                    }
                    break;
                case 3: // Left
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[newWidth - 1 - x][y + newYOffset] = oldLayer[width - 1 - x][y + oldYOffset];
                        }
                    }
                    break;
                case 4: // Center
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x + newXOffset][y + newYOffset] = oldLayer[x + oldXOffset][y + oldYOffset];
                        }
                    }
                    break;
                case 5: // Right
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x][y + newYOffset] = oldLayer[x][y + oldYOffset];
                        }
                    }
                    break;
                case 6: // Bottom left
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[newWidth - 1 - x][y] = oldLayer[width - 1 - x][y];
                        }
                    }
                    break;
                case 7: // Down
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x + newXOffset][y] = oldLayer[x + oldXOffset][y];
                        }
                    }
                    break;
                case 8: // Bottom right
                    for (int x = 0; x < xBound; x++) {
                        for (int y = 0; y < yBound; y++) {
                            newLayer[x][y] = oldLayer[x][y];
                        }
                    }
                    break;
                default: // This shouldn't ever be possible
                    System.err.println("Error: invalid resize option");
                    resizeOption = 4;
                    break;
            }

            layers.set(i, newLayer);
        }

        width = newWidth;
        height = newHeight;

        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scale -= 0.1 * e.getWheelRotation(); // Change the scale based on scroll direction
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale)); // Clamp the scale factor

        repaint(); // Repaint the viewport
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
            prevPressPoint = e.getPoint(); // Store the mouse's current position
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (EDITOR.mode == EditorMode.SELECT) {
                selectTile(e.getPoint());
            } else {
                paintTile(e.getPoint());
                prevPaintPoint = e.getPoint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
            // Finalize the viewport's position (i.e., the displacement is saved)
            xPosition += xOffset;
            yPosition += yOffset;

            // Reset the offset value
            xOffset = 0;
            yOffset = 0;

            repaint(); // Repaint the viewport
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            prevPaintPoint = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
            // Calculate the difference between the mouse's current position
            // and where the middle mouse button was originally pressed.
            xOffset = e.getPoint().x - prevPressPoint.x;
            yOffset = e.getPoint().y - prevPressPoint.y;

            repaint(); // Repaint the viewport
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            paintTile(e.getPoint());
            prevPaintPoint = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlSelect = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlSelect = false;
        }
    }
}