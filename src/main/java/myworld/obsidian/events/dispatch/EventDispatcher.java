package myworld.obsidian.events.dispatch;

import myworld.obsidian.events.BaseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    public <T extends BaseEvent> void subscribe(Class<T> eventType, Predicate<T> filter, Consumer<T> listener){
        registrations.add(new Registration<>(eventType, filter, listener));
    }

    public <T extends BaseEvent> void subscribe(Class<T> eventType, Consumer<T> listener){
        subscribe(eventType, null, listener);
    }

    public <T extends BaseEvent> void unsubscribe(Class<T> eventType, Consumer<T> listener){
        registrations.removeIf(r -> r.eventType().equals(eventType) && r.listener() == listener);
    }

    public <T extends BaseEvent> void addFilter(Class<T> eventType, Predicate<T> filter){
        subscribe(eventType, filter, null);
    }

    public <T extends BaseEvent> void removeFilter(Class<T> eventType, Predicate<T> filter){
        registrations.removeIf(r -> r.eventType().equals(eventType) && r.filter() == filter);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEvent> void dispatch(T event){
        for(var r : registrationsFor(event.getClass(), Registration::isEventListener)){
            var registration = (Registration<T>) r;

            if(event.isConsumed()){
                break;
            }

            if(r.filter() == null || registration.filter().test(event)){
                registration.listener().accept(event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEvent> boolean filter(T event){
        for(var r : registrationsFor(event.getClass(), Registration::isEventFilter)){
            // Stop evaluating filters once event is consumed or one filter returns false
            if(event.isConsumed() || !((Registration<T>) r).filter().test(event)){
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseEvent> List<Registration<T>> registrationsFor(Class<T> eventType, Predicate<Registration<?>> whichRegistrations){
        return registrations.stream()
                .filter(r -> r.eventType().equals(eventType))
                .filter(whichRegistrations)
                .map(r -> (Registration<T>) r)
                .collect(Collectors.toList());
    }

}
