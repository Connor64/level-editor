package Core;

import Content.Level;
import Serial.LevelData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Manages all levels currently open within the application.
 */
public class LevelManager {
    private final int DEFAULT_WIDTH = 30, DEFAULT_HEIGHT = 30;

    private ArrayList<Level> levels;
    private Level currentLevel = null;
    private final JPanel resizeDirectionPanel;
    private int resizeDirection = 4;

    private JFileChooser fileChooser;

    public LevelManager() {
        levels = new ArrayList<>();
        levels.add(new Level(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        currentLevel = levels.get(0);

        resizeDirectionPanel = new JPanel();
        resizeDirectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Add resize direction buttons to the panel
        try {
            int index = 0;
            for (int y = 0; y < 3; y++) {
                gc.gridy = y;
                for (int x = 0; x < 3; x++) {
                    int finalIndex = index;
                    BufferedImage icon = ImageIO.read(new File("icons/resize_icon_" + index + ".png"));

                    JButton button = new JButton();
                    button.setBackground((index == resizeDirection) ? EditorConstants.TOGGLE_COLOR : EditorConstants.BUTTON_COLOR);
                    button.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_FAST)));
                    button.setPreferredSize(new Dimension(32, 32));
                    button.addActionListener(e -> {
                        for (int i = 0; i < 9; i++) {
                            resizeDirectionPanel.getComponent(i).setBackground(EditorConstants.BUTTON_COLOR);
                        }
                        button.setBackground(EditorConstants.TOGGLE_COLOR);
                        resizeDirection = finalIndex;
                    });

                    gc.gridx = x;
                    resizeDirectionPanel.add(button, gc);
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // File chooser for the tileset
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Level File (*.lvl, *.level)", "lvl", "level"
        );
        fileChooser.setFileFilter(filter);
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int levelIndex) {
        currentLevel = levels.get(levelIndex);

        // TODO: probably update the panels or something idk
    }

    /**
     * Prompts to the user to resize the level canvas and choose which direction it grows/shrinks
     */
    public void resizeCurrentLevel() {
        // Create panel containing controls
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Create width label and field
        JLabel widthLabel = new JLabel("Width:");
        JTextField widthField = new JTextField(4);
        widthField.setText(String.valueOf(currentLevel.getWidth()));

        // Create height label and field
        JLabel heightLabel = new JLabel("Height:");
        JTextField heightField = new JTextField(4);
        heightField.setText(String.valueOf(currentLevel.getHeight()));

        // Add labels and fields to panel
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(2, 2, 2, 2);

        sizePanel.add(widthLabel, gc);
        gc.gridx = 1;
        sizePanel.add(widthField, gc);

        gc.gridy = 1;
        sizePanel.add(heightField, gc);
        gc.gridx = 0;
        sizePanel.add(heightLabel, gc);

        gc.gridwidth = 2;
        gc.gridx = 0;
        gc.gridy = 2;
        sizePanel.add(resizeDirectionPanel, gc);

        int newWidth = 0;
        int newHeight = 0;

        // Loop over prompt until user cancels or gives valid input
        while (true) {
            // Prompt the user for a tile size
            int decision = JOptionPane.showConfirmDialog(
                    null,
                    sizePanel,
                    "Choose a new level size",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (decision != JOptionPane.OK_OPTION) return;

            // Parse input
            try {
                newWidth = Integer.parseInt(widthField.getText());
                newHeight = Integer.parseInt(heightField.getText());
            } catch (NumberFormatException ignored) {
            }

            // If input is valid, exit loop
            if ((newWidth > 0) && (newHeight > 0)) {
                break;
            }

            // Prompt user for valid input
            JOptionPane.showMessageDialog(null,
                    "Input must be an integer greater than 0!",
                    "Invalid Input!", JOptionPane.ERROR_MESSAGE
            );

            // Reset fields
            widthField.setText(String.valueOf(currentLevel.getWidth()));
            heightField.setText(String.valueOf(currentLevel.getHeight()));
            newWidth = 0;
            newHeight = 0;
        }

        // If the size wasn't changed
        if ((newWidth == currentLevel.getWidth()) &&
                (newHeight == currentLevel.getHeight())) return;

        currentLevel.resize(newWidth, newHeight, resizeDirection, true);
    }

    /**
     * Exports the current level as a .lvl file.
     *
     * @throws IOException Thrown if the filepath is invalid or file is unable to be written.
     */
    public void exportCurrentLevel() throws IOException {
        // Prompt for choosing the level's name
        String levelName = JOptionPane.showInputDialog(
                null,
                "Enter a name for the level (this is not the file name!):", "Layer Name",
                JOptionPane.PLAIN_MESSAGE
        );
        if (levelName == null || levelName.trim().isEmpty()) return;

        // Prompt the user to choose where to save the file
        int val = fileChooser.showSaveDialog(null);

        if (val != JFileChooser.APPROVE_OPTION) return;

        // Create output stream to the chosen file destination
        File fileToSave = fileChooser.getSelectedFile();
        ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(fileToSave.toPath()));

        // Create and export the LevelData object
        LevelData levelData = new LevelData(currentLevel, levelName);
        outputStream.writeObject(levelData);

        outputStream.close();
    }
}
