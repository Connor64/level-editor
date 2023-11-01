package History;

import Content.Layer;
import Content.Level;

import java.util.ArrayList;
import java.util.Arrays;

public class LayerAction extends HistoricAction {

    private final Level LEVEL;
    private final Layer[] OLD_LAYERS;
    private final Layer[] NEW_LAYERS;

    public LayerAction(Level level, Layer[] oldLayers) {
        LEVEL = level;

        OLD_LAYERS = oldLayers;
        NEW_LAYERS = level.getLayers().toArray(new Layer[level.getNumLayers()]);
    }

    @Override
    public void redoAction() {
        System.out.println("redoing level edit!");
        ArrayList<Layer> currentLayers = LEVEL.getLayers();

        for (Layer layer : OLD_LAYERS) {
            currentLayers.remove(layer);
        }

        currentLayers.addAll(Arrays.asList(NEW_LAYERS));

        LEVEL.setCurrentLayer(null);
    }

    @Override
    public void undoAction() {
        System.out.println("undoing level edit!");
        ArrayList<Layer> currentLayers = LEVEL.getLayers();

        for (Layer layer : NEW_LAYERS) {
            currentLayers.remove(layer);
        }

        currentLayers.addAll(Arrays.asList(OLD_LAYERS));

        LEVEL.setCurrentLayer(null);
    }
}
