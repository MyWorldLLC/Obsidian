package myworld.obsidian.display.skin;

import myworld.obsidian.styles.ComponentStyles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UISkin {

    protected final Map<String, Object> variables;

    protected final Map<String, StyleClass> componentSkins;

    public UISkin(){
        variables = new ConcurrentHashMap<>();
        componentSkins = new ConcurrentHashMap<>();
    }
}
