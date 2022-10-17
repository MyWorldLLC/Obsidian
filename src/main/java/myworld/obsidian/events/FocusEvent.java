package myworld.obsidian.events;

import myworld.obsidian.scene.Component;

public class FocusEvent extends BaseEvent {

    protected final Component oldFocus;
    protected final Component newFocus;

    public FocusEvent(Component oldFocus, Component newFocus){
        this.oldFocus = oldFocus;
        this.newFocus = newFocus;
    }

    public Component getOldFocus(){
        return oldFocus;
    }

    public Component getNewFocus(){
        return newFocus;
    }

}
