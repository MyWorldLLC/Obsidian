package myworld.obsidian.scene.events;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.scene.Component;

public class AttachEvent extends BaseEvent {

    protected final Component parent;
    protected final Component child;

    public AttachEvent(Component parent, Component child){
        this.parent = parent;
        this.child = child;
    }

    public Component parent(){
        return parent;
    }

    public Component child(){
        return child;
    }

}
