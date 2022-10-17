package myworld.obsidian.events;

public class MouseWheelEvent extends BaseMouseEvent {

    protected final float scrollDelta;

    public MouseWheelEvent(int x, int y, float scrollDelta){
        super(x, y);
        this.scrollDelta = scrollDelta;
    }

    public float getScrollDelta(){
        return scrollDelta;
    }
}
