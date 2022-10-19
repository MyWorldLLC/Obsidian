package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.input.MouseWheelAxis;

public class MouseWheelEvent extends BaseMouseEvent {

    protected final MouseWheelAxis axis;
    protected final float scrollDelta;

    public MouseWheelEvent(InputManager manager, MouseWheelAxis axis, int x, int y, float scrollDelta){
        super(manager, x, y);
        this.axis = axis;
        this.scrollDelta = scrollDelta;
    }

    public MouseWheelAxis getAxis(){
        return axis;
    }

    public float getScrollDelta(){
        return scrollDelta;
    }
}
