package myworld.obsidian.input;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.events.dispatch.EventDispatcher;
import myworld.obsidian.events.dispatch.EventFilters;
import myworld.obsidian.events.input.*;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InputManager {

    protected final ObsidianUI ui;
    protected final KeyStates state;
    protected final Map<MouseButton, Boolean> mouseState;
    protected final ValueProperty<MousePos> mousePosition;
    protected final ValueProperty<Component> mouseButtonTarget;
    protected final ListProperty<Accelerator> accelerators;
    protected final EventDispatcher dispatcher;

    protected final ValueProperty<Consumer<Exception>> uncaughtExceptionHandler;
    protected final ValueProperty<Consumer<Throwable>> uncaughtThrowableHandler;

    public InputManager(ObsidianUI ui){
        this.ui = ui;
        state = new KeyStates();
        mouseState = new HashMap<>();
        mousePosition = new ValueProperty<>();
        mouseButtonTarget = new ValueProperty<>();
        accelerators = new ListProperty<>();
        dispatcher = new EventDispatcher();
        uncaughtExceptionHandler = new ValueProperty<>();
        uncaughtThrowableHandler = new ValueProperty<>();
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
        try{
            dispatcher.dispatch(evt);
            if(!evt.isConsumed()){
                ui.fireEvent(evt);
            }
            return evt;
        }catch(Exception e){
            if(uncaughtExceptionHandler.isSet()){
                uncaughtExceptionHandler.get().accept(e);
                return evt;
            }else{
                throw e;
            }
        }catch(Throwable t){
            if(uncaughtThrowableHandler.isSet()){
                uncaughtThrowableHandler.get().accept(t);
                return evt;
            }else{
                throw t;
            }
        }
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
        var pressedSet = state.stream()
                .filter(Map.Entry::getValue)
                .map(e -> Key.values()[e.getKey()])
                .collect(Collectors.toSet());
        return pressedSet.equals(keys);
    }

    public ValueProperty<Consumer<Exception>> uncaughtExceptionHandler(){
        return uncaughtExceptionHandler;
    }

    public ValueProperty<Consumer<Throwable>> uncaughtThrowableHandler(){
        return uncaughtThrowableHandler;
    }

    public void addAccelerator(Consumer<AcceleratorEvent> listener, Key... keys){
        Consumer<KeyEvent> handler = (evt) -> {
            listener.accept(new AcceleratorEvent(evt.getManager(), keys));
        };
        accelerators.add(new Accelerator(keys, handler));
        dispatcher.subscribe(KeyEvent.class, EventFilters.accelerator(keys), handler);
    }

    public void removeAccelerators(Key... keys){
        removeAccelerators(
                accelerators.stream()
                .filter(a -> Arrays.equals(a.keys(), keys))
                .toList());
    }

    public void clearAccelerators(){
        removeAccelerators(new ArrayList<>(accelerators));
    }

    protected void removeAccelerators(Collection<Accelerator> toRemove){
        toRemove.forEach(accelerator -> dispatcher.unsubscribe(KeyEvent.class, accelerator.listener()));
        accelerators.removeAll(toRemove);
    }
}
