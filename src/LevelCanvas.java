import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The canvas for the level editor. Allows the user to zoom and pan around the level's grid and paint tiles.
 */
public class LevelCanvas extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener, KeyListener {

    /**
     * The level's grid of tiles.
     */
    private Tile[][] tiles;

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

    /**
     * The button used to reset the viewport to its original position and scale and empties all tiles.
     */
    private JButton resetButton;

    private final EditorWindow PARENT_WINDOW;

    /**
     * Initializes a new instance of the level editor's viewport.
     *
     * @param width  The number of tiles in the horizontal direction.
     * @param height The number of tiles in the vertical direction.
     */
    public LevelCanvas(int width, int height, EditorWindow parent) {
        // Initialize values
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
        prevPoint = new Point(0, 0);
        scale = 1.0;
        xOffset = 0;
        yOffset = 0;
        xPosition = 0;
        yPosition = 0;

        PARENT_WINDOW = parent;

        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        // Set up button to reset position and scale of level preview
        resetButton = new JButton("Reset Position");
        resetButton.addActionListener(e -> {
            resetCanvas();
        });

        add(resetButton); // Add the button to the panel

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
        double scaledSize = TILE_SIZE * scale;

        int yPos = yPosition + yOffset;
        int xPos = xPosition + xOffset;

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
                if (tiles[x][y] == null) continue;

                // Create filled square if it exists
                g2.setColor(Color.DARK_GRAY);

                int newX = (int) (x * scaledSize) + xPos;
                int newY = (int) (y * scaledSize) + yPos;
                int scaledSizeInt = (int) Math.ceil(scaledSize);

                // Draw current tile
                tiles[x][y].draw(g2, newX, newY, scaledSizeInt, this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear panel

        // Draw tiles
        Graphics2D g2 = (Graphics2D) g;
        drawGrid(g2);

        resetButton.paint(g); // Draw reset position button

        g2.dispose(); // Dispose of graphics resources (this has to be last)
    }

    /**
     * Paints the tile at the given mouse coordinates.
     *
     * @param xCoord The mouse's x coordinates within the canvas (in pixels).
     * @param yCoord The mouse's y coordinates within the canvas (in pixels).
     */
    private void paintTile(int xCoord, int yCoord) {

        // Get the x and y coordinates of the tile within the array
        int x = (int) Math.floor((xCoord - xPosition) / (TILE_SIZE * scale));
        int y = (int) Math.floor((yCoord - yPosition) / (TILE_SIZE * scale));

        // If the coordinates are within the bounds of the array
        if ((x >= 0 && x < width) && (y >= 0 && y < height)) {
            tiles[x][y] = PARENT_WINDOW.getCurrentTile();
        }

        repaint();
    }

    /**
     * Resets the position, scale, and all tiles currently within the level grid.
     */
    public void resetCanvas() {
        // Reset "camera" position values
        xPosition = 0;
        yPosition = 0;
        xOffset = 0;
        yOffset = 0;
        scale = 1;

        // Reset tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = null;
            }
        }

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
            prevPoint = e.getPoint(); // Store the mouse's current position
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            paintTile(e.getX(), e.getY());
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
            paintTile(e.getX(), e.getY());
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

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}