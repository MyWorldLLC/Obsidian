package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;

public abstract class BaseMouseEvent extends InputEvent {

    protected final int x;
    protected final int y;

    public BaseMouseEvent(InputManager manager, int x, int y){
        super(manager);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
