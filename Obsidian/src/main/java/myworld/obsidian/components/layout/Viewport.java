package myworld.obsidian.components.layout;

import myworld.obsidian.events.dispatch.EventFilters;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.input.MouseMoveEvent;
import myworld.obsidian.events.input.MouseOverEvent;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.layout.Offsets;
import myworld.obsidian.layout.PositionType;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.util.Range;

public class Viewport extends Component {

    public static final String COMPONENT_STYLE_NAME = "Viewport";

    protected final ValueProperty<Component> viewContent;
    protected final ValueProperty<Point2D> offsets;
    protected final ValueProperty<Boolean> clampViewToContent;

    public Viewport(){

        clipChildren().set(true);

        viewContent = new ValueProperty<>();
        viewContent.addListener((prop, oldValue, newValue) -> {
            if(oldValue != null){
                removeChild(oldValue);
            }

            if(newValue != null){
                addChild(newValue);
                newValue.layout().positionType().set(PositionType.RELATIVE);
            }
        });

        offsets = new ValueProperty<>(new Point2D(0, 0));
        clampViewToContent = new ValueProperty<>(true);

        var dragStart = new ValueProperty<Point2D>();
        dispatcher.addFilter(MouseButtonEvent.class, evt -> {
            if(evt.isDown() && evt.getButton().equals(MouseButton.PRIMARY)){
                dragStart.set(new Point2D(evt.getX(), evt.getY()));
            }
            return true;
        });
        dispatcher.addFilter(MouseButtonEvent.class, evt -> {
            if(evt.isUp() && evt.getButton().equals(MouseButton.PRIMARY)){
                dragStart.set(null);
            }
            return true;
        });
        //dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mousePressed(MouseButton.PRIMARY), evt -> dragStart.set(new Point2D(evt.getX(), evt.getY())));
        //dispatcher.subscribe(MouseButtonEvent.class, EventFilters.mouseReleased(MouseButton.PRIMARY), evt -> dragStart.set(null));
        dispatcher.subscribe(MouseOverEvent.class,
                evt -> evt.getManager().isDown(MouseButton.PRIMARY),
                evt -> {
                    moveTo(evt.getX() - dragStart.get().x(), evt.getY() - dragStart.get().y());
                }
        );
    }

    public ValueProperty<Component> viewContent(){
        return viewContent;
    }

    public ValueProperty<Point2D> offsets(){
        return offsets;
    }

    public ValueProperty<Boolean> clampViewToContent(){
        return clampViewToContent;
    }

    public void setViewContent(Component component){
        viewContent.set(component);
    }

    public void moveTo(float x, float y){
        viewContent.ifSet(c -> {
            c.layout().offsets().set(Offsets.shift(x, y));
            offsets.set(new Point2D(x, y));
        });
    }

    public void move(float dx, float dy){
        viewContent.ifSet(c -> {

            var shiftedX = offsets.get().x() + dx;
            var shiftedY = offsets.get().y() + dy;

            if(clampViewToContent.get() && ui.isSet()){
                // TODO - this isn't correct
                var bounds = ui.get().getLayout().getLocalBounds(c);
                shiftedX = Range.clamp(bounds.left(), shiftedX, bounds.right());
                shiftedY = Range.clamp(bounds.top(), shiftedY, bounds.bottom());
            }

            System.out.println("Shift dx: %f, dy: %f, X: %f, Y: %f".formatted(dx, dy, shiftedX, shiftedY));
            c.layout().offsets().set(Offsets.shift(shiftedX, shiftedY));
            offsets.set(new Point2D(shiftedX, shiftedY));
        });
    }

}
