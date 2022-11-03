package myworld.obsidian.events.input;

import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.input.InputManager;

public abstract class BaseMouseEvent extends InputEvent {

    protected final Point2D point;

    public BaseMouseEvent(InputManager manager, int x, int y){
        super(manager);
        point = new Point2D(x, y);
    }

    public Point2D getPoint(){
        return point;
    }

    public int getX() {
        return (int) point.x();
    }

    public int getY() {
        return (int) point.y();
    }
}
