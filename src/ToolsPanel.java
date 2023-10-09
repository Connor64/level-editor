import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ToolsPanel extends JPanel {
    private JButton testButton;
    private final JFileChooser fileChooser;

    private ArrayList<Tileset> tilesets;

    public ToolsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        tilesets = new ArrayList<>();

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (*.png, *.bmp)", "png", "bmp"
        );
        fileChooser.setFileFilter(filter);

        testButton = new JButton("Open File");
        testButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        testButton.addActionListener(e -> {
            try {
                openFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(testButton);
    }

    public void openFile() throws IOException {
        int returnVal = fileChooser.showOpenDialog(getParent());
        if (returnVal != JFileChooser.APPROVE_OPTION) return;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        File file = fileChooser.getSelectedFile();
        BufferedImage bufferedImage = ImageIO.read(file);
        JLabel label = new JLabel(new ImageIcon(bufferedImage));
        System.out.println("width: " + bufferedImage.getWidth());

        JTextField textField = new JTextField(10);

        panel.add(label);
        panel.add(textField);

        int tileSize = getValidTileSize(bufferedImage.getWidth(), bufferedImage.getHeight(), panel, textField);

        if (tileSize == -1) return;

        tilesets.add(new Tileset(tileSize, bufferedImage));
        add(tilesets.get(0));

        System.out.println(file.getName());
        System.out.println("Tile size: " + tileSize);
    }

    private int getValidTileSize(int width, int height, JPanel panel, JTextField textField) {
        int tileSize = -1;
        int val = 0;
        while (true) {
            int decision = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    val == 0 ? "Choose a tile size" : "Invalid. Choose a tile size.",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (decision == JOptionPane.CANCEL_OPTION) break;

            val = -1;
            try {
                val = Integer.parseInt(textField.getText());
            } catch (NumberFormatException ignored) {}

            if ((val == -1) || (((width % val) != 0) && ((height % val) != 0))) {
                textField.setText("");
                continue;
            }

            tileSize = val;

            break;
        }

        return tileSize;
    }
}
