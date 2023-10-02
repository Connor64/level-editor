import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.KeyEvent;

public class EditorWindow extends JFrame {

    private JPanel bottomPanel, sidePanel;
    JSplitPane splitPane1, splitPane2;
    private Container contentPane;
    private LevelGrid levelGrid;

    private Color levelColor, panelColors, splitPaneColor;

    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, helpMenu;

    private JMenuItem openFile, exit;

    public EditorWindow(int width, int height) {
        super("Level Editor");

        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize(); // Get resolution of display

        setBounds((resolution.width - width) / 2, (resolution.height - height) / 2, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(width, height));

        levelColor = new Color(160, 160, 168);
        panelColors = new Color(74, 75, 79);
        splitPaneColor = new Color(57, 59, 64);

        // Set up content pane
        contentPane = getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(Color.CYAN);

        // Set up bottom panel (will hold custom game objects)
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(width, 90));
        bottomPanel.setBackground(panelColors);

        // Set up side control panel
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(180, height));
        sidePanel.setBackground(panelColors);

        // Set up level grid
        levelGrid = new LevelGrid(30, 30);
        levelGrid.setBackground(levelColor);

        // Set up split panes
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, levelGrid, bottomPanel);
        splitPane1.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void paint(Graphics g) {
                        // Set the divider's background color
                        g.setColor(splitPaneColor); // Change this to the desired color
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
        splitPane1.setResizeWeight(0.75);
        splitPane1.setDividerSize(4);
        splitPane1.setBorder(null);
        ((BasicSplitPaneDivider) splitPane1.getComponent(2)).setBorder(null);

        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, sidePanel);
        splitPane2.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void paint(Graphics g) {
                        // Set the divider's background color
                        g.setColor(splitPaneColor); // Change this to the desired color
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
        splitPane2.setResizeWeight(0.75);
        splitPane2.setDividerSize(4);
        splitPane2.setBorder(null);
        ((BasicSplitPaneDivider) splitPane2.getComponent(2)).setBorder(null);

        contentPane.add(splitPane2);

        // Menu bar stuff
        // TODO: Add event handling to menu bar
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        openFile = new JMenuItem("Open file");
        exit = new JMenuItem("Exit");
        fileMenu.add(openFile);
        fileMenu.add(exit);

        menuBar.add(fileMenu);

        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();

        setVisible(true);
    }
}