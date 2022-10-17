package myworld.obsidian.input;

import myworld.obsidian.ObsidianUI;

public class InputManager {

    protected final ObsidianUI ui;
    protected final KeyStates state;

    public InputManager(ObsidianUI ui){
        this.ui = ui;
        state = new KeyStates();
    }

    public void fireKeyEvent(Key key, boolean isDown){
        fireKeyEvent(key, Character.MIN_VALUE, isDown);
    }

    public void fireKeyEvent(Key key, char keyChar, boolean isDown) {

    }

    public void fireMouseWheelEvent(MouseWheelAxis axis, double wheelDelta, int x, int y) {

    }

    public void fireMouseButtonEvent(MouseButton button, boolean isDown, int x, int y) {

    }

    public void fireMouseMoveEvent(int x, int y) {

    }

    public boolean isKeyDown(Key key){
        return state.get(key.ordinal(), false);
    }

    public boolean isLeftControlDown(){
        return isKeyDown(Key.LEFT_CONTROL);
    }

    public boolean isLeftShiftDown(){
        return isKeyDown(Key.LEFT_SHIFT);
    }

    public boolean isLeftAltDown(){
        return isKeyDown(Key.LEFT_ALT);
    }

    public boolean isLeftMetaDown(){
        return isKeyDown(Key.LEFT_META);
    }

    public boolean isRightControlDown(){
        return isKeyDown(Key.RIGHT_CONTROL);
    }

    public boolean isRightShiftDown(){
        return isKeyDown(Key.RIGHT_SHIFT);
    }

    public boolean isRightAltDown(){
        return isKeyDown(Key.RIGHT_ALT);
    }

    public boolean isRightMetaDown(){
        return isKeyDown(Key.RIGHT_META);
    }

    public boolean isControlDown(){
        return isLeftControlDown() || isRightControlDown();
    }

    public boolean isShiftDown(){
        return isLeftShiftDown() || isRightShiftDown();
    }

    public boolean isAltDown(){
        return isLeftAltDown() || isRightAltDown();
    }

    public boolean isMetaDown(){
        return isLeftMetaDown() || isRightMetaDown();
    }

}
