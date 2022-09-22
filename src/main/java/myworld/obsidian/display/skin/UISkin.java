package myworld.obsidian.display.skin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UISkin {

    protected final String name;
    protected final Variables variables;

    protected final Map<String, ComponentSkin> componentSkins;
    protected final Map<String, StyleClass> styles;
    protected final List<String> fonts;

    public UISkin(String name){
        this.name = name;
        variables = new Variables();
        componentSkins = new ConcurrentHashMap<>();
        styles = new ConcurrentHashMap<>();
        fonts = new CopyOnWriteArrayList<>();
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

    public void addFonts(String... fontPath){
        fonts.addAll(Arrays.asList(fontPath));
    }

    public void addFonts(List<String> fontPaths){
        fonts.addAll(fontPaths);
    }

    public Map<String, ComponentSkin> componentSkins(){
        return componentSkins;
    }

    public Map<String, StyleClass> styles(){
        return styles;
    }

    public List<String> fonts(){
        return fonts;
    }
}
