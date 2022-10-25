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
    protected final List<String> svgs;
    protected final List<String> images;
    protected final ResourceResolver resolver;

    public UISkin(String name, ResourceResolver resolver){
        this.name = name;
        this.resolver = resolver;
        variables = new Variables();
        componentSkins = new ConcurrentHashMap<>();
        styles = new ConcurrentHashMap<>();
        fonts = new CopyOnWriteArrayList<>();
        svgs = new CopyOnWriteArrayList<>();
        images = new CopyOnWriteArrayList<>();
    }

    public String getName(){
        return name;
    }

    public ResourceResolver getResolver(){
        return resolver;
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

    public void addFonts(List<String> fontPaths){
        fonts.addAll(fontPaths);
    }

    public void addImages(List<String> imagePaths){
        images.addAll(imagePaths);
    }

    public void addSvgs(List<String> svgPaths){
        svgs.addAll(svgPaths);
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

    public List<String> images(){
        return images;
    }

    public List<String> svgs(){
        return svgs;
    }

}
