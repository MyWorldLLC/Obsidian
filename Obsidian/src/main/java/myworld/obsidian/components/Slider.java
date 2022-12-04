package myworld.obsidian.components;

import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.util.Range;

public class Slider extends Component {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public static final String COMPONENT_STYLE_NAME = "Slider";

    protected final ValueProperty<Float> value;
    protected final ValueProperty<Range<Float>> allowedRange;
    protected final ValueProperty<Range<Float>> slider;
    protected final ValueProperty<Orientation> orientation;

    public Slider(){
        styleName.set(COMPONENT_STYLE_NAME);

        value = new ValueProperty<>();
        allowedRange = new ValueProperty<>();
        slider = new ValueProperty<>();
        orientation = new ValueProperty<>(Orientation.HORIZONTAL);
    }

    public ValueProperty<Float> value(){
        return value;
    }

    public ValueProperty<Range<Float>> allowedRange(){
        return allowedRange;
    }

    public ValueProperty<Range<Float>> slider(){
        return slider;
    }

    public ValueProperty<Orientation> orientation(){
        return orientation;
    }
}
