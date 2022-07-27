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

import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.scene.Component;
import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.Library;

public class DisplayEngine implements AutoCloseable {

    static {
        if("false".equals(System.getProperty("skija.staticLoad"))){
            Library.load(); // Load Skia lib if it's not statically linked
        }
    }

    protected final DirectContext context;
    protected final BackendRenderTarget renderTarget;
    protected final Surface surface;

    protected final ListChangeListener<Component> sceneListener;

    public static DisplayEngine createForGL(int width, int height, int framebufferHandle){
        var context = DirectContext.makeGL();
        var renderTarget = BackendRenderTarget.makeGL(
                width,
                height,
                0,
                8,
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

        return new DisplayEngine(context, renderTarget, surface);
    }

    public static DisplayEngine createForCpu(int width, int height){
        var surface = Surface.makeRaster(getImageInfo(width, height));

        return new DisplayEngine(null, null, surface);
    }

    protected DisplayEngine(DirectContext context, BackendRenderTarget renderTarget, Surface surface){
        this.context = context;
        this.renderTarget = renderTarget;
        this.surface = surface;

        sceneListener = this::onSceneChange;
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

    public void flush(){
        if(context != null){
            context.flush();
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
