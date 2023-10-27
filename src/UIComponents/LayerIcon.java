package UIComponents;

import Core.EditorConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LayerIcon extends JPanel {
    private String layerName;
    private boolean selected = false;
    private LayerContainer container;

    public LayerIcon(String layerName) {
        this.layerName = layerName;

        container = (LayerContainer) getParent();

        // Load icon images
        BufferedImage upIcon = null;
        BufferedImage downIcon = null;
        BufferedImage deleteIcon = null;
        try {
            upIcon = ImageIO.read(new File("icons/up_icon.png"));
            downIcon = ImageIO.read(new File("icons/down_icon.png"));
            deleteIcon = ImageIO.read(new File("icons/delete_icon.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Configure layer select button
        JButton layerButton = new JButton(layerName);
        layerButton.addActionListener(e -> {
            System.out.println("selected " + layerName);

        });
        setSelected(false);

        // Configure the delete button
        JButton deleteButton = new JButton();
        deleteButton.setIcon(new ImageIcon(deleteIcon));
        deleteButton.addActionListener(e -> {
            System.out.println("deleted " + layerName);
        });
        deleteButton.setBackground(EditorConstants.BUTTON_COLOR);
        deleteButton.setPreferredSize(new Dimension(32, 32));

        // Configure up button
        JButton buttonUp = new JButton();
        buttonUp.setIcon(new ImageIcon(upIcon));
        buttonUp.addActionListener(e -> {
            System.out.println("upbutton teehee");
        });
        buttonUp.setBackground(EditorConstants.BUTTON_COLOR);
        buttonUp.setPreferredSize(new Dimension(16, 16));

        // Configure down button
        JButton buttonDown = new JButton();
        buttonDown.setIcon(new ImageIcon(downIcon));
        buttonDown.addActionListener(e -> {
            System.out.println("downbutton haha hehe");
        });
        buttonDown.setBackground(EditorConstants.BUTTON_COLOR);
        buttonDown.setPreferredSize(new Dimension(16, 16));

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridheight = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(layerButton, gc);

        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;
        gc.gridx = 1;
        add(deleteButton, gc);

        gc.gridheight = 1;
        gc.gridx = 2;
        add(buttonUp, gc);

        gc.gridy = 1;
        add(buttonDown, gc);

        setBackground(Color.LIGHT_GRAY);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        setBackground(selected ? EditorConstants.TOGGLE_COLOR : EditorConstants.BUTTON_COLOR);
    }
}