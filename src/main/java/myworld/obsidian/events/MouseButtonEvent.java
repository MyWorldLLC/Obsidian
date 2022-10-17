package myworld.obsidian.events;

import myworld.obsidian.input.MouseButton;

public class MouseButtonEvent extends BaseMouseEvent {

    protected final MouseButton button;
    protected final boolean down;

    public MouseButtonEvent(int x, int y, MouseButton button, boolean down){
        super(x, y);
        this.button = button;
        this.down = down;
    }

    public MouseButton getButton(){
        return button;
    }

    public boolean isDown(){
        return down;
    }

    public boolean isUp(){
        return !down;
    }
}
