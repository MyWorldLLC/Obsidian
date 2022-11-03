package myworld.obsidian.events.scene;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.scene.Component;

public class ActionEvent extends BaseEvent {

    protected final Component component;

    public ActionEvent(Component component){
        this.component = component;
    }

    public Component component(){
        return component;
    }
}
