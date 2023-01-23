package myworld.obsidian.components;

import myworld.obsidian.events.dispatch.EventFilters;
import myworld.obsidian.events.input.BaseMouseEvent;
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

        range = new ValueProperty<>(new Range<>(0f, 1000f));
        value = new ValueProperty<>(0f);
        orientation = new ValueProperty<>(Orientation.HORIZONTAL);

        width = new ValueProperty<>(Distance.percentage(10));

        renderVars.put(ENABLED_DATA_NAME, enabled);
        renderVars.put(HORIZONTAL_DATA_NAME, () -> orientation.get().equals(Orientation.HORIZONTAL));
        renderVars.put(VERTICAL_DATA_NAME, () -> orientation.get().equals(Orientation.VERTICAL));
        renderVars.put(WIDTH_DATA_NAME, width);
        renderVars.put(OFFSET_DATA_NAME, this::visualOffset);

        enabled.addListener((prop, oldValue, newValue) -> {
            focusable.set(newValue);
            hoverable.set(newValue);
        });

        var startPos = new ValueProperty<Integer>();
        dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mousePressed(MouseButton.PRIMARY), evt -> {
            startPos.set(primaryAxisValue(evt));
            evt.consume();
        });
        dispatcher.subscribe(MouseMoveEvent.class, evt -> evt.getManager().isDown(MouseButton.PRIMARY) && startPos.isSet(), evt -> {
            var mouseDelta = primaryAxisDelta(evt);
            var mouseCoord = primaryAxisValue(evt);

            if(mouseDelta > 0 && mouseCoord > startPos.get()
                || mouseDelta < 0 && mouseCoord < startPos.get()){

                if(move(pixelDeltaToValueDelta(mouseDelta))){
                    startPos.set(mouseCoord);
                }

                evt.consume();
            }
        });
        dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mouseReleased(MouseButton.PRIMARY), evt -> {
            startPos.set(null);
            evt.consume();
        });
    }

    protected float rangeWidth(){
        return range.get().end() - range.get().start();
    }

    protected float effectiveWidth(){
        return primaryAxisWidth() - width.get().toPixels(primaryAxisWidth());
    }

    protected float pixelsPerValue(){
        return effectiveWidth() / rangeWidth();
    }

    protected float visualOffset(){
        return value.get() * pixelsPerValue();
    }

    protected float pixelDeltaToValueDelta(float pixelDelta){
        return pixelDelta / pixelsPerValue();
    }

    public ValueProperty<Boolean> enabled(){
        return enabled;
    }

    public ValueProperty<Range<Float>> valueRange(){
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

    public boolean move(float amount){
        var current = value.get();
        var clamped = Range.clamp(range.get().start(), current + amount, range.get().end());
        value.set(clamped);
        return clamped == current + amount;
    }

    public int primaryAxisDelta(MouseMoveEvent evt){
        return isHorizontal() ? evt.getDeltaX() : evt.getDeltaY();
    }

    public int primaryAxisValue(BaseMouseEvent evt){
        return isHorizontal() ? evt.getX() : evt.getY();
    }

    public float primaryAxisWidth(){
        var bounds = getLocalBounds();
        return isHorizontal() ? bounds.width() : bounds.height();
    }
}
