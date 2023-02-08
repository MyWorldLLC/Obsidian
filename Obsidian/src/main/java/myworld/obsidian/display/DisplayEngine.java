/*
 *    Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.obsidian.display;

import io.github.humbleui.skija.svg.SVGDOM;
import io.github.humbleui.types.IRect;
import myworld.obsidian.ObsidianUI;
import myworld.obsidian.display.skin.*;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Dimension2D;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.impl.Library;
import myworld.obsidian.text.TextStyle;
import myworld.obsidian.text.Typeset;
import myworld.obsidian.util.LogUtil;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;


public class DisplayEngine implements AutoCloseable {

    private static final Logger log = LogUtil.loggerFor(DisplayEngine.class);

    static {
        if("false".equals(System.getProperty("skija.staticLoad"))){
            Library.load(); // Load Skia lib if it's not statically linked
        }
    }

    protected final ValueProperty<Dimension2D> dimensions;

    protected final SurfaceManager surfaceManager;

    protected final Typeset fonts;
    protected final Renderer renderer;

    protected final ListChangeListener<Component> sceneListener;

    public static DisplayEngine createForGL(int width, int height, int samples, int framebufferHandle){
        return new DisplayEngine(width, height, new GLSurfaceManager(width, height, samples, framebufferHandle));
    }

    public static DisplayEngine createForCpu(int width, int height){
        return new DisplayEngine(width, height, new CpuSurfaceManager(width, height));
    }

    protected DisplayEngine(int width, int height, SurfaceManager surfaceManager){
        this.surfaceManager = surfaceManager;

        dimensions = new ValueProperty<>(new Dimension2D(width, height));

        sceneListener = this::onSceneChange;

        fonts = new Typeset();
        renderer = new Renderer(dimensions);
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

    protected void onSceneChange(ListProperty<Component> prop, int index, Component oldValue, Component newValue){
        if(oldValue == null && newValue != null){
            newValue.children().addListener(sceneListener);
        }else if(oldValue != null && newValue == null){
            oldValue.children().removeListener(sceneListener);
        }
    }

    public void clear(ColorRGBA clearColor){
        getCanvas().clear(clearColor.toARGB());
    }

    public Canvas getCanvas(){
        return getSurface().getCanvas();
    }

    public void resize(int width, int height){
        surfaceManager.resize(width, height);
        dimensions.set(new Dimension2D(width, height));
    }

    public SurfaceManager getSurfaceManager(){
        return surfaceManager;
    }

    public Image snapshot(){
        return getSurface().makeImageSnapshot();
    }

    public Image snapshot(Bounds2D area){
        return getSurface().makeImageSnapshot(
                new IRect(
                (int) area.left(),
                (int) area.top(),
                (int) area.right(),
                (int) area.bottom()
                ));
    }

    public Bitmap makeWritableBitmap(){
        return makeWritableBitmap(getSurface().getWidth(), getSurface().getHeight());
    }

    public Bitmap makeWritableBitmap(int width, int height){
        final var bytesPerRow = width * getSurface().getImageInfo().getBytesPerPixel();
        var data = new byte[bytesPerRow * height];
        return Bitmap.makeFromImage(Image.makeRaster(getSurface().getImageInfo(), data, bytesPerRow));
    }

    public Bitmap getSurfacePixels(Bitmap target){
        return getSurfacePixels(target, 0, 0);
    }

    public Bitmap getSurfacePixels(Bitmap target, int srcX, int srcY){
        getSurface().readPixels(target, srcX, srcY);
        return target;
    }

    protected static ImageInfo getImageInfo(int width, int height){
        var colorInfo = new ColorInfo(ColorType.RGBA_8888, ColorAlphaType.PREMUL, ColorSpace.getSRGB());
        return new ImageInfo(colorInfo, width, height);
    }

    protected ImageInfo getImageInfo(){
        return getImageInfo(getSurface().getWidth(), getSurface().getHeight());
    }

    public void render(ObsidianUI ui, Component component, UISkin uiSkin){

        renderer.setActiveSkin(uiSkin);

        var skin = uiSkin.getComponentSkin(component.styleName().get());

        component.preRenderers().forEach(Runnable::run);

        if(skin != null && !component.isLayoutOnly()){

            var renderVars = component.generateRenderVars();

            var backgroundLayers = new ArrayList<StyleClass>();
            var foregroundLayers = new ArrayList<StyleClass>();

            var styleLookup = new StyleLookup(skin, uiSkin);

            // For each named layer, merge all style classes applicable for the given renderVars
            // (including state-independent classes)
            for(var layer : skin.layerNames()){

                var styleClasses = skin.activeForLayer(layer, renderVars, s -> mixStyles(s, renderVars, styleLookup));

                var background = styleClasses.stream()
                        .filter(s -> !s.isForegroundLayer())
                        .reduce(StyleClass::merge)
                        .orElse(null);

                var foreground = styleClasses.stream()
                        .filter(StyleClass::isForegroundLayer)
                        .reduce(StyleClass::merge)
                        .orElse(null);

                if(background != null){
                    backgroundLayers.add(background);
                }

                if(foreground != null){
                    foregroundLayers.add(foreground);
                }

            }

            var componentBounds = ui.getLayout().getSceneBounds(component);
            backgroundLayers.forEach(style -> {
                renderer.render(getCanvas(), componentBounds, style, renderVars, styleLookup);
            });

            if(component.clipChildren().get(false)){
                log.log(FINE, "Enter clipping context for {0}: {1}", new Object[]{component, componentBounds});
                renderer.enterClippingRegion(componentBounds);
            }

            renderChildren(ui, component, uiSkin);

            if(component.clipChildren().get(false)){
                log.log(FINE, "Exit clipping context for {0}: {1}", new Object[]{component, componentBounds});
                renderer.exitClippingRegion();
            }

            foregroundLayers.forEach(style -> {
                renderer.render(getCanvas(), componentBounds, style, renderVars, styleLookup);
            });

        }else{
            if(!component.isLayoutOnly()){
                log.log(WARNING, "Cannot render component {0} because no skin is present", component.styleName().get());
            }else{
                renderer.renderDebug(getCanvas(), ui.getLayout().getSceneBounds(component));
            }
            renderChildren(ui, component, uiSkin);
        }

    }

    protected void renderChildren(ObsidianUI ui, Component component, UISkin uiSkin){
        if(RenderOrder.ASCENDING.equals(component.renderOrder().get(RenderOrder.ASCENDING))){
            for(var c : component.children()){
                render(ui, c, uiSkin);
            }
        }else{
            for(int i = component.children().size() - 1; i >= 0; i--){
                render(ui, component.children().get(i), uiSkin);
            }
        }
    }

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

    protected void loadFonts(UISkin skin){
        for(var path : skin.fonts()){
            try(var is = skin.getResolver().resolve(path)){

                try(var fontData = Data.makeFromBytes(is.readAllBytes())){
                    var typeface = Typeface.makeFromData(fontData);
                    if(!fonts.contains(typeface)){
                        renderer.registerFont(typeface);
                        fonts.add(typeface);
                    }else{
                        typeface.close();
                    }
                }
            }catch(Exception e){
                log.log(WARNING, "Failed to load font {0} for skin {1}: {2}", new Object[]{path, skin.getName(), e});
            }
        }
    }

    public ObsidianImage loadImage(InputStream is) throws Exception {
        return new ObsidianImage(Image.makeFromEncoded(is.readAllBytes()));
    }

    public ObsidianImage loadImage(UISkin skin, String path){
        try(var is = skin.getResolver().resolve(path)){
            return loadImage(is);
        }catch (Exception e){
            log.log(WARNING, "Failed to load image {0} for skin {1}: {2}. Check that the file exists and is not corrupt.", new Object[]{path, skin.getName(), e});
            return null;
        }
    }

    protected void loadImages(UISkin skin){
        for (var path : skin.images()) {
            var image = loadImage(skin, path);
            if (image == null) {
                continue;
            }
            skin.cache(path, image);
        }
    }

    protected Surface getSurface(){
        return surfaceManager.getSurface();
    }

    public ObsidianSvg loadSvg(InputStream is) throws Exception {
        try(var data = Data.makeFromBytes(is.readAllBytes())){
            return new ObsidianSvg(new SVGDOM(data));
        }
    }

    public ObsidianSvg loadSvg(UISkin skin, String path){
        try(var is = skin.getResolver().resolve(path)){
            return loadSvg(is);
        }catch(Exception e){
            log.log(WARNING, "Failed to load svg {0} for skin {1}: {2}. Check that the file exists and is not corrupt.", new Object[]{path, skin.getName(), e});
            return null;
        }
    }

    protected void loadSvgs(UISkin skin){
        for(var path : skin.svgs()){
            var svg = loadSvg(skin, path);
            if(svg == null){
                continue;
            }
            skin.cache(path, svg);
        }
    }

    public Typeset availableFonts(){
        return fonts;
    }

    public void enableRenderDebug(ColorRGBA debugColor){
        renderer.debugBoundsColor().set(debugColor);
    }

    public void disableRenderDebug(){
        renderer.debugBoundsColor().set(null);
    }

    public boolean isRenderDebugEnabled(){
        return renderer.debugBoundsColor().get() != null;
    }

    public void flush(){

        if(getSurface() != null){
            getSurface().flushAndSubmit(true);
        }

    }

    public TextRuler getTextRuler(String fontFamily, TextStyle style, float size){
        return renderer.getTextRuler(fontFamily, style, size);
    }

    public TextRuler getTextRuler(StyleClass textStyle, Variables v){
        return renderer.getTextRuler(textStyle, v);
    }

    @Override
    public void close() {
        surfaceManager.close();

        fonts.clear();
    }
}
