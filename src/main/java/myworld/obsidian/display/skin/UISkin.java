package myworld.obsidian.display.skin;

import myworld.obsidian.styles.ComponentStyles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UISkin {

    protected final String name;
    protected final Variables variables;

    protected final Map<String, ComponentSkin> componentSkins;
    protected final Map<String, StyleClass> styles;

    public UISkin(String name){
        this.name = name;
        variables = new Variables();
        componentSkins = new ConcurrentHashMap<>();
        styles = new ConcurrentHashMap<>();
    }

    public String getName(){
        return name;
    }

    public void addComponentSkin(ComponentSkin skin){
        componentSkins.put(skin.name(), skin);
    }

    public ComponentSkin getComponentSkin(String componentName){
        return componentSkins.get(componentName);
    }

    public StyleClass getStyle(String name){
        return styles.get(name);
    }

    public Variables variables(){
        return variables;
    }

    public Map<String, ComponentSkin> componentSkins(){
        return componentSkins;
    }

    public Map<String, StyleClass> styles(){
        return styles;
    }
}
