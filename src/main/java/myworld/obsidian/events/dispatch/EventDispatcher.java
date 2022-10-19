package myworld.obsidian.events.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EventDispatcher {

    protected static record Registration<T>(Class<T> eventType, Predicate<T> filter, Consumer<T> listener){

        public boolean isEventFilter(){
            return filter != null && listener == null;
        }

        public boolean isEventListener(){
            return listener != null;
        }
    }

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
        registrations.removeIf(r -> r.eventType().equals(eventType) && r.listener() == listener);
    }

    public <T> void addFilter(Class<T> eventType, Predicate<T> filter){
        subscribe(eventType, filter, null);
    }

    public <T> void removeFilter(Class<T> eventType, Predicate<T> filter){
        registrations.removeIf(r -> r.eventType().equals(eventType) && r.filter() == filter);
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(T event){
        registrationsFor(event.getClass())
                .filter(Registration::isEventListener)
                .forEach(r -> {
                    var registration = (Registration<T>) r;
                    if(registration.filter() == null || registration.filter().test(event)){
                        registration.listener().accept(event);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T> boolean filter(T event){
        return registrationsFor(event.getClass())
                .filter(Registration::isEventFilter)
                .allMatch(r -> ((Registration<T>) r).filter().test(event));
    }

    @SuppressWarnings("unchecked")
    protected <T> Stream<Registration<T>> registrationsFor(Class<T> eventType){
        return registrations.stream()
                .filter(r -> r.eventType().equals(eventType))
                .map(r -> (Registration<T>) r);
    }

}
