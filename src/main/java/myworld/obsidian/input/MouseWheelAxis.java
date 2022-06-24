package myworld.client.obsidian.input;

import com.sun.javafx.embed.AbstractEvents;

public enum MouseWheelAxis {
    VERTICAL(AbstractEvents.MOUSEEVENT_VERTICAL_WHEEL),
    HORIZONTAL(AbstractEvents.MOUSEEVENT_HORIZONTAL_WHEEL);

    private final int fxAxis;

    MouseWheelAxis(int fxAxis){
        this.fxAxis = fxAxis;
    }

    public int fxAxis(){
        return fxAxis;
    }
}
