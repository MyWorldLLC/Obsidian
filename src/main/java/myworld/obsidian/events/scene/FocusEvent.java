package myworld.obsidian.events.scene;

import myworld.obsidian.events.BaseEvent;
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

    public boolean gainedFocus(Component component){
        return component == newFocus && component != oldFocus;
    }

    public boolean isFocused(Component component){
        return component == newFocus;
    }

    public boolean lostFocus(Component component){
        return component != newFocus && component == oldFocus;
    }

}
