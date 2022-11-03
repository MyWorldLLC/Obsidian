package myworld.obsidian.events.scene;

import myworld.obsidian.components.Button;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.scene.Component;

public class ButtonEvent extends ActionEvent {

    public enum Type {
        PRESSED,
        RELEASED,
        CLICKED
    }

    protected final Type type;
    protected final MouseButtonEvent trigger;

    public ButtonEvent(Component component, Type type, MouseButtonEvent trigger){
        super(component);
        this.type = type;
        this.trigger = trigger;
    }

    public Type type(){
        return type;
    }

    public boolean isPressed(){
        return type.equals(Type.PRESSED);
    }

    public boolean isReleased(){
        return type.equals(Type.RELEASED);
    }

    public boolean isClicked(){
        return type.equals(Type.CLICKED);
    }

    public MouseButtonEvent triggerEvent(){
        return trigger;
    }
}
