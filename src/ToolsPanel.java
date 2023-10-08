import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class ToolsPanel extends JPanel {
    private JButton testButton;
    private JFileChooser fileChooser;

    public ToolsPanel() {
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (*.png, *.bmp)", "png", "bmp"
        );
        fileChooser.setFileFilter(filter);

        testButton = new JButton("Open File");
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

        File file = fileChooser.getSelectedFile();
        BufferedImage bufferedImage = ImageIO.read(file);

        JPopupMenu popupMenu = new JPopupMenu("Create new Tileset");
        popupMenu.add(new JLabel(new ImageIcon(bufferedImage)));

//        JOptionPane.showInputDialog(null,)

        popupMenu.show(getParent(), 0, 0);

        System.out.println(file.getName());
    }
}
