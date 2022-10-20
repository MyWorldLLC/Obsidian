package myworld.obsidian.events.dispatch;

import myworld.obsidian.events.BaseEvent;
import myworld.obsidian.events.KeyEvent;
import myworld.obsidian.input.Key;

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

    public static Predicate<KeyEvent> keyIs(Key key){
        return evt -> evt.getKey().equals(key);
    }

    public static Predicate<KeyEvent> keyPressed(Key key){
        return keyPressed(key, true);
    }

    public static Predicate<KeyEvent> keyPressed(Key key, boolean acceptRepeating){
        return evt -> evt.isDown() && (!evt.isRepeating() || acceptRepeating) && evt.getKey().equals(key);
    }

}
