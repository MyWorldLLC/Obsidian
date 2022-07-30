package myworld.obsidian.display.skin;

import myworld.obsidian.styles.ComponentStyles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UISkin {

    protected final Variables variables;

    protected final Map<String, ComponentSkin> componentSkins;

    public UISkin(){
        variables = new Variables();
        componentSkins = new ConcurrentHashMap<>();
    }
}
