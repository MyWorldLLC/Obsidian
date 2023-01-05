package myworld.obsidian.display.skin.builder;

import myworld.obsidian.display.skin.ComponentSkin;
import myworld.obsidian.display.skin.ResourceResolver;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.UISkin;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class UISkinBuilder {

    protected final UISkin uiSkin;

    protected UISkinBuilder(String name, ResourceResolver resolver){
        uiSkin = new UISkin(name, resolver);
    }

    public UISkinBuilder withComponent(ComponentSkin component){
        uiSkin.addComponentSkin(component);
        return this;
    }

    public UISkinBuilder withComponents(Collection<ComponentSkin> components){
        return handleBulk(components, this::withComponent);
    }

    public UISkinBuilder withVar(String name, Object value){
        uiSkin.variables().set(name, value);
        return this;
    }

    public UISkinBuilder withVars(Map<String, Object> vars){
        uiSkin.variables().set(vars);
        return this;
    }

    public UISkinBuilder withStyleClass(StyleClass style){
        uiSkin.styles().put(style.name(), style);
        return this;
    }

    public UISkinBuilder withStyleClasses(Collection<StyleClass> styles){
        return handleBulk(styles, this::withStyleClass);
    }

    public UISkinBuilder withFont(String resourcePath){
        uiSkin.fonts().add(resourcePath);
        return this;
    }

    public UISkinBuilder withFonts(Collection<String> resourcePaths){
        return handleBulk(resourcePaths, this::withFont);
    }

    public UISkinBuilder withImage(String resourcePath){
        uiSkin.images().add(resourcePath);
        return this;
    }

    public UISkinBuilder withImages(Collection<String> resourcePaths){
        return handleBulk(resourcePaths, this::withImage);
    }

    public UISkinBuilder withSvg(String resourcePath){
        uiSkin.svgs().add(resourcePath);
        return this;
    }

    public UISkinBuilder withSvgs(Collection<String> resourcePaths){
        return handleBulk(resourcePaths, this::withSvg);
    }

    public UISkin build(){
        return uiSkin;
    }

    protected <T> UISkinBuilder handleBulk(Collection<T> c, Consumer<T> handler){
        c.forEach(handler);
        return this;
    }

    public static UISkinBuilder create(String name, ResourceResolver resolver){
        return new UISkinBuilder(name, resolver);
    }

}
