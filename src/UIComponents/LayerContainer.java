package UIComponents;

import Content.Layer;
import Core.EditorConstants;
import Core.EditorWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LayerContainer extends JPanel {
    private final GridBagConstraints GC;
    private BufferedImage[] icons;

    public LayerContainer() {
        setBackground(EditorConstants.PANEL_COLOR);

        setLayout(new GridBagLayout());
        GC = new GridBagConstraints();

        GC.insets = new Insets(1, 0, 1, 0);
        GC.weightx = 1;
        GC.fill = GridBagConstraints.BOTH;
        GC.anchor = GridBagConstraints.NORTH;

        icons = new BufferedImage[4];
        // Load icon images
        try {
            icons[0] = ImageIO.read(new File("icons/pencil_icon.png"));
            icons[1] = ImageIO.read(new File("icons/delete_icon.png"));
            icons[2] = ImageIO.read(new File("icons/up_icon.png"));
            icons[3] = ImageIO.read(new File("icons/down_icon.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshLayers() {
        ArrayList<Layer> layers = EditorWindow.INSTANCE.getLevelManager().getCurrentLevel().getLayers();
        Layer currentLayer = EditorWindow.INSTANCE.getLevelManager().getCurrentLevel().getCurrentLayer();

        removeAll();
        GC.gridy = layers.size() - 1;

        for (Layer layer : layers) {
            LayerIcon icon = new LayerIcon(layer.getName(), layer, icons);

            if (currentLayer == layer) icon.toggle();

            add(icon, GC);
            GC.gridy--;
        }

        revalidate();
        repaint();
    }
}
