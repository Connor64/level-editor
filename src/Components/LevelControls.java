package Components;

import Core.EditorWindow;

import javax.swing.*;
import java.awt.*;

public class LevelControls extends JPanel {
    /**
     * The button used to reset the viewport to its original position and scale and empties all tiles.
     */
    private JButton resetButton;
    private JButton addLayerButton, removeLayerButton;

    private JComboBox<String> layersDropdown;
    private final EditorWindow EDITOR;
    private final LevelCanvas CANVAS;

    public LevelControls(EditorWindow editor, LevelCanvas canvas) {
        EDITOR = editor;
        CANVAS = canvas;

//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Set up button to reset position and scale of level preview
        resetButton = new JButton("Reset Position");
        resetButton.setBackground(EDITOR.BUTTON_COLOR);
        resetButton.addActionListener(e -> {
            CANVAS.resetCanvasPosition();
        });

        layersDropdown = new JComboBox<>();
        layersDropdown.setBackground(EDITOR.BUTTON_COLOR);
        layersDropdown.addActionListener(e -> {
            if (CANVAS.currentLayer == layersDropdown.getSelectedIndex()) return;

            CANVAS.currentLayer = layersDropdown.getSelectedIndex();
            CANVAS.repaint();
        });

        addLayerButton = new JButton("Add Layer");
        addLayerButton.setBackground(EDITOR.BUTTON_COLOR);
        addLayerButton.addActionListener(e -> {
            String layerName = JOptionPane.showInputDialog(
                    null,
                    "Enter a name for the layer:", "Layer Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (layerName == null || layerName.trim().isEmpty()) return;

            CANVAS.addLayer();
            layersDropdown.addItem(layerName);
        });

        removeLayerButton = new JButton("Remove Layer");
        removeLayerButton.setBackground(EDITOR.BUTTON_COLOR);
        removeLayerButton.addActionListener(e -> {
            if (CANVAS.currentLayer < 0) return;
            layersDropdown.removeItemAt(CANVAS.currentLayer);
            CANVAS.removeCurrentLayer();
        });

        gc.insets = new Insets(5, 5, 5, 5);

        gc.gridy = 0;
        gc.gridwidth = 2;
        add(resetButton, gc);

        gc.gridy = 1;
        gc.gridwidth = 1;
        add(addLayerButton, gc);
        add(removeLayerButton, gc);

        gc.gridy = 2;
        gc.gridwidth = 2;
        add(layersDropdown, gc);
    }


}
