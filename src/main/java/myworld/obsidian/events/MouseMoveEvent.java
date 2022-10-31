package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;

public class MouseMoveEvent extends BaseMouseEvent {

    protected final int dx;
    protected final int dy;

    public MouseMoveEvent(InputManager manager, int x, int y, int dx, int dy){
        super(manager, x, y);
        this.dx = dx;
        this.dy = dy;
    }

    public int getDeltaX() {
        return dx;
    }

    public int getDeltaY() {
        return dy;
    }

}
