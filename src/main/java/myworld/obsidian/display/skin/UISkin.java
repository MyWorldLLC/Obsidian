package myworld.obsidian.display.skin;

import myworld.obsidian.styles.ComponentStyles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UISkin {

    protected final String name;
    protected final Variables variables;

    protected final Map<String, ComponentSkin> componentSkins;

    public UISkin(String name){
        this.name = name;
        variables = new Variables();
        componentSkins = new ConcurrentHashMap<>();
    }

    public String getName(){
        return name;
    }

    public ComponentSkin getComponentSkin(String componentName){
        return componentSkins.get(componentName);
    }
}
