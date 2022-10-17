package myworld.obsidian.events;

public class MouseMoveEvent extends BaseMouseEvent {

    protected final int dx;
    protected final int dy;

    public MouseMoveEvent(int x, int y, int dx, int dy){
        super(x, y);
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

}
