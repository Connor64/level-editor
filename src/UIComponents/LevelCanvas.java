package UIComponents;

import Core.EditorWindow;
import Core.EditorConstants.EditorMode;
import Core.LevelManager;
import Content.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The canvas for the level editor. Allows the user to zoom and pan around the level's grid and paint tiles.
 */
public class LevelCanvas extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

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
    private final LevelManager LEVEL_MANAGER;

    private boolean ctrlSelect;
    private int selectX, selectY;

    private JFileChooser fileChooser;

    /**
     * Initializes a new instance of the level editor's viewport.
     */
    public LevelCanvas() {
        // Initialize values
        EDITOR = EditorWindow.INSTANCE;
        LEVEL_MANAGER = EDITOR.getLevelManager();

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

        // Add the necessary mouse input listeners for moving the viewport
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LEVEL_MANAGER.getCurrentLevel().undo();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redo");
        getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LEVEL_MANAGER.getCurrentLevel().redo();
            }
        });
    }

    /**
     * Draws the grid of tiles to the viewport.
     *
     * @param g2 The Graphics2D object to handle the graphics resources.
     */
    public void drawGrid(Graphics2D g2) {
        if (LEVEL_MANAGER.getCurrentLevel().getNumLayers() <= 0) return;

        double scaledSize = TILE_SIZE * scale;

        int yPos = yPosition + yOffset;
        int xPos = xPosition + xOffset;
        int scaledSizeInt = (int) Math.ceil(scaledSize);

        // Draw vertical grid lines
        for (int x = 0; x <= LEVEL_MANAGER.getCurrentLevel().getWidth(); x++) {
            int xPos2 = (int) (x * scaledSize) + xPos;
            g2.drawLine(xPos2, yPos, xPos2, yPos + (int) (LEVEL_MANAGER.getCurrentLevel().getHeight() * scaledSize));
        }

        // Draw horizontal grid lines
        for (int y = 0; y <= LEVEL_MANAGER.getCurrentLevel().getHeight(); y++) {
            int yPos2 = (int) (y * scaledSize) + yPos;
            g2.drawLine(xPos, yPos2, xPos + (int) (LEVEL_MANAGER.getCurrentLevel().getWidth() * scaledSize), yPos2);
        }

        LEVEL_MANAGER.getCurrentLevel().draw(g2, xPos, yPos, scaledSize);

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

    private void selectTile(Point point) {
        Point pos = screenToGrid(point);

        if ((pos.x >= 0 && pos.x < LEVEL_MANAGER.getCurrentLevel().getWidth()) &&
                (pos.y >= 0 && pos.y < LEVEL_MANAGER.getCurrentLevel().getHeight())) {
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
        if (mousePos == null) return null;

        // Get the x and y coordinates of the tile within the array
        int x = (int) Math.floor((mousePos.x - xPosition) / (TILE_SIZE * scale));
        int y = (int) Math.floor((mousePos.y - yPosition) / (TILE_SIZE * scale));

        return new Point(x, y);
    }

    private void paintLevel(Point point) {
        if ((EDITOR.mode == EditorMode.DRAW) || (EDITOR.mode == EditorMode.ERASE)) {
            Tile tileToPaint = (EDITOR.mode == EditorMode.ERASE) ? null : EDITOR.getCurrentTile();

            LEVEL_MANAGER.getCurrentLevel().paintTiles(screenToGrid(point), screenToGrid(prevPaintPoint), tileToPaint);

            repaint();
        }
        prevPaintPoint = point;
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
                paintLevel(e.getPoint());
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
            LEVEL_MANAGER.getCurrentLevel().completeTileAction();

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
            paintLevel(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}