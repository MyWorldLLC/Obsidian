package myworld.obsidian.events.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EventDispatcher {

    protected static record Registration<T>(Class<T> eventType, Predicate<T> filter, Consumer<T> listener){}

    protected final List<Registration<?>> registrations;

    public EventDispatcher(){
        registrations = Collections.synchronizedList(new ArrayList<>());
    }

    public <T> void subscribe(Class<T> eventType, Predicate<T> filter, Consumer<T> listener){
        registrations.add(new Registration<>(eventType, filter, listener));
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener){
        subscribe(eventType, null, listener);
    }

    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener){
        registrations.removeIf(r -> r.eventType().equals(eventType) && r.listener == listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(T event){
        registrationsFor(event.getClass())
                .forEach(r -> ((Registration<T>)r).listener().accept(event));
    }

    @SuppressWarnings("unchecked")
    public <T> boolean filter(T event){
        return registrationsFor(event.getClass())
                .filter(r -> r.filter() != null)
                .allMatch(r -> ((Registration<T>) r).filter().test(event));
    }

    @SuppressWarnings("unchecked")
    protected <T> Stream<Registration<T>> registrationsFor(Class<T> eventType){
        return registrations.stream()
                .filter(r -> r.eventType().equals(eventType))
                .map(r -> (Registration<T>) r);
    }

}
