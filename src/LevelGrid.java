import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The viewport for the level editor. Allows the user to zoom and pan around the level's grid.
 */
public class LevelGrid extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    /** The level's grid of tiles. */
    private Tile[][] tiles;

    /** The width/height of the level's grid. */
    private int width, height;

    /** The zoom scale of viewport. The higher the value, the more zoomed in the grid appears. */
    private double scale;
    private final double MIN_SCALE = 0.3;
    private final double MAX_SCALE = 3;

    /** The previous location that the mouse was last clicked on the viewport. Used to track panning. */
    private Point prevPoint;

    /** The x/y offset of the viewport during panning. */
    private int xOffset, yOffset;

    /** The current x/y position of the viewport. */
    private int xPosition, yPosition;

    /** The button used to reset the viewport to its original position and scale. */
    private JButton resetPosition;

    /**
     * Initializes a new instance of the level editor's viewport.
     *
     * @param width The number of tiles in the horizontal direction.
     * @param height The number of tiles in the vertical direction.
     */
    public LevelGrid(int width, int height) {
        // Initialize values
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
        scale = 1.0;
        prevPoint = new Point(0, 0);
        xOffset = 0;
        yOffset = 0;
        xPosition = 0;
        yPosition = 0;

        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        // Set up button to reset position and scale of level preview
        resetPosition = new JButton("Reset Position");
        resetPosition.addActionListener(e -> {
            xPosition = 0;
            yPosition = 0;
            xOffset = 0;
            yOffset = 0;
            scale = 1;
            repaint();
        });

        add(resetPosition); // Add the button to the panel

        // Add the necessary mouse input listeners for moving the viewport
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Set up tile objects
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile();
            }
        }
    }

    /**
     * Draws the grid of tiles to the viewport.
     *
     * @param g2 The Graphics2D object to handle the graphics resources.
     */
    public void drawTiles(Graphics2D g2) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2.setColor(Color.DARK_GRAY);

                // Draw a rectangle for each tile
                // TODO: Optimize by removing redundant line draws (lines where rectangles are drawn over each other)
                g2.drawRect((int) (x * 16 * scale) + xOffset + xPosition,
                        (int) (y * 16 * scale) + yOffset + yPosition,
                        (int) (16 * scale),
                        (int) (16 * scale)
                );
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear panel

        // Draw tiles
        Graphics2D g2 = (Graphics2D) g;
        drawTiles(g2);

        resetPosition.paint(g); // Draw reset position button

        g2.dispose(); // Dispose of graphics resources (this has to be last)
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scale -= 0.1 * e.getWheelRotation(); // Change the scale based on scroll direction
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale)); // Clamp the scale factor

        repaint(); // Repaint the viewport
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            prevPoint = e.getPoint(); // Store the mouse's current position
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
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
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            // Calculate the difference between the mouse's current position
            // and where the middle mouse button was originally pressed.
            xOffset = e.getPoint().x - prevPoint.x;
            yOffset = e.getPoint().y - prevPoint.y;

            repaint(); // Repaint the viewport
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}