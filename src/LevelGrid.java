import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LevelGrid extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    private Tile[][] tiles;
    private int width, height;
    private double scale;
    private final double MIN_SCALE = 0.3;
    private final double MAX_SCALE = 3;
    private Point prevPoint;
    private int xOffset, yOffset, xPosition, yPosition;
    private JButton resetPosition;

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

        add(resetPosition);

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

    public void drawTiles(Graphics2D g2) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect((int) (x * 16 * scale) + xOffset + xPosition,
                        (int) (y * 16 * scale) + yOffset + yPosition,
                        (int) (16 * scale),
                        (int) (16 * scale)
                );
            }
        }
    }

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
        scale -= 0.1 * e.getWheelRotation();
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            prevPoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            xPosition += xOffset;
            yPosition += yOffset;
            xOffset = 0;
            yOffset = 0;
            repaint();
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
        if (SwingUtilities.isMiddleMouseButton(e)) {
            xOffset = e.getPoint().x - prevPoint.x;
            yOffset = e.getPoint().y - prevPoint.y;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}