package Components;

import Core.EditorWindow;
import Core.EditorWindow.EditorMode;
import Serial.LevelData;
import Serial.Tile;
import com.sun.deploy.panel.JavaPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
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
    private Point prevPoint;

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

        prevPoint = new Point(0, 0);
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

        boolean erase = (EDITOR.mode == EditorMode.ERASE);

        Point gridPos = screenToGrid(mousePos);
        int x = gridPos.x;
        int y = gridPos.y;

        // If the coordinates are within the bounds of the array
        if ((x >= 0 && x < width) && (y >= 0 && y < height)) {
            layers.get(currentLayer)[x][y] = erase ? null : EDITOR.getCurrentTile();
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

//        if (currentLayer >= 0) {
//            // Reset tiles
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    layers.get(currentLayer)[x][y] = null;
//                }
//            }
//        }

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

    public void exportLevelFile() throws IOException {
        int val = fileChooser.showSaveDialog(null);

        if (val != JFileChooser.APPROVE_OPTION) return;

        String levelName = JOptionPane.showInputDialog(
                null,
                "Enter a name for the level:", "Layer Name",
                JOptionPane.PLAIN_MESSAGE
        );

        if (levelName == null || levelName.trim().isEmpty()) return;

        LevelData level = new LevelData(layers, levelName, width, height);

        File fileToSave = fileChooser.getSelectedFile();
        ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(fileToSave.toPath()));

        outputStream.writeObject(level);

        outputStream.close();
    }

    public void editCanvasSize() {
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
            } catch (NumberFormatException ignored) {}

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

        for (int i = 0; i < layers.size(); i++) {
            Tile[][] currentLayer = layers.get(i);
            Tile[][] newLayer = new Tile[newWidth][newHeight];

            int xBound = Math.min(width, newWidth);
            int yBound = Math.min(height, newHeight);

            for (int x = 0; x < xBound; x++) {
                for (int y = 0; y < yBound; y++) {
                    newLayer[x][y] = currentLayer[x][y];
                }
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
        System.out.println("pressed");
        if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
            prevPoint = e.getPoint(); // Store the mouse's current position
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (EDITOR.mode == EditorMode.SELECT) {
                selectTile(e.getPoint());
            } else {
               paintTile(e.getPoint());
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
            xOffset = e.getPoint().x - prevPoint.x;
            yOffset = e.getPoint().y - prevPoint.y;

            repaint(); // Repaint the viewport
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            paintTile(e.getPoint());
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