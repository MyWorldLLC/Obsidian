package myworld.obsidian.events.input;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.input.InputManager;

public class InputEvent extends BaseEvent {

    protected final InputManager input;

    public InputEvent(InputManager input){
        this.input = input;
    }

    public InputManager getManager(){
        return input;
    }
}
