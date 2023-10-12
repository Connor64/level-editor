package Core;

import Components.LevelCanvas;
import Components.Tile;
import Components.Tileset;
import Components.ToolsPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * The frame for the window that comprises the entire level editor application.
 */
public class EditorWindow extends JFrame {

    public enum EditorMode {
        SELECT,
        DRAW,
        ERASE,
    }

    /** The control panels for the level editor */
    private JPanel bottomPanel;
    private ToolsPanel sidePanel;

    /** The panel which serves a viewport for the level preview. */
    private LevelCanvas levelGrid;

    /** The split panes that contain the 3 main panels of the editor. */
    private JSplitPane splitPane1, splitPane2;

    /** Colors of various elements in the editor. */
    private final Color LEVEL_COLOR, PANEL_COLOR, SPLITPANE_COLOR;

    public final Color BUTTON_COLOR, TOGGLE_COLOR;

    /** The width/height of the level grid in number of tiles. */
    private final int LEVEL_WIDTH = 30;
    private final int LEVEL_HEIGHT = 30;

    /** The menu bar of the application. It holds the "File", "Edit", and "Help" buttons. */
    private JMenuBar menuBar;

    /** The file/edit/help buttons in the menu bar. */
    private JMenu fileMenu, editMenu, helpMenu;

    /** Open/Exit options in the file menu button dropdown. */
    private JMenuItem openFile, importTileset, exit;

    public EditorMode mode;

    /**
     * Sets up the main editor window and all components within it.
     * @param width The starting and minimum width of the window.
     * @param height The starting and minimum height of the window.
     */
    public EditorWindow(int width, int height) {
        super("Level Editor");

        mode = EditorMode.SELECT;

        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize(); // Get resolution of display

        // Set the resolution of the window and center it in the middle of the user's screen
        setBounds((resolution.width - width) / 2, (resolution.height - height) / 2, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(width, height));

        // Initialize the values of the colors used throughout the application
        LEVEL_COLOR = new Color(160, 160, 168);
        PANEL_COLOR = new Color(74, 75, 79);
        SPLITPANE_COLOR = new Color(57, 59, 64);

        BUTTON_COLOR = new Color(231, 234, 239, 255);
        TOGGLE_COLOR = new Color(116, 125, 141, 255);

        // Set up content pane
        Container contentPane = getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(Color.CYAN); // For debugging if elements don't show up correctly

        // Set up bottom panel (will hold custom game objects)
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(width, 90));
        bottomPanel.setBackground(PANEL_COLOR);

        // Set up side control panel
        sidePanel = new ToolsPanel(this);
        sidePanel.setPreferredSize(new Dimension(180, height));
        sidePanel.setBackground(PANEL_COLOR);

        // Set up level grid
        levelGrid = new LevelCanvas(LEVEL_WIDTH, LEVEL_HEIGHT, this);
        levelGrid.setBackground(LEVEL_COLOR);

        // Set up split pane between the level preview and bottom control panel
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, levelGrid, bottomPanel);
        splitPane1.setUI(customDivider(SPLITPANE_COLOR));
        splitPane1.setResizeWeight(0.75);
        splitPane1.setDividerSize(4);
        splitPane1.setBorder(null);
        ((BasicSplitPaneDivider) splitPane1.getComponent(2)).setBorder(null);

        // Set up split pane between previous split pane (level view and bottom panel) and the side control panel
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, sidePanel);
        splitPane2.setUI(customDivider(SPLITPANE_COLOR));
        splitPane2.setResizeWeight(0.75);
        splitPane2.setDividerSize(4);
        splitPane2.setBorder(null);
        ((BasicSplitPaneDivider) splitPane2.getComponent(2)).setBorder(null);

        contentPane.add(splitPane2); // Add all control panels to the content pane

        // Menu bar stuff
        // TODO: Add event handling to menu bar
        menuBar = new JMenuBar();

        // Create the menu buttons
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
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
        exit = new JMenuItem("Exit");
        fileMenu.add(openFile);
        fileMenu.add(importTileset);
        fileMenu.add(exit);

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

    public Tile getCurrentTile() {
        Tileset tileset = sidePanel.getCurrentTileset();
        if (tileset == null) return null;

        return tileset.getCurrentTile();
    }
}