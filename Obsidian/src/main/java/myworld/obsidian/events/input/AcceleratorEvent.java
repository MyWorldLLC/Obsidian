package myworld.obsidian.events.input;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.input.Key;

public class AcceleratorEvent extends InputEvent {

    protected final Key[] keys;

    public AcceleratorEvent(InputManager manager, Key[] keys){
        super(manager);
        this.keys = keys;
    }

    public Key[] getKeys(){
        return keys;
    }

}
