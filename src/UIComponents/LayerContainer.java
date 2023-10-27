package UIComponents;

import Core.EditorConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LayerContainer extends JPanel {
    ArrayList<LayerIcon> layerIcons;
    GridBagConstraints gc;

    public LayerContainer() {
        layerIcons = new ArrayList<>();

        setBackground(EditorConstants.PANEL_COLOR);

        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();

        gc.insets = new Insets(1, 0, 1, 0);
        gc.gridy = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;

        revalidate();
        repaint();
    }

    public void addLayer(String layerName) {
        LayerIcon layer = new LayerIcon(layerName);
        layerIcons.add(layer);

        add(layer, gc);
        gc.gridy++;
    }

    public void selectLayer(int layerIndex) {
        for (int i = 0; i < layerIcons.size(); i++) {
            layerIcons.get(i).setSelected(i == layerIndex);
        }
    }
}
