package UIComponents;

import Core.EditorConstants;
import Core.EditorWindow;
import Core.LevelManager;

import javax.swing.*;
import java.awt.*;

public class LayerControlsPanel extends JPanel {
    /**
     * The button used to reset the viewport to its original position and scale and empties all tiles.
     */
    private JButton resetButton;
    private JButton addLayerButton;

    private final EditorWindow EDITOR;
    private final LevelManager LEVEL_MANAGER;

    private LayerContainer layerContainer;
    private JScrollPane layerScrollPane;

    public LayerControlsPanel() {
        EDITOR = EditorWindow.INSTANCE;
        LEVEL_MANAGER = EDITOR.getLevelManager();

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Set up button to reset position and scale of level preview
        resetButton = new JButton("Reset Position");
        resetButton.setBackground(EditorConstants.BUTTON_COLOR);
        resetButton.addActionListener(e -> {
            EDITOR.getCanvas().resetCanvasPosition();
        });

        addLayerButton = new JButton("Add Layer");
        addLayerButton.setBackground(EditorConstants.BUTTON_COLOR);
        addLayerButton.addActionListener(e -> {
            String layerName = JOptionPane.showInputDialog(
                    null,
                    "Enter a name for the layer:", "Layer Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (layerName == null || layerName.trim().isEmpty()) return;

            LEVEL_MANAGER.getCurrentLevel().addLayer(layerName);
        });

        layerContainer = new LayerContainer();
        layerScrollPane = new JScrollPane(layerContainer);

        gc.insets = new Insets(5, 5, 5, 5);

        gc.gridy = 0;
        add(resetButton, gc);

        gc.gridy = 1;
        add(addLayerButton, gc);

        gc.gridy = 2;
        gc.weightx = 1;
        gc.weighty = 0.75;
        gc.fill = GridBagConstraints.BOTH;
        add(layerScrollPane, gc);
    }

    public LayerContainer getLayerContainer() {
        return layerContainer;
    }
}
