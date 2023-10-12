package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * An object which contains a grid of buttons for each tile in a tileset image.
 */
public class Tileset extends JPanel {
    /** The array of all tiles from the tileset. */
    private Tile[] tiles;

    private int tileSize;
    private final int ROWS;
    private final int COLUMNS;
    private int currentTile;
    private int tilesetID;

    /**
     * Instantiates a tileset object containing all tiles of a specified size.
     *
     * @param tileSize The size of each tile in the tileset.
     * @param image The source image of the tileset.
     * @param tilesetID The ID of the tileset.
     */
    public Tileset(int tileSize, BufferedImage image, int tilesetID) {
        this.tileSize = tileSize;
        currentTile = 0;
        this.tilesetID = tilesetID;

        // Create a container for the tile buttons
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Create empty array of tiles
        ROWS = image.getHeight() / tileSize;
        COLUMNS = image.getWidth() / tileSize;
        tiles = new Tile[ROWS * COLUMNS];

        // Iterate over all tiles/sub-images
        int spriteIndex = 0;
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                BufferedImage subImage = image.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);

                // Check if the sub-image is empty
                boolean empty = true;
                for (int pixX = 0; (pixX < tileSize) && empty; pixX++) {
                    for (int pixY = 0; pixY < tileSize; pixY++) {
                        int alpha = (subImage.getRGB(pixX, pixY)>>24)&0xff;
                        if (alpha != 0) {
                            empty = false;
                            break;
                        }
                    }
                }

                if (empty) continue; // If the sub-image was empty, continue to the next one

                tiles[spriteIndex] = new Tile(subImage, spriteIndex, tilesetID);

                // Set the position of the tile button in the grid
                gc.gridx = x;
                gc.gridy = y;

                // Create button for the current tile
                JButton tileButton = new JButton();
                tileButton.setContentAreaFilled(false);
                tileButton.setIcon(new ImageIcon(tiles[spriteIndex].getSprite().getScaledInstance(
                        tileSize * 2, tileSize * 2, Image.SCALE_FAST
                        ))
                );

                int finalI = spriteIndex;
                tileButton.addActionListener(e -> {
                    currentTile = finalI;
                });

                // Set the button size to be double the size of the sprite
                tileButton.setPreferredSize(new Dimension(tileSize * 2, tileSize * 2));

                buttonContainer.add(tileButton, gc); // Add the tile button to the container of all tile buttons

                spriteIndex++;
            }
        }

        // If there were some tiles in the image which were completely empty, cull them from the end of the array.
        if (spriteIndex != ROWS * COLUMNS) {
            tiles = Arrays.copyOf(tiles, spriteIndex);
        }

        // Using a scroll pane bc it allows us to resize the tool panel.
        // TODO: figure out how to actually let it scroll.
        JScrollPane scrollPane = new JScrollPane(buttonContainer);
        add(scrollPane);

        revalidate();
        repaint();
    }

    public Tile getCurrentTile() {
        if (currentTile >= 0 && currentTile < tiles.length) {
            return tiles[currentTile];
        }

        return null;
    }

    public int getCurrentTileIndex() {
        return currentTile;
    }

    @Override
    public String toString() {
        return ("Tileset: " + tilesetID);
    }

}