package Core;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class EditorConstants {

    /** Colors of various elements in the editor. */
    public final static Color LEVEL_COLOR = new Color(160, 160, 168);
    public final static Color PANEL_COLOR = new Color(74, 75, 79);
    public final static Color SPLITPANE_COLOR = new Color(57, 59, 64);
    public final static Color BUTTON_COLOR = new Color(231, 234, 239, 255);
    public final static Color TOGGLE_COLOR = new Color(116, 125, 141, 255);

    public final static Border HOVER_BORDER = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
    public final static Border SELECT_BORDER = BorderFactory.createLineBorder(Color.RED, 2);

    public enum EditorMode {
        SELECT,
        DRAW,
        ERASE,
    }
}