import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * An object class that uses a sprite from a specified tile set. It comprises the grid that is the basis of a level.
 */
public class Tile {
    /** The ID of the tileset this tile uses. */
    private int tilesetID;

    /** The ID of the sprite that the tile uses from its tileset. */
    private int spriteID;

    public boolean exists;

    public BufferedImage sprite;

    public Tile() {
        exists = false;
        // TODO: Add stuff
    }

    public void setSprite(BufferedImage sprite) {
        exists = (sprite != null);
        this.sprite = sprite;
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}