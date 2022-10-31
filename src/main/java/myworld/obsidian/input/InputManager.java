package myworld.obsidian.input;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.events.*;
import myworld.obsidian.events.dispatch.EventDispatcher;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputManager {

    protected final ObsidianUI ui;
    protected final KeyStates state;
    protected final Map<MouseButton, Boolean> mouseState;
    protected final ValueProperty<MousePos> mousePosition;
    protected final ValueProperty<Component> mouseButtonTarget;
    protected final EventDispatcher dispatcher;

    public InputManager(ObsidianUI ui){
        this.ui = ui;
        state = new KeyStates();
        mouseState = new HashMap<>();
        mousePosition = new ValueProperty<>();
        mouseButtonTarget = new ValueProperty<>();
        dispatcher = new EventDispatcher();
    }

    public MousePos getMousePosition(){
        return mousePosition.get();
    }

    public EventDispatcher getDispatcher(){
        return dispatcher;
    }

    public CharacterEvent fireCharacterEvent(char[] characters){
        return handleEvent(new CharacterEvent(this, characters));
    }

    public KeyEvent fireKeyEvent(Key key, boolean isDown) {

        var repeating = state.get(key.ordinal());
        state.set(key.ordinal(), isDown);

        return handleEvent(new KeyEvent(this, key, isDown, repeating));
    }

    public MouseWheelEvent fireMouseWheelEvent(MouseWheelAxis axis, int x, int y, float wheelDelta) {
        return handleEvent(new MouseWheelEvent(this, axis, x, y, wheelDelta));
    }

    public MouseButtonEvent fireMouseButtonEvent(MouseButton button, boolean isDown, int x, int y) {
        mouseState.put(button, isDown);
        return handleEvent(new MouseButtonEvent(this, x, y, button, isDown));
    }

    public MouseMoveEvent fireMouseMoveEvent(int x, int y) {
        int dx = 0;
        int dy = 0;
        var oldPos = getMousePosition();
        if(oldPos != null && oldPos.x() == x && oldPos.y() == y){
            return null; // De-duplicate events at the same pixel coordinates
        }else if(oldPos != null){
            dx = x - oldPos.x();
            dy = y - oldPos.y();
        }
        mousePosition.set(new MousePos(x, y));
        return handleEvent(new MouseMoveEvent(this, x, y, dx, dy));
    }

    public FileDropEvent fireFileDropEvent(String[] filePaths){
        return handleEvent(new FileDropEvent(this, filePaths));
    }

    protected <T extends InputEvent> T handleEvent(T evt){
        dispatcher.dispatch(evt);
        if(!evt.isConsumed()){
            ui.fireEvent(evt);
        }
        return evt;
    }

    public boolean isDown(MouseButton button){
        var down = mouseState.get(button);
        return down != null ? down : false;
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

    public boolean onlyKeysDown(Set<Key> keys){
        return state.stream()
                .filter(Map.Entry::getValue)
                .allMatch(e -> keys.contains(Key.values()[e.getKey()]));
    }

}
