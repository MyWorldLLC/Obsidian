package myworld.obsidian.events;

public class KeyEvent extends BaseEvent {

    protected final int keyCode;
    protected final char character;
    protected final boolean isDown;
    protected final boolean isRepeating;

    public KeyEvent(int keyCode, boolean isDown, boolean isRepeating){
        this(keyCode, Character.MIN_VALUE, isDown, isRepeating);
    }

    public KeyEvent(int keyCode, char character, boolean isDown, boolean isRepeating){
        this.keyCode = keyCode;
        this.character = character;
        this.isDown = isDown;
        this.isRepeating = isRepeating;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public char getCharacter() {
        return character;
    }

    public boolean isCharacterEvent(){
        return character != Character.MIN_VALUE;
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
