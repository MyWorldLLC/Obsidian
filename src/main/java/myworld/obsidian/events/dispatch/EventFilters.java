package myworld.obsidian.events.dispatch;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.events.KeyEvent;
import myworld.obsidian.events.MouseButtonEvent;
import myworld.obsidian.events.MouseWheelEvent;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.input.MouseWheelAxis;

import java.util.Set;
import java.util.function.Predicate;

public class EventFilters {

    public static <T extends BaseEvent> Predicate<T> when(Predicate<T>... predicates){
        return evt -> {
            for(var p : predicates){
                if(evt.isConsumed() || !p.test(evt)){
                    return false;
                }
            }
            return true;
        };
    }

    public static <T extends BaseEvent> Predicate<T> whenAny(Predicate<T>... predicates){
        return evt -> {
            for(var p : predicates){
                if(!evt.isConsumed() && p.test(evt)){
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<KeyEvent> keyIs(Key key){
        return evt -> evt.getKey().equals(key);
    }

    public static Predicate<KeyEvent> keyPressed(Key key){
        return keyPressed(key, true);
    }

    public static Predicate<KeyEvent> keyPressed(Key key, boolean acceptRepeating){
        return evt -> evt.isDown() && (!evt.isRepeating() || acceptRepeating) && evt.getKey().equals(key);
    }

    public static Predicate<KeyEvent> accelerator(Key... keys){
        return evt -> evt.getManager().onlyKeysDown(Set.of(keys));
    }

    public static Predicate<MouseButtonEvent> mousePressed(MouseButton button){
        return evt -> evt.isDown() && evt.getButton().equals(button);
    }

    public static Predicate<MouseButtonEvent> mousePressed(){
        return mousePressed(MouseButton.PRIMARY);
    }

    public static Predicate<MouseButtonEvent> mouseReleased(MouseButton button){
        return evt -> evt.isUp() && evt.getButton().equals(button);
    }

    public static Predicate<MouseButtonEvent> mouseReleased(){
        return mouseReleased(MouseButton.PRIMARY);
    }

    public static Predicate<MouseWheelEvent> mouseScrolled(MouseWheelAxis axis){
        return evt -> evt.getAxis().equals(axis);
    }

    public static Predicate<MouseWheelEvent> mouseScrolled(){
        return mouseScrolled(MouseWheelAxis.VERTICAL);
    }

}
