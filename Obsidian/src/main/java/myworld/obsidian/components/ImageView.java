package myworld.obsidian.components;

import myworld.obsidian.display.ObsidianImage;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

public class ImageView extends Component {

    public static final String COMPONENT_STYLE_NAME = "ImageView";

    public static final String IMAGE_DATA_NAME = "image";

    protected final ValueProperty<ObsidianImage> image;

    public ImageView(){
        styleName.set(COMPONENT_STYLE_NAME);

        image = new ValueProperty<>();

        renderVars.put(IMAGE_DATA_NAME, image);
    }

    public ValueProperty<ObsidianImage> image(){
        return image;
    }

}
