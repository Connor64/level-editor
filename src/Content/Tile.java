package Content;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;

/**
 * An object class that uses a sprite from a specified tile set. It comprises the grid that is the basis of a level.
 */
public class Tile implements Serializable {
    private static final long serialVersionUID = 2L;

    /** The ID of the tileset this tile uses. */
    private String tilesetID;

    /** The ID of the sprite that the tile uses from its tileset. */
    private int spriteIndex;

    public boolean collidable;

    private transient BufferedImage sprite;
    public transient HashMap<Integer, Image> scaledSprites;

    /**
     * Instantiates a tile object with the specified sprite.
     *
     * @param sprite The sprite object.
     * @param spriteIndex The index of the sprite in its tileset.
     * @param tilesetID The ID representing which tileset the sprite comes from.
     */
    public Tile(BufferedImage sprite, int spriteIndex, String tilesetID) {
        this.sprite = sprite;
        this.spriteIndex = spriteIndex;
        this.tilesetID = tilesetID;
        collidable = false;

        scaledSprites = new HashMap<>();
    }

    public Tile(Tile tile) {
        if (tile == null) return;
        sprite = tile.sprite;
        spriteIndex = tile.spriteIndex;
        tilesetID = tile.tilesetID;
        collidable = tile.collidable;
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
        Image scaledSprite = scaledSprites.get(scaledSize);

        if (scaledSprite == null) {
            scaledSprites.put(scaledSize, sprite.getScaledInstance(scaledSize, scaledSize, Image.SCALE_FAST));
            scaledSprite = scaledSprites.get(scaledSize);
        }

        // Draw the tile's (scaled) sprite
        g2.drawImage(scaledSprite, x, y, canvas);
    }

    @Override
    public String toString() {
        return ("TileIndex: " + spriteIndex + "   TilesetID: " + tilesetID);
    }
}