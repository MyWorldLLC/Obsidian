package myworld.obsidian.util;

import java.util.HashMap;
import java.util.Map;

public class InputStates {

    protected final Map<Integer, Boolean> states;

    public InputStates(){
        states = new HashMap<>();
    }

    public boolean get(int key, boolean defaultState){
        return states.getOrDefault(key, defaultState);
    }

    public boolean get(int key){
        return get(key, false);
    }

    public boolean set(int key, boolean state){
        var former = states.put(key, state);
        return former != null && former;
    }
}
