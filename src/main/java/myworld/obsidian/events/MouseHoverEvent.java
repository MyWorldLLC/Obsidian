package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.scene.Component;

public class MouseHoverEvent extends BaseMouseEvent {

    protected final Component prior;
    protected final Component current;

    public MouseHoverEvent(InputManager manager, int x, int y, Component prior, Component current){
        super(manager, x, y);
        this.prior = prior;
        this.current = current;
    }

    public Component getPrior(){
        return prior;
    }

    public Component getCurrent(){
        return current;
    }

    public boolean entered(Component component){
        return component == current && component != prior;
    }

    public boolean exited(Component component){
        return component == prior && component != current;
    }

    public boolean isOver(Component component){
        return component == current;
    }

    public boolean isHovering(Component component){
        return component == current && component == prior;
    }

}
