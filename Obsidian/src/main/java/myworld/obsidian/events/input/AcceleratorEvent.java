package myworld.obsidian.events.input;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.input.Key;

public class AcceleratorEvent extends InputEvent {

    protected final KeyEvent cause;
    protected final Key[] keys;

    public AcceleratorEvent(InputManager manager, KeyEvent cause, Key[] keys){
        super(manager);
        this.cause = cause;
        this.keys = keys;
    }

    public KeyEvent getCause(){
        return cause;
    }

    public Key[] getKeys(){
        return keys;
    }

}
