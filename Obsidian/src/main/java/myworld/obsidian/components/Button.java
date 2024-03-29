package myworld.obsidian.components;

import myworld.obsidian.behaviors.ButtonBehavior;
import myworld.obsidian.components.text.TextDisplay;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.scene.ButtonEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.layout.Offsets;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static myworld.obsidian.events.dispatch.EventFilters.mousePressed;
import static myworld.obsidian.events.dispatch.EventFilters.mouseReleased;

public class Button extends Component {

    public static final String COMPONENT_STYLE_NAME = "Button";
    public static final String PRESSED_DATA_NAME = "pressed";
    protected final ButtonBehavior behavior;

    public Button(){
        styleName.set(COMPONENT_STYLE_NAME);

        behavior = ButtonBehavior.create(MouseButton.PRIMARY, this);

        renderVars.put(PRESSED_DATA_NAME, behavior.pressed());
    }

    public Button addButtonListener(Consumer<ButtonEvent> listener){
        return addButtonListener(null, listener);
    }

    public Button addButtonListener(Predicate<ButtonEvent> filter, Consumer<ButtonEvent> listener){
        dispatcher.subscribe(ButtonEvent.class, filter, listener);
        return this;
    }

    public Button onClick(Consumer<ButtonEvent> listener){
        return addButtonListener(ButtonEvent::isClicked, listener);
    }

    public Button onPress(Consumer<ButtonEvent> listener){
        return addButtonListener(ButtonEvent::isPressed, listener);
    }

    public Button onRelease(Consumer<ButtonEvent> listener){
        return addButtonListener(ButtonEvent::isReleased, listener);
    }

    public void removeListener(Consumer<ButtonEvent> listener){
        dispatcher.unsubscribe(ButtonEvent.class, listener);
    }

    public ValueProperty<Boolean> pressed(){
        return behavior.pressed();
    }

    public boolean isPressed(){
        return pressed().get();
    }

    public static Button textButton(Text text){
        return new Button().withChildren(new TextDisplay(text));
    }

    public static Button graphicButton(Component... graphics){
        return new Button().withChildren(graphics);
    }

}
