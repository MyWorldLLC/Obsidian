package myworld.obsidian.events.dispatch;

import myworld.obsidian.events.BaseEvent;

import java.util.function.Consumer;

public record OneOffHandler<T extends BaseEvent>(EventDispatcher dispatcher, Class<T> eventType, Consumer<T> handler) implements Consumer<T> {

    @Override
    public void accept(T t) {
        handler.accept(t);
        dispatcher.unsubscribe(eventType, this);
    }
}
