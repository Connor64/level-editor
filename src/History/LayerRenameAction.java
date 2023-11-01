package History;

import Content.Layer;

public class LayerRenameAction extends HistoricAction {

    private final Layer LAYER;

    private final String OLD_NAME;
    private final String NEW_NAME;

    public LayerRenameAction(Layer layer, String oldName, String newName) {
        LAYER = layer;

        OLD_NAME = oldName;
        NEW_NAME = newName;
    }

    @Override
    public void redoAction() {
        LAYER.setName(NEW_NAME);
    }

    @Override
    public void undoAction() {
        LAYER.setName(OLD_NAME);
    }
}
