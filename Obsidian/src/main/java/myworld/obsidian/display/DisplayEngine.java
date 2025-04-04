package myworld.obsidian.display;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.display.skin.*;
import myworld.obsidian.geometry.Dimension2D;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.TextStyle;
import myworld.obsidian.util.LogUtil;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

public abstract class DisplayEngine implements AutoCloseable {

    private static final Logger log = LogUtil.loggerFor(DisplayEngine.class);

    protected final ValueProperty<Dimension2D> dimensions;

    protected final ListChangeListener<Component> sceneListener;

    public DisplayEngine(int width, int height){
        dimensions = new ValueProperty<>(new Dimension2D(width, height));
        sceneListener = this::onSceneChange;
    }

    protected void onSceneChange(ListProperty<Component> prop, int index, Component oldValue, Component newValue){
        if(oldValue == null && newValue != null){
            newValue.children().addListener(sceneListener);
        }else if(oldValue != null && newValue == null){
            oldValue.children().removeListener(sceneListener);
        }
    }

    public ValueProperty<Dimension2D> getDimensions(){
        return dimensions;
    }

    public void registerRoot(Component root){
        root.children().addListener(sceneListener);
    }

    public void unregisterRoot(Component root){
        root.children().removeListener(sceneListener);
    }

    public abstract void clear(ColorRGBA clearColor);

    public abstract void resize(int width, int height);

    public abstract void render(ObsidianUI ui, Component component, UISkin uiSkin);

    public abstract void enableRenderDebug(ColorRGBA debugColor);

    public abstract void disableRenderDebug();

    public abstract boolean isRenderDebugEnabled();

    public abstract void flush();

    @Override
    public abstract void close();

    public abstract TextRuler getTextRuler(String fontFamily, TextStyle style, float size);

    public abstract TextRuler getTextRuler(StyleClass textStyle, Variables v);

    public StyleClass resolveStyle(StyleClass style, Component component, UISkin uiSkin){
        return resolveStyle(style, component, component.generateRenderVars(), uiSkin);
    }

    public StyleClass resolveStyle(StyleClass style, Component component, Variables renderVars, UISkin uiSkin){
        return mixStyles(style, renderVars, new StyleLookup(uiSkin.getComponentSkin(component.styleName().get()), uiSkin));
    }

    public StyleClass resolveStyles(Collection<StyleClass> styles, Component component, UISkin uiSkin){
        return resolveStyle(StyleClass.merge(styles), component, component.generateRenderVars(), uiSkin);
    }

    public StyleClass resolveStyles(Collection<StyleClass> styles, Component component, Variables renderVars, UISkin uiSkin){
        return mixStyles(StyleClass.merge(styles), renderVars, new StyleLookup(uiSkin.getComponentSkin(component.styleName().get()), uiSkin));
    }

    protected StyleClass mixStyles(StyleClass s, Variables renderVars, StyleLookup styleLookup){
        List<String> mixStyles = s.rule(StyleRules.STYLES, renderVars);
        var result = s;

        if(mixStyles != null){
            var combined = new StyleClass();

            // Iteratively merge together the imported styles, with conflicting rules from later
            // referenced ones overriding rules from previously referenced ones
            for(String mixName : mixStyles){
                var mixStyle = styleLookup.getStyle(mixName);

                if(mixStyle == null){
                    log.log(WARNING, "Could not find style {0}", mixName);
                    continue;
                }

                combined = StyleClass.merge(combined, mixStyles(mixStyle, renderVars, styleLookup));
            }

            // Note that local class rules override mixed-in style rules
            result = StyleClass.merge(combined, s);
        }

        return result;
    }

    public void loadResources(UISkin skin){
        loadFonts(skin);
        loadImages(skin);
        loadSvgs(skin);
    }

    protected abstract void loadFonts(UISkin skin);
    protected abstract void loadImages(UISkin skin);
    protected abstract void loadSvgs(UISkin skin);
}
