package myworld.obsidian.display;

import io.github.humbleui.skija.*;

public class GLSurfaceManager implements SurfaceManager {

    protected DirectContext context;
    protected BackendRenderTarget renderTarget;
    protected Surface surface;

    protected final int samples;
    protected final int framebufferHandle;

    public GLSurfaceManager(int width, int height, int samples, int framebufferHandle){
        this.samples = samples;
        this.framebufferHandle = framebufferHandle;

        makeSurface(width, height);
    }

    protected void makeSurface(int width, int height){
        cleanResources();

        context = DirectContext.makeGL();
        renderTarget = BackendRenderTarget.makeGL(
                width,
                height,
                samples,
                0,
                framebufferHandle,
                FramebufferFormat.GR_GL_RGBA8);

        surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getDisplayP3(),
                new SurfaceProps(PixelGeometry.RGB_H)
        );
    }

    protected void cleanResources(){
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

    @Override
    public void resize(int width, int height) {
        makeSurface(width, height);
    }

    @Override
    public Surface getSurface() {
        return surface;
    }

    @Override
    public void close() {
        cleanResources();
    }
}
