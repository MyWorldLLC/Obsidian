package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;

public class MouseWheelEvent extends BaseMouseEvent {

    protected final float scrollDelta;

    public MouseWheelEvent(InputManager manager, int x, int y, float scrollDelta){
        super(manager, x, y);
        this.scrollDelta = scrollDelta;
    }

    public float getScrollDelta(){
        return scrollDelta;
    }
}
