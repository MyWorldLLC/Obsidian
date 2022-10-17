package myworld.obsidian.events;

public abstract class BaseMouseEvent extends BaseEvent {

    protected final int x;
    protected final int y;

    public BaseMouseEvent(int x, int y){
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
