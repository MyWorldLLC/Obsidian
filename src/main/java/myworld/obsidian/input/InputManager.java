package myworld.obsidian.input;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.events.*;
import myworld.obsidian.events.dispatch.EventDispatcher;
import myworld.obsidian.properties.ValueProperty;

public class InputManager {

    protected final ObsidianUI ui;
    protected final KeyStates state;
    protected final ValueProperty<MousePos> mousePosition;
    protected final EventDispatcher dispatcher;

    public InputManager(ObsidianUI ui){
        this.ui = ui;
        state = new KeyStates();
        mousePosition = new ValueProperty<>();
        dispatcher = new EventDispatcher();
    }

    public MousePos getMousePosition(){
        return mousePosition.get();
    }

    public EventDispatcher getDispatcher(){
        return dispatcher;
    }

    public void fireCharacterEvent(char[] characters){
        handleEvent(new CharacterEvent(this, characters));
    }

    public void fireKeyEvent(Key key, boolean isDown) {

        var repeating = state.get(key.ordinal());
        state.set(key.ordinal(), isDown);

        handleEvent(new KeyEvent(this, key, isDown, repeating));
    }

    public void fireMouseWheelEvent(MouseWheelAxis axis, int x, int y, float wheelDelta) {
        handleEvent(new MouseWheelEvent(this, axis, x, y, wheelDelta));
    }

    public void fireMouseButtonEvent(MouseButton button, boolean isDown, int x, int y) {
        handleEvent(new MouseButtonEvent(this, x, y, button, isDown));
    }

    public void fireMouseMoveEvent(int x, int y) {
        int dx = 0;
        int dy = 0;
        var oldPos = getMousePosition();
        if(oldPos != null){
            dx = x - oldPos.x();
            dy = y - oldPos.y();
        }
        mousePosition.set(new MousePos(x, y));
        handleEvent(new MouseMoveEvent(this, x, y, dx, dy));
    }

    protected void handleEvent(InputEvent evt){
        dispatcher.dispatch(evt);
        if(!evt.isConsumed()){
            ui.fireEvent(evt);
        }
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
