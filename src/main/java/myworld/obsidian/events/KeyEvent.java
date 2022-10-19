package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;
import myworld.obsidian.input.Key;

public class KeyEvent extends InputEvent {

    protected final Key key;
    protected final boolean isDown;
    protected final boolean isRepeating;

    public KeyEvent(InputManager manager, Key key, boolean isDown, boolean isRepeating){
        super(manager);
        this.key = key;
        this.isDown = isDown;
        this.isRepeating = isRepeating;
    }

    public Key getKey() {
        return key;
    }

    public boolean isDown() {
        return isDown;
    }

    public boolean isUp(){
        return !isDown;
    }

    public boolean isRepeating() {
        return isRepeating;
    }
}
