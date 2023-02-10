package myworld.obsidian.events.dispatch;

import myworld.obsidian.events.BaseEvent;

import java.util.function.Consumer;

public class EventHandlers {

    public static <T extends BaseEvent> Consumer<T> consuming(Consumer<T> handler){
        return evt -> {
            handler.accept(evt);
            evt.consume();
        };
    }

    public static <T extends BaseEvent> Consumer<T> consuming(Runnable handler){
        return consuming(evt -> handler.run());
    }

}
