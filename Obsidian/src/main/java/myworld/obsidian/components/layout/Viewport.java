package myworld.obsidian.components.layout;

import myworld.obsidian.events.input.MouseButtonEvent;
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
                newValue.layout().positionType().set(PositionType.ABSOLUTE);
                moveTo(0, 0);
            }
        });

        offsets = new ValueProperty<>(new Point2D(0, 0));
        clampViewToContent = new ValueProperty<>(true);

        var dragStart = new ValueProperty<Point2D>();
        dispatcher.addFilter(MouseButtonEvent.class, evt -> {
            if(evt.isDown() && evt.getButton().equals(MouseButton.PRIMARY)){
                dragStart.set(new Point2D(evt.getX(), evt.getY()));
            }else if(evt.isUp() && evt.getButton().equals(MouseButton.PRIMARY)){
                dragStart.set(null);
            }
            return true;
        });

        dispatcher.subscribe(MouseOverEvent.class,
                evt -> evt.getManager().isDown(MouseButton.PRIMARY) && dragStart.isSet(),
                evt -> {
                    var dx = evt.getX() - dragStart.get().x();
                    var dy = evt.getY() - dragStart.get().y();
                    dragStart.set(new Point2D(evt.getX(), evt.getY()));

                    move(dx, dy);
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

            var offsetX = offsets.get().x() + dx;
            var offsetY = offsets.get().y() + dy;

            if(clampViewToContent.get(true)){
                var constrained = constrainedOffsets(offsetX, offsetY);
                offsetX = constrained.x();
                offsetY = constrained.y();
            }

            c.layout().offsets().set(Offsets.shift(offsetX, offsetY));
            offsets.set(new Point2D(offsetX, offsetY));
        });
    }

    public Point2D constrainedOffsets(float x, float y){
        return constrainedOffsets(viewContent.get(), x, y);
    }

    public Point2D constrainedOffsets(Component c, float x, float y){
        var bounds = c.getSceneBounds();
        var viewBounds = this.getSceneBounds();

        var rangeX = Math.abs(bounds.width() - viewBounds.width());
        x = Range.clamp(-rangeX, x, 0);

        var rangeY = Math.abs(bounds.height() - viewBounds.height());
        y = Range.clamp(-rangeY, y, 0);

        return new Point2D(x, y);
    }


}
