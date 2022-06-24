package myworld.client.obsidian.input;

import com.sun.javafx.embed.AbstractEvents;

public enum MouseButton {
    NONE(AbstractEvents.MOUSEEVENT_NONE_BUTTON),
    PRIMARY(AbstractEvents.MOUSEEVENT_PRIMARY_BUTTON),
    MIDDLE(AbstractEvents.MOUSEEVENT_MIDDLE_BUTTON),
    SECONDARY(AbstractEvents.MOUSEEVENT_SECONDARY_BUTTON),
    BACK(AbstractEvents.MOUSEEVENT_BACK_BUTTON),
    FORWARD(AbstractEvents.MOUSEEVENT_FORWARD_BUTTON);

    private final int fxButton;

    MouseButton(int fxButton){
        this.fxButton = fxButton;
    }

    public int fxButton(){
        return fxButton;
    }
}
