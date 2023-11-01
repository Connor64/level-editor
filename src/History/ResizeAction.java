package History;

import Content.Level;

public class ResizeAction extends HistoricAction {

    private final Level LEVEL;

    private final int OLD_WIDTH, OLD_HEIGHT;
    private final int NEW_WIDTH, NEW_HEIGHT;

    private final int RESIZE_DIRECTION;

    public ResizeAction(Level level, int oldWidth, int oldHeight, int newWidth, int newHeight, int resizeDirection) {
        LEVEL = level;

        OLD_WIDTH = oldWidth;
        OLD_HEIGHT = oldHeight;

        NEW_WIDTH = newWidth;
        NEW_HEIGHT = newHeight;

        RESIZE_DIRECTION = resizeDirection;
    }

    @Override
    public void redoAction() {
        LEVEL.resize(NEW_WIDTH, NEW_HEIGHT, RESIZE_DIRECTION, false);
    }

    @Override
    public void undoAction() {
        LEVEL.resize(OLD_WIDTH, OLD_HEIGHT, RESIZE_DIRECTION, false);
    }
}
