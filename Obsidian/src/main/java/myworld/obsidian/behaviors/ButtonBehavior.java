package myworld.obsidian.behaviors;

import myworld.obsidian.events.dispatch.EventDispatcher;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.scene.ButtonEvent;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import java.util.Objects;
import java.util.function.Consumer;

import static myworld.obsidian.events.dispatch.EventFilters.mousePressed;
import static myworld.obsidian.events.dispatch.EventFilters.mouseReleased;

public class ButtonBehavior implements RemovableBehavior<Component> {

    public static ButtonBehavior create(Component c){
        return create(MouseButton.PRIMARY, c);
    }

    public static ButtonBehavior create(MouseButton button, Component c){
        var behavior = new ButtonBehavior(button);
        behavior.apply(c);
        return behavior;
    }

    protected final MouseButton button;
    protected final Consumer<MouseButtonEvent> pressListener;
    protected final Consumer<MouseButtonEvent> releaseListener;
    protected final ValueProperty<Boolean> pressed;
    protected final ValueProperty<Boolean> consume;
    protected  final ValueProperty<Component> boundComponent;

    public ButtonBehavior(){
        this(MouseButton.PRIMARY);
    }

    public ButtonBehavior(MouseButton button){
        this.button = button;

        pressListener = this::onPress;
        releaseListener = this::onRelease;

        pressed = new ValueProperty<>(false);
        consume = new ValueProperty<>(true);
        boundComponent = new ValueProperty<>(null);
    }

    public MouseButton button(){
        return button;
    }

    public ValueProperty<Boolean> pressed(){
        return pressed;
    }

    public ValueProperty<Boolean> consume(){
        return consume;
    }

    public ValueProperty<Component> component(){
        return boundComponent;
    }

    @Override
    public Behavior<Component> apply(Component c) {
        Objects.requireNonNull(c, "Component must not be null");
        if(boundComponent.isSet()){
            throw new IllegalStateException("Button behavior is already active on another component");
        }

        boundComponent.set(c);

        c.dispatcher().subscribe(MouseButtonEvent.class, mousePressed(MouseButton.PRIMARY), pressListener);
        c.dispatcher().subscribe(MouseButtonEvent.class, mouseReleased(MouseButton.PRIMARY), releaseListener);

        return this;
    }

    @Override
    public Behavior<Component> remove(Component c) {
        Objects.requireNonNull(c, "Component must not be null");
        if(!boundComponent.is(c)){
            throw new IllegalStateException("Button behavior is not active on the provided component");
        }

        boundComponent.ifSet(component -> {
            component.dispatcher().unsubscribe(MouseButtonEvent.class, pressListener);
            component.dispatcher().unsubscribe(MouseButtonEvent.class, releaseListener);
        });

        boundComponent.set(null);

        return this;
    }

    protected void onPress(MouseButtonEvent evt){
        pressed.set(true);
        dispatcher().dispatch(new ButtonEvent(boundComponent.get(), ButtonEvent.Type.PRESSED, evt));

        if(consume.get(true)){
            evt.consume();
        }
    }

    protected void onRelease(MouseButtonEvent evt){
        if(pressed.get(false)){
            pressed.set(false);

            var component = boundComponent.get();

            dispatcher().dispatch(new ButtonEvent(component, ButtonEvent.Type.RELEASED, evt));

            if(component.ui().get().isOver(evt.getPoint(), component)){
                dispatcher().dispatch(new ButtonEvent(component, ButtonEvent.Type.CLICKED, evt));
            }

            if(consume.get(true)){
                evt.consume();
            }
        }
    }

    protected EventDispatcher dispatcher(){
        return boundComponent.get().dispatcher();
    }

}
