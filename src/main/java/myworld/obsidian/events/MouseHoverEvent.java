package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.scene.Component;

public class MouseHoverEvent extends BaseMouseEvent {

    protected final Component oldHover;
    protected final Component newHover;

    public MouseHoverEvent(InputManager manager, int x, int y, Component oldHover, Component newHover){
        super(manager, x, y);
        this.oldHover = oldHover;
        this.newHover = newHover;
    }

    public Component getOldHover(){
        return oldHover;
    }

    public Component getNewHover(){
        return newHover;
    }

    public boolean entered(Component component){
        return newHover == component;
    }

    public boolean exited(Component component){
        return oldHover == component;
    }

}
