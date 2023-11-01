package Core;

import UIComponents.*;
import Content.Tileset;
import Content.Tile;
import Core.EditorConstants.EditorMode;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * The frame for the window that comprises the entire level editor application. Uses singleton design pattern.
 */
public class EditorWindow extends JFrame {

    public static final EditorWindow INSTANCE = new EditorWindow();

    /** The control panels for the level editor */
    private JPanel bottomPanel;
    private ToolsPanel sidePanel;
    private LevelManager levelManager;
    private LayerControlsPanel layerControlsPanel;

    /** The panel which serves as a viewport for the level preview. */
    private LevelCanvas levelCanvas;

    /** The split panes that contain the 3 main panels of the editor. */
    private JSplitPane splitPane1, splitPane2, splitPane3;

    /** The menu bar of the application. It holds the "File", "Edit", and "Help" buttons. */
    private JMenuBar menuBar;

    /** The file/edit/help buttons in the menu bar. */
    private JMenu fileMenu, editMenu, helpMenu;

    /** Open/Exit options in the file menu button dropdown. */
    private JMenuItem openFile, importTileset, exportLevel, exit;

    /** Options in the editor menu button dropdown. */
    private JMenuItem resizeLevel;

    public EditorMode mode;

    /**
     * Creates the JFrame of the application.
     */
    private EditorWindow() {
        super("Level Editor");
    }

    /**
     * Initializes the main editor window and all components within it.
     *
     * @param width The starting and minimum width of the window.
     * @param height The starting and minimum height of the window.
     */
    public void initialize(int width, int height) {
        mode = EditorMode.SELECT;

        levelManager = new LevelManager();

        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize(); // Get resolution of display

        // Set the resolution of the window and center it in the middle of the user's screen
        setBounds((resolution.width - width) / 2, (resolution.height - height) / 2, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(width, height));

        // Set up content pane
        Container contentPane = getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(Color.CYAN); // For debugging if elements don't show up correctly

        // Set up bottom panel (will hold custom game objects)
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(width, 90));
        bottomPanel.setBackground(EditorConstants.PANEL_COLOR);

        // Set up side control panel
        sidePanel = new ToolsPanel();
        sidePanel.setPreferredSize(new Dimension(180, height));
        sidePanel.setBackground(EditorConstants.PANEL_COLOR);

        // Set up level grid
        levelCanvas = new LevelCanvas();
        levelCanvas.setBackground(EditorConstants.LEVEL_COLOR);

        layerControlsPanel = new LayerControlsPanel();
        layerControlsPanel.setBackground(EditorConstants.PANEL_COLOR);

        // Set up split pane between the level preview and bottom control panel
        splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, layerControlsPanel, levelCanvas);
        splitPane1.setUI(customDivider(EditorConstants.SPLITPANE_COLOR));
        splitPane1.setResizeWeight(0);
        splitPane1.setDividerSize(4);
        splitPane1.setBorder(null);
        ((BasicSplitPaneDivider) splitPane1.getComponent(2)).setBorder(null);

        // Set up split pane between the level preview and bottom control panel
        splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane1, bottomPanel);
        splitPane2.setUI(customDivider(EditorConstants.SPLITPANE_COLOR));
        splitPane2.setResizeWeight(0.75);
        splitPane2.setDividerSize(4);
        splitPane2.setBorder(null);
        ((BasicSplitPaneDivider) splitPane2.getComponent(2)).setBorder(null);

        // Set up split pane between previous split pane (level view and bottom panel) and the side control panel
        splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane2, sidePanel);
        splitPane3.setUI(customDivider(EditorConstants.SPLITPANE_COLOR));
        splitPane3.setResizeWeight(0.75);
        splitPane3.setDividerSize(4);
        splitPane3.setBorder(null);
        ((BasicSplitPaneDivider) splitPane3.getComponent(2)).setBorder(null);

        contentPane.add(splitPane3); // Add all control panels to the content pane

        // Menu bar stuff
        // TODO: Add event handling to menu bar
        menuBar = new JMenuBar();

        // Create the menu buttons
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit"); // TODO: Add ability to rename current tileset and chance level size
        helpMenu = new JMenu("Help");

        // Assigns a key to each menu button
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        // Create and add menu items to the "File" menu
        openFile = new JMenuItem("Open file");

        importTileset = new JMenuItem("Import tileset");
        importTileset.addActionListener(e -> {
            try {
                sidePanel.createNewTileset();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        exportLevel = new JMenuItem("Export Level");
        exportLevel.addActionListener(e -> {
            try {
                levelManager.exportCurrentLevel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        exit = new JMenuItem("Exit");

        fileMenu.add(openFile);
        fileMenu.add(importTileset);
        fileMenu.add(exportLevel);
        fileMenu.add(exit);

        resizeLevel = new JMenuItem("Resize Level");
        resizeLevel.addActionListener(e -> {
            levelManager.resizeCurrentLevel();
        });

        editMenu.add(resizeLevel);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar); // This adds it to the top of the screen/window

        pack(); // Removes any unnecessary space

        setVisible(true); // After adding everything, make it visible.
    }

    /**
     Generates a custom split pane divider.
     @param dividerColor The desired color of the split pane divider.
     @return A BasicSplitPaneUI object which can be passed to a split pane's setUI() function to assign the new divider.
     */
    public BasicSplitPaneUI customDivider(Color dividerColor) {
        return new BasicSplitPaneUI() {

            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {

                    @Override
                    public void paint(Graphics g) {
                        // Set the divider's background color
                        g.setColor(dividerColor); // Change this to the desired color
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        };
    }

    public void refreshComponents() {
        levelCanvas.repaint();
        layerControlsPanel.repaint();
        layerControlsPanel.getLayerContainer().refreshLayers();
    }

    public Tile getCurrentTile() {
        Tileset tileset = sidePanel.getCurrentTileset();
        if (tileset == null) return null;

        return tileset.getCurrentTile();
    }

    public LevelCanvas getCanvas() {
        return levelCanvas;
    }

    public LayerControlsPanel getLayerControls() {
        return layerControlsPanel;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }
}