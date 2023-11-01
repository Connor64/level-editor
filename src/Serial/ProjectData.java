package Serial;

import Content.Layer;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjectData implements Serializable {
    public ArrayList<Layer> layers;
    public ArrayList<String> tilesetFilenames;
    public String levelName;
    public int width, height;


}
