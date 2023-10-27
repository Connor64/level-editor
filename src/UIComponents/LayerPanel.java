package UIComponents;

import Core.EditorConstants;
import Core.EditorWindow;
import Core.LevelManager;

import javax.swing.*;
import java.awt.*;

public class LayerPanel extends JPanel {
    /**
     * The button used to reset the viewport to its original position and scale and empties all tiles.
     */
    private JButton resetButton;
    private JButton addLayerButton;

    private final EditorWindow EDITOR;
    private final LevelCanvas CANVAS;

    private LayerContainer layerContainer;
    private JScrollPane layerScrollPane;

    public LayerPanel(EditorWindow editor, LevelCanvas canvas) {
        EDITOR = editor;
        CANVAS = canvas;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Set up button to reset position and scale of level preview
        resetButton = new JButton("Reset Position");
        resetButton.setBackground(EditorConstants.BUTTON_COLOR);
        resetButton.addActionListener(e -> {
            CANVAS.resetCanvasPosition();
        });

//        layersDropdown = new JComboBox<>();
//        layersDropdown.setBackground(EditorConstants.BUTTON_COLOR);
//        layersDropdown.addActionListener(e -> {
//            if (CANVAS.currentLayer == layersDropdown.getSelectedIndex()) return;
//
//            CANVAS.currentLayer = layersDropdown.getSelectedIndex();
//            CANVAS.repaint();
//        });
//
        addLayerButton = new JButton("Add Layer");
        addLayerButton.setBackground(EditorConstants.BUTTON_COLOR);
        addLayerButton.addActionListener(e -> {
            String layerName = JOptionPane.showInputDialog(
                    null,
                    "Enter a name for the layer:", "Layer Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (layerName == null || layerName.trim().isEmpty()) return;

            LevelManager.INSTANCE.getCurrentLevel().addLayer(layerName);
            layerContainer.addLayer(layerName);
        });
//
//        removeLayerButton = new JButton("Remove Layer");
//        removeLayerButton.setBackground(EditorConstants.BUTTON_COLOR);
//        removeLayerButton.addActionListener(e -> {
//            if (CANVAS.currentLayer < 0) return;
//            layersDropdown.removeItemAt(CANVAS.currentLayer);
//            CANVAS.removeCurrentLayer();
//        });

        layerContainer = new LayerContainer();
        layerScrollPane = new JScrollPane(layerContainer);

        gc.insets = new Insets(5, 5, 5, 5);

        gc.gridy = 0;
        gc.gridwidth = 2;
        add(resetButton, gc);

        gc.gridy = 1;
        add(addLayerButton, gc);
//        add(removeLayerButton, gc);
//
//        gc.gridy = 2;
//        gc.gridwidth = 2;
//        add(layersDropdown, gc);

        gc.gridy = 3;
        gc.weightx = 1;
        gc.weighty = 0.75;
        gc.fill = GridBagConstraints.BOTH;
        add(layerScrollPane, gc);
    }
}
