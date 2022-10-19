package myworld.obsidian.events;

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
