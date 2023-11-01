package UIComponents;

import Content.Layer;
import Core.EditorConstants;
import Core.EditorWindow;
import Core.LevelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LayerIcon extends JPanel {
    private JButton layerButton;

    private final LevelManager LEVEL_MANAGER;

    public LayerIcon(String layerName, Layer layer, BufferedImage[] icons) {
        LEVEL_MANAGER = EditorWindow.INSTANCE.getLevelManager();

        JButton editNameButton = new JButton();
        editNameButton.setIcon(new ImageIcon(icons[0]));
        editNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(
                    null,
                    "Rename the layer:", "Layer Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (newName == null || newName.trim().isEmpty()) return;

            LEVEL_MANAGER.getCurrentLevel().renameLayer(layer, newName);
        });
        editNameButton.setBackground(EditorConstants.BUTTON_COLOR);
        editNameButton.setPreferredSize(new Dimension(32, 32));

        // Configure layer select button
        layerButton = new JButton(layerName);
        layerButton.addActionListener(e -> {
            LEVEL_MANAGER.getCurrentLevel().setCurrentLayer(layer);
        });
        layerButton.setBackground(EditorConstants.BUTTON_COLOR);

        // Configure the delete button
        JButton deleteButton = new JButton();
        deleteButton.setIcon(new ImageIcon(icons[1]));
        deleteButton.addActionListener(e -> {
            LEVEL_MANAGER.getCurrentLevel().deleteLayer(layer);
        });
        deleteButton.setBackground(EditorConstants.BUTTON_COLOR);
        deleteButton.setPreferredSize(new Dimension(32, 32));

        // Configure up button
        JButton buttonUp = new JButton();
        buttonUp.setIcon(new ImageIcon(icons[2]));
        buttonUp.addActionListener(e -> {
            LEVEL_MANAGER.getCurrentLevel().shiftLayer(layer, true);
        });
        buttonUp.setBackground(EditorConstants.BUTTON_COLOR);
        buttonUp.setPreferredSize(new Dimension(16, 16));

        // Configure down button
        JButton buttonDown = new JButton();
        buttonDown.setIcon(new ImageIcon(icons[3]));
        buttonDown.addActionListener(e -> {
            LEVEL_MANAGER.getCurrentLevel().shiftLayer(layer, false);
        });
        buttonDown.setBackground(EditorConstants.BUTTON_COLOR);
        buttonDown.setPreferredSize(new Dimension(16, 16));

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridheight = 2;
        add(editNameButton, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(layerButton, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        add(deleteButton, gc);

        gc.gridx = 3;
        gc.gridheight = 1;
        add(buttonUp, gc);

        gc.gridy = 1;
        add(buttonDown, gc);

        setBackground(Color.LIGHT_GRAY);
    }

    public void toggle() {
        layerButton.setBackground(EditorConstants.TOGGLE_COLOR);
    }
}