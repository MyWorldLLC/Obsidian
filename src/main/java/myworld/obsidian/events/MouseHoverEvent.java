package myworld.obsidian.events;

import myworld.obsidian.scene.Component;

public class MouseHoverEvent extends BaseMouseEvent {

    protected final Component oldHover;
    protected final Component newHover;

    public MouseHoverEvent(int x, int y, Component oldHover, Component newHover){
        super(x, y);
        this.oldHover = oldHover;
        this.newHover = newHover;
    }

    public Component getOldHover(){
        return oldHover;
    }

    public Component getNewHover(){
        return newHover;
    }

}
