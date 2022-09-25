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

import io.github.humbleui.types.IRect;
import myworld.obsidian.ObsidianUI;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.UISkin;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Dimension2D;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.impl.Library;
import myworld.obsidian.text.Typeset;

import java.util.*;
import java.lang.System.Logger.Level;

public class DisplayEngine implements AutoCloseable {

    private static final System.Logger log = System.getLogger(DisplayEngine.class.getName());

    static {
        if("false".equals(System.getProperty("skija.staticLoad"))){
            Library.load(); // Load Skia lib if it's not statically linked
        }
    }

    protected final ValueProperty<Dimension2D> dimensions;

    protected final DirectContext context;
    protected final BackendRenderTarget renderTarget;
    protected final Surface surface;

    protected final Typeset fonts;

    protected final ListChangeListener<Component> sceneListener;

    public static DisplayEngine createForGL(int width, int height, int framebufferHandle){
        var context = DirectContext.makeGL();
        var renderTarget = BackendRenderTarget.makeGL(
                width,
                height,
                0,
                0,
                framebufferHandle,
                FramebufferFormat.GR_GL_RGBA8);

        var surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getDisplayP3(),
                new SurfaceProps(PixelGeometry.RGB_H)
        );

        return new DisplayEngine(width, height, context, renderTarget, surface);
    }

    public static DisplayEngine createForCpu(int width, int height){
        var surface = Surface.makeRaster(getImageInfo(width, height));

        return new DisplayEngine(width, height, null, null, surface);
    }

    protected DisplayEngine(int width, int height, DirectContext context, BackendRenderTarget renderTarget, Surface surface){
        this.context = context;
        this.renderTarget = renderTarget;
        this.surface = surface;

        dimensions = new ValueProperty<>(new Dimension2D(width, height));

        sceneListener = this::onSceneChange;

        fonts = new Typeset();
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
        return surface.getCanvas();
    }

    public Image snapshot(){
        return surface.makeImageSnapshot();
    }

    public Image snapshot(Bounds2D area){
        return surface.makeImageSnapshot(
                new IRect(
                (int) area.left(),
                (int) area.top(),
                (int) area.right(),
                (int) area.bottom()
                ));
    }

    public Bitmap makeWritableBitmap(){
        return makeWritableBitmap(surface.getWidth(), surface.getHeight());
    }

    public Bitmap makeWritableBitmap(int width, int height){
        final var bytesPerRow = width * surface.getImageInfo().getBytesPerPixel();
        var data = new byte[bytesPerRow * height];
        return Bitmap.makeFromImage(Image.makeRaster(surface.getImageInfo(), data, bytesPerRow));
    }

    public Bitmap getSurfacePixels(Bitmap target){
        return getSurfacePixels(target, 0, 0);
    }

    public Bitmap getSurfacePixels(Bitmap target, int srcX, int srcY){
        surface.readPixels(target, srcX, srcY);
        return target;
    }

    protected static ImageInfo getImageInfo(int width, int height){
        var colorInfo = new ColorInfo(ColorType.RGBA_8888, ColorAlphaType.PREMUL, ColorSpace.getSRGB());
        return new ImageInfo(colorInfo, width, height);
    }

    protected ImageInfo getImageInfo(){
        return getImageInfo(surface.getWidth(), surface.getHeight());
    }

    public void render(ObsidianUI ui, Component component, UISkin uiSkin){

        var skin = uiSkin.getComponentSkin(component.styleName().get());

        if(skin != null){
            var renderVars = component.data();

            for(var layer : skin.layers()){
                var activeStates = skin.activeForLayer(layer.layer(), renderVars);
                var style = StyleClass.merge(layer, StyleClass.merge(activeStates));

                // Mix-in style imports - pull from component styles first, then skin styles
                List<String> styles = style.rule("styles");
                if(styles != null){
                    for(String mixName : styles){
                        var mixStyle = skin.findNamed(mixName);
                        if(mixStyle == null){
                            mixStyle = uiSkin.getStyle(mixName);
                        }

                        if(mixStyle != null){
                            style = StyleClass.merge(mixStyle, style);
                        }
                    }
                }

                Renderer.render(getCanvas(), ui.getLayout().getBounds(component), style, component.data());
            }

        }else{
            log.log(Level.WARNING, "Cannot render component {0} because no skin is present", component.styleName().get());
        }

        component.children().forEach((c) -> render(ui, c, uiSkin));
    }

    public void loadFonts(UISkin skin){
        for(var path : skin.fonts()){
            try(var is = skin.getResolver().resolve(path)){
                var fontData = Data.makeFromBytes(is.readAllBytes());
                var typeface = Typeface.makeFromData(fontData);
                fonts.add(typeface);
            }catch(Exception e){
                log.log(Level.WARNING, "Failed to load font {0} for skin {1}: {2}", path, skin.getName(), e);
            }
        }
    }

    public void clearFonts(){
        fonts.clear();
    }

    public Typeset availableFonts(){
        return fonts;
    }

    public void flush(){

        if(surface != null){
            surface.flushAndSubmit(true);
        }

    }

    @Override
    public void close() {
        if(renderTarget != null){
            renderTarget.close();
        }

        if(surface != null){
            surface.close();
        }

        if(context != null){
            context.close();
        }
    }
}
