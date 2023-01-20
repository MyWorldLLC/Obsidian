package myworld.obsidian.components;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.events.dispatch.EventFilters;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.input.MouseMoveEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.util.Range;

public class Slider extends Component {

    public static final String COMPONENT_STYLE_NAME = "Slider";

    public static final String ENABLED_DATA_NAME = "enabled";
    public static final String HORIZONTAL_DATA_NAME = "horizontal";
    public static final String VERTICAL_DATA_NAME = "vertical";
    public static final String WIDTH_DATA_NAME = "width";
    public static final String OFFSET_DATA_NAME = "offset";

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    protected final ValueProperty<Boolean> enabled;
    protected final ValueProperty<Range<Float>> range;
    protected final ValueProperty<Float> value;
    protected final ValueProperty<Orientation> orientation;
    protected final ValueProperty<Distance> width;

    public Slider(){
        styleName.set(COMPONENT_STYLE_NAME);

        enabled = new ValueProperty<>(true);

        range = new ValueProperty<>(new Range<>(0f, 100f));
        value = new ValueProperty<>(0f);
        orientation = new ValueProperty<>(Orientation.HORIZONTAL);

        width = new ValueProperty<>(Distance.percentage(10));

        renderVars.put(ENABLED_DATA_NAME, enabled);
        renderVars.put(HORIZONTAL_DATA_NAME, () -> orientation.get().equals(Orientation.HORIZONTAL));
        renderVars.put(VERTICAL_DATA_NAME, () -> orientation.get().equals(Orientation.VERTICAL));
        renderVars.put(WIDTH_DATA_NAME, width);
        renderVars.put(OFFSET_DATA_NAME, () -> {
            return value.get();
        });

        enabled.addListener((prop, oldValue, newValue) -> {
            focusable.set(newValue);
            hoverable.set(newValue);
        });

        dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mousePressed(MouseButton.PRIMARY), BaseEvent::consume);
        dispatcher.subscribe(MouseMoveEvent.class, evt -> evt.getManager().isDown(MouseButton.PRIMARY), evt -> {
            // TODO - convert pixel delta to proportion of the available range,
            //  which is the "width" of the track along the primary axis (horizontal or vertical) minus the width of the slider
            var delta = (primaryAxisDelta(evt) / (primaryAxisWidth() - width.get().toPixels(primaryAxisWidth()))) * (allowedRange().get().end() - allowedRange().get().start());
            move(delta);
        });
        dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mouseReleased(MouseButton.PRIMARY), BaseEvent::consume);
    }

    public ValueProperty<Boolean> enabled(){
        return enabled;
    }

    public ValueProperty<Range<Float>> allowedRange(){
        return range;
    }

    public ValueProperty<Float> value(){
        return value;
    }

    public ValueProperty<Orientation> orientation(){
        return orientation;
    }

    public ValueProperty<Distance> width(){
        return width;
    }

    public boolean isHorizontal(){
        return orientation.get().equals(Orientation.HORIZONTAL);
    }

    public boolean isVertical(){
        return orientation.get().equals(Orientation.VERTICAL);
    }

    public void move(float amount){
        value.setWith(current -> Range.clamp(allowedRange().get().start(), current + amount, allowedRange().get().end()));
    }

    public int primaryAxisDelta(MouseMoveEvent evt){
        return isHorizontal() ? evt.getDeltaX() : evt.getDeltaY();
    }

    public float primaryAxisWidth(){
        var bounds = getLocalBounds();
        return isHorizontal() ? bounds.width() : bounds.height();
    }
}
