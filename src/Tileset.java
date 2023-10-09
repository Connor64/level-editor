import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tileset extends JPanel {
    private BufferedImage[] tileSprites;

    private int tileSize;
    private final int ROWS;
    private final int COLUMNS;

    public Tileset(int tileSize, BufferedImage image) {
        this.tileSize = tileSize;

        ROWS = image.getHeight() / tileSize;
        COLUMNS = image.getWidth() / tileSize;

        setLayout(new GridLayout(ROWS, COLUMNS));

        tileSprites = new BufferedImage[ROWS * COLUMNS];

        int spriteIndex = 0;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                tileSprites[spriteIndex] = new BufferedImage(tileSize, tileSize, image.getType());
                Graphics2D g2 = tileSprites[spriteIndex].createGraphics();

                int x_origin = x * tileSize;
                int y_origin = y * tileSize;

                g2.drawImage(image, 0, 0, tileSize, tileSize, x_origin, y_origin,
                        x_origin + tileSize, y_origin + tileSize, null);

                spriteIndex++;
            }
        }

//        for (int i = 0; i < tileSprites.length; i++) {
//            File output = new File("sprite" + i + ".bmp");
//            try {
//                ImageIO.write(tileSprites[i], "bmp", output);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        for (int i = 0; i < tileSprites.length; i++) {
            JButton button = new JButton();
            button.setIcon(new ImageIcon(tileSprites[i].getScaledInstance(tileSize * 2, tileSize * 2, Image.SCALE_FAST)));

            int finalI = i;
            button.addActionListener(e -> {
                EditorManager.selectedTile = tileSprites[finalI];
                System.out.println("hello i am a sprite");
            });

            button.setPreferredSize(new Dimension(tileSize * 2, tileSize * 2));

            add(button);
        }
    }

}