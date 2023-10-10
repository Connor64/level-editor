import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An object class that uses a sprite from a specified tile set. It comprises the grid that is the basis of a level.
 */
public class Tile {
    /** The ID of the tileset this tile uses. */
    private int tilesetID;

    /** The ID of the sprite that the tile uses from its tileset. */
    private int spriteID;

    public BufferedImage sprite;

    public boolean collidable;

    /**
     * Instantiates a tile object with the specified sprite.
     *
     * @param sprite The sprite object.
     * @param spriteID The index of the sprite in its tileset.
     * @param tilesetID The ID representing which tileset the sprite comes from.
     */
    public Tile(BufferedImage sprite, int spriteID, int tilesetID) {
        this.sprite = sprite;
        this.spriteID = spriteID;
        this.tilesetID = tilesetID;
        collidable = false;
    }

    /**
     * @return The sprite of the tile.
     */
    public BufferedImage getSprite() {
        return sprite;
    }

    /**
     * Draws the tile's sprite on screen at the given location on the specified canvas.
     *
     * @param g2 The Graphics2D object to handle the graphics resources.
     * @param x The x coordinate of the top-left corner of the tile.
     * @param y The y coordinate of the top-left corner of the tile.
     * @param scaledSize The width/height of the tile that will be taken up on screen.
     * @param canvas The component which the tile will be drawn on.
     */
    public void draw(Graphics2D g2, int x, int y, int scaledSize, JComponent canvas) {
        // Draw the tile's (scaled) sprite
        g2.drawImage(sprite.getScaledInstance(scaledSize, scaledSize, Image.SCALE_FAST), x, y, canvas);
    }
}