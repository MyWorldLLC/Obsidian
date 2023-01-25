package myworld.obsidian.display.skin;

import myworld.obsidian.display.ObsidianImage;
import myworld.obsidian.display.ObsidianSvg;
import myworld.obsidian.util.ResourceCache;

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
    protected final ResourceCache<String, ObsidianImage> imageCache;
    protected final ResourceCache<String, ObsidianSvg> svgCache;
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
        imageCache = new ResourceCache<>();
        svgCache = new ResourceCache<>();
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

    public StyleClass getStyle(String componentName, String styleName){
        var component = getComponentSkin(componentName);
        if(component != null){
            var style = component.findNamed(styleName);
            if(style != null){
                return style;
            }
        }

        return getStyle(styleName);
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

    public void setStyle(String name, StyleClass style){
        styles.put(name, style);
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

    public void cache(String path, ObsidianSvg svg){
        svgCache.cache(path, svg);
    }

    public void cache(String path, ObsidianImage image){
        imageCache.cache(path, image);
    }

    public ObsidianSvg getCachedSvg(String path){
        return svgCache.get(path);
    }

    public ObsidianImage getCachedImage(String path){
        return imageCache.get(path);
    }

    public void clearCachedSvg(String path){
        svgCache.remove(path);
    }

    public void clearCachedImage(String path){
        imageCache.remove(path);
    }

    public void clearCachedResources(){
        svgCache.clear();
        imageCache.clear();
    }

}
