package myworld.obsidian.events;

public class BaseEvent {

    private volatile boolean consumed = false;

    public void consume(){
        consumed = true;
    }

    public boolean isConsumed(){
        return consumed;
    }

}
